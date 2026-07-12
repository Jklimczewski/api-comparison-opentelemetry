import json
import statistics
from datetime import datetime
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import numpy as np

BASE_DIR = Path(__file__).parent
RESULTS_DIR = BASE_DIR / "results"
OUTPUT_DIR = BASE_DIR / "plots"
OUTPUT_DIR.mkdir(exist_ok=True)
plt.rcParams.update(
    {
        "font.family": "serif",
        "font.serif": ["DejaVu Serif", "Times New Roman", "Palatino"],
        "font.size": 10,
        "axes.labelsize": 10,
        "axes.titlesize": 11,
        "xtick.labelsize": 9,
        "ytick.labelsize": 9,
        "legend.fontsize": 9,
        "figure.dpi": 150,
        "savefig.dpi": 300,
        "savefig.bbox": "tight",
        "savefig.pad_inches": 0.08,
        "axes.grid": True,
        "grid.alpha": 0.35,
        "grid.linestyle": "--",
        "axes.spines.top": False,
        "axes.spines.right": False,
        "axes.axisbelow": True,
    }
)

PROTO_COLORS = {"REST": "#1565C0", "GraphQL": "#E65100", "gRPC": "#2E7D32"}
OTEL_COLORS = {"off": "#455A64", "on-100": "#C62828"}

OP_LABELS = {
    "getUser": "GetUser",
    "getBooks": "GetBooks",
    "getOrder": "GetOrder",
    "createOrder": "CreateOrder",
    "user": "GetUser",
    "books": "GetBooks",
    "order": "GetOrder",
    "GetUser": "GetUser",
    "GetBooks": "GetBooks",
    "GetOrder": "GetOrder",
    "CreateOrder": "CreateOrder",
}
OP_ORDER = ["GetUser", "GetBooks", "GetOrder", "CreateOrder"]

def _find_result_file(pattern: str) -> Path:
    matches = list(RESULTS_DIR.glob(pattern))
    if not matches:
        raise FileNotFoundError(f"No result file found matching pattern: {pattern}")
    return sorted(matches)[-1]

FILES = {
    ("GraphQL", "off"): lambda: _find_result_file("off_graphql_*.json"),
    ("REST", "off"): lambda: _find_result_file("off_rest_*.json"),
    ("gRPC", "off"): lambda: _find_result_file("off_grpc_*.json"),
    ("GraphQL", "on-100"): lambda: _find_result_file("on-100_graphql_*.json"),
    ("REST", "on-100"): lambda: _find_result_file("on-100_rest_*.json"),
    ("gRPC", "on-100"): lambda: _find_result_file("on-100_grpc_*.json"),
    ("payload", "off"): lambda: _find_result_file("off_payload_*.json"),
}

DUR_METRIC = {"GraphQL": "http_req_duration", "REST": "http_req_duration", "gRPC": "grpc_req_duration"}
OP_TAG = {"GraphQL": "operation", "REST": "endpoint", "gRPC": "method"}

def _is_test_point(tags: dict) -> bool:
    grp = tags.get("group", "")
    return "setup" not in grp and "teardown" not in grp

def load_latencies(path: Path, metric: str, tag_key: str) -> dict[str, list[float]]:
    data: dict[str, list] = {}
    with open(path, encoding="utf-8") as fh:
        for line in fh:
            obj = json.loads(line)
            if obj.get("type") != "Point" or obj.get("metric") != metric:
                continue
            tags = obj["data"].get("tags", {})
            if not _is_test_point(tags):
                continue
            raw_op = tags.get(tag_key, "")
            op = OP_LABELS.get(raw_op, raw_op)
            data.setdefault(op, []).append(obj["data"]["value"])
    return data


def load_network(path: Path) -> tuple[float, float]:
    recv = sent = 0.0
    with open(path, encoding="utf-8") as fh:
        for line in fh:
            obj = json.loads(line)
            if obj.get("type") != "Point":
                continue
            tags = obj["data"].get("tags", {})
            if not _is_test_point(tags):
                continue
            m = obj.get("metric")
            if m == "data_received":
                recv += obj["data"]["value"]
            elif m == "data_sent":
                sent += obj["data"]["value"]
    return recv / 1024, sent / 1024


def load_throughput(path: Path, dur_metric: str) -> float:
    times = []
    with open(path, encoding="utf-8") as fh:
        for line in fh:
            obj = json.loads(line)
            if obj.get("type") != "Point" or obj.get("metric") != dur_metric:
                continue
            tags = obj["data"].get("tags", {})
            if not _is_test_point(tags):
                continue
            t = datetime.fromisoformat(obj["data"]["time"].replace("Z", "+00:00"))
            times.append(t)
    if len(times) < 2:
        return 0.0
    times.sort()
    duration = (times[-1] - times[0]).total_seconds()
    return len(times) / duration if duration > 0 else 0.0


def load_timeseries(path: Path, metric: str, bucket_secs: int = 5) -> tuple[list, list, list]:
    raw: list[tuple[float, float]] = []
    with open(path, encoding="utf-8") as fh:
        for line in fh:
            obj = json.loads(line)
            if obj.get("type") != "Point" or obj.get("metric") != metric:
                continue
            tags = obj["data"].get("tags", {})
            if not _is_test_point(tags):
                continue
            t = datetime.fromisoformat(obj["data"]["time"].replace("Z", "+00:00"))
            raw.append((t, obj["data"]["value"]))
    if not raw:
        return [], [], []
    raw.sort()
    t0 = raw[0][0]
    buckets: dict[int, list] = {}
    for t, v in raw:
        b = int((t - t0).total_seconds() / bucket_secs) * bucket_secs
        buckets.setdefault(b, []).append(v)
    times = sorted(buckets)
    p50 = [statistics.median(buckets[t]) for t in times]
    p95 = [sorted(buckets[t])[max(0, int(len(buckets[t]) * 0.95) - 1)] for t in times]
    return times, p50, p95


def load_payload_bytes(path: Path) -> dict[str, dict[str, list[float]]]:
    data: dict[str, dict] = {}
    with open(path, encoding="utf-8") as fh:
        for line in fh:
            obj = json.loads(line)
            if obj.get("type") != "Point" or obj.get("metric") != "response_body_bytes":
                continue
            tags = obj["data"].get("tags", {})
            proto = tags.get("protocol", "unknown").upper()
            tier = tags.get("payload", "unknown")
            data.setdefault(proto, {}).setdefault(tier, []).append(obj["data"]["value"])
    return data


def percentile(data: list[float], p: float) -> float:
    s = sorted(data)
    idx = max(0, int(len(s) * p / 100) - 1)
    return s[idx]

def save(fig: plt.Figure, name: str) -> None:
    for ext in ("pdf", "png"):
        out = OUTPUT_DIR / f"{name}.{ext}"
        fig.savefig(out)
    print(f"  Saved: {name}.pdf / .png")
    plt.close(fig)

def fig_latency_by_operation() -> None:
    print("Generating fig 1: latency by operation …")

    protocols = ["REST", "GraphQL", "gRPC"]
    stats: dict[str, dict] = {}

    for proto in protocols:
        path = FILES[(proto, "off")]()
        lat = load_latencies(path, DUR_METRIC[proto], OP_TAG[proto])
        stats[proto] = {}
        for op in OP_ORDER:
            if op in lat:
                vals = lat[op]
                stats[proto][op] = {
                    "mean": statistics.mean(vals),
                    "p95": percentile(vals, 95),
                    "p99": percentile(vals, 99),
                }

    x = np.arange(len(OP_ORDER))
    width = 0.26
    offsets = [-width, 0, width]

    fig, axes = plt.subplots(1, 2, figsize=(7.2, 3.6), sharey=False)

    for i, (proto, offset) in enumerate(zip(protocols, offsets)):
        for ax_idx, stat_key in enumerate(("mean", "p95")):
            ax = axes[ax_idx]
            vals = [stats[proto].get(op, {}).get(stat_key, 0) for op in OP_ORDER]
            bars = ax.bar(x + offset, vals, width, label=proto,
                          color=PROTO_COLORS[proto], alpha=0.85, zorder=3)

    for ax, title in zip(axes, ("Średnia latencja [ms]", "p95 latencja [ms]")):
        ax.set_ylabel(title)
        ax.set_xticks(x)
        ax.set_xticklabels(OP_ORDER, rotation=15, ha="right")
        ax.set_ylim(bottom=0)

    axes[0].set_title("(a) Średnia latencja")
    axes[1].set_title("(b) Latencja p95")

    handles = [mpatches.Patch(color=PROTO_COLORS[p], label=p) for p in protocols]
    fig.legend(handles=handles, loc="upper center", ncol=3,
               frameon=True, bbox_to_anchor=(0.5, 1.02), fontsize=8)
    fig.tight_layout()
    save(fig, "fig1_latency_by_operation")

def fig_network_bytes() -> None:
    print("Generating fig 2: network bytes …")

    protocols = ["REST", "GraphQL", "gRPC"]
    recv_kb = []
    sent_kb = []
    for proto in protocols:
        r, s = load_network(FILES[(proto, "off")]())
        recv_kb.append(r)
        sent_kb.append(s)

    x = np.arange(len(protocols))
    width = 0.35

    fig, ax = plt.subplots(figsize=(5.5, 3.8))
    bars1 = ax.bar(x - width / 2, [r / 1024 for r in recv_kb], width,
                   label="Odebrane", color="#1565C0", alpha=0.85, zorder=3)
    bars2 = ax.bar(x + width / 2, [s / 1024 for s in sent_kb], width,
                   label="Wysłane", color="#E65100", alpha=0.85, zorder=3)

    for bars in (bars1, bars2):
        for bar in bars:
            h = bar.get_height()
            ax.text(bar.get_x() + bar.get_width() / 2, h + 0.2,
                    f"{h:.0f}", ha="center", va="bottom", fontsize=8)

    ax.set_xticks(x)
    ax.set_xticklabels(protocols)
    ax.set_ylabel("Dane sieciowe [MB]")
    ax.set_title("Transfery sieciowe per protokół (OTel wyłączony)")
    ax.set_ylim(bottom=0)
    ax.legend()
    fig.tight_layout()
    save(fig, "fig2_network_bytes")

def fig_payload_comparison() -> None:
    print("Generating fig 3: payload comparison …")

    payload_data = load_payload_bytes(FILES[("payload", "off")]())

    tiers = {
        "small": "Mały\n(~60-80 B)",
        "medium": "Średni\n(~250-600 B)",
        "large-ids": "Duży\n(ograniczone pola)\n(~1 KB)",
        "large": "Duży\n(pełna odpowiedź)\n(~6 KB)",
    }
    graphql_payloads = payload_data.get("GRAPHQL", {})
    tiers_present = [tier for tier in tiers if tier in graphql_payloads]

    x = np.arange(len(tiers_present))

    fig, ax = plt.subplots(figsize=(7.2, 4.0))

    means = []
    for tier in tiers_present:
        vals = graphql_payloads.get(tier, [])
        means.append(statistics.mean(vals) if vals else 0)
    ax.bar(x, means, width=0.55, color=PROTO_COLORS["GraphQL"], alpha=0.85, zorder=3)

    ax.set_yscale("log")
    ax.set_xticks(x)
    ax.set_xticklabels([tiers[t] for t in tiers_present], fontsize=8)
    ax.set_ylabel("Rozmiar odpowiedzi [bajty] (skala log)")
    ax.set_title("GraphQL - porównanie rozmiarów ładunków")
    fig.tight_layout()
    save(fig, "fig3_payload_comparison")

def fig_otel_overhead() -> None:
    print("Generating fig 4: OTel overhead …")

    protocols = ["REST", "GraphQL", "gRPC"]
    modes = ["off", "on-100"]

    p95s: dict[tuple, float] = {}

    for proto in protocols:
        for mode in modes:
            path = FILES[(proto, mode)]()
            lat = load_latencies(path, DUR_METRIC[proto], OP_TAG[proto])
            all_vals = [v for vals in lat.values() for v in vals]
            p95s[(proto, mode)] = percentile(all_vals, 95) if all_vals else 0

    x = np.arange(len(protocols))
    width = 0.35

    mode_labels = {"off": "off", "on-100": "on"}

    fig, ax = plt.subplots(figsize=(5.5, 3.8))

    for i, (mode, hatch) in enumerate(zip(modes, ["", "//"])):
        vals = [p95s[(p, mode)] for p in protocols]
        bars = ax.bar(
            x + (i - 0.5) * width,
            vals,
            width,
            label=f"OTel {mode_labels[mode]}",
            color=[OTEL_COLORS[mode]] * len(protocols),
            hatch=hatch,
            edgecolor="white",
            alpha=0.85,
            zorder=3,
        )
        for bar, val in zip(bars, vals):
            ax.text(
                bar.get_x() + bar.get_width() / 2,
                bar.get_height() + 0.1,
                f"{val:.1f}",
                ha="center",
                va="bottom",
                fontsize=7.5,
            )

    ax.set_xticks(x)
    ax.set_xticklabels(protocols)
    ax.set_ylabel("p95 latencja [ms]")
    ax.set_ylim(bottom=0)

    handles = [mpatches.Patch(color=OTEL_COLORS[m], label=f"OTel {mode_labels[m]}") for m in modes]
    ax.legend(handles=handles)
    ax.set_title("Narzut instrumentacji OpenTelemetry p95")
    fig.tight_layout()
    save(fig, "fig4_otel_overhead")

def fig_percentile_summary() -> None:
    print("Generating fig 5: percentile summary …")

    protocols = ["REST", "GraphQL", "gRPC"]
    percentiles = [50, 90, 95, 99]
    pct_labels = ["p50", "p90", "p95", "p99"]

    results: dict[str, list[float]] = {}
    for proto in protocols:
        path = FILES[(proto, "off")]()
        lat = load_latencies(path, DUR_METRIC[proto], OP_TAG[proto])
        all_vals = sorted(v for vals in lat.values() for v in vals)
        results[proto] = [all_vals[max(0, int(len(all_vals) * p / 100) - 1)] for p in percentiles]

    x = np.arange(len(pct_labels))
    width = 0.26
    offsets = [-width, 0, width]

    fig, ax = plt.subplots(figsize=(6.0, 3.8))
    for proto, offset in zip(protocols, offsets):
        bars = ax.bar(x + offset, results[proto], width,
                      label=proto, color=PROTO_COLORS[proto], alpha=0.85, zorder=3)
        for bar, val in zip(bars, results[proto]):
            ax.text(bar.get_x() + bar.get_width() / 2, bar.get_height() + 0.1,
                    f"{val:.1f}", ha="center", va="bottom", fontsize=7)

    ax.set_xticks(x)
    ax.set_xticklabels(pct_labels)
    ax.set_ylabel("Latencja [ms]")
    ax.set_title("Percentyle latencji - podsumowanie (OTel wyłączony)")
    ax.set_ylim(bottom=0)
    ax.legend()
    fig.tight_layout()
    save(fig, "fig5_percentile_summary")

def fig_bytes_per_request() -> None:
    print("Generating fig 6: bytes per request …")

    protocols = ["REST", "GraphQL", "gRPC"]
    recv_per_req: list[float] = []
    sent_per_req: list[float] = []

    for proto in protocols:
        r_kb, s_kb = load_network(FILES[(proto, "off")]())
        rps = load_throughput(FILES[(proto, "off")](), DUR_METRIC[proto])
        lat = load_latencies(FILES[(proto, "off")](), DUR_METRIC[proto], OP_TAG[proto])
        n = sum(len(v) for v in lat.values())
        recv_per_req.append((r_kb * 1024) / n if n else 0)
        sent_per_req.append((s_kb * 1024) / n if n else 0)

    x = np.arange(len(protocols))
    width = 0.35

    fig, ax = plt.subplots(figsize=(5.5, 3.8))
    bars1 = ax.bar(x - width / 2, recv_per_req, width, label="Odebrane/żądanie",
                   color="#1565C0", alpha=0.85, zorder=3)
    bars2 = ax.bar(x + width / 2, sent_per_req, width, label="Wysłane/żądanie",
                   color="#E65100", alpha=0.85, zorder=3)

    for bars in (bars1, bars2):
        for bar in bars:
            h = bar.get_height()
            ax.text(bar.get_x() + bar.get_width() / 2, h + 0.2,
                    f"{h:.1f}", ha="center", va="bottom", fontsize=8)

    ax.set_xticks(x)
    ax.set_xticklabels(protocols)
    ax.set_ylabel("Bajty na żądanie [B]")
    ax.set_title("Efektywność sieciowa - bajty per żądanie (OTel wyłączony)")
    ax.set_ylim(bottom=0)
    ax.legend()
    fig.tight_layout()
    save(fig, "fig6_bytes_per_request")

def fig_otel_overhead_pct() -> None:
    print("Generating fig 7: OTel overhead %")

    protocols = ["REST", "GraphQL", "gRPC"]
    stats_keys = ["p50", "p95", "p99"]

    overhead: dict[str, dict[str, float]] = {p: {} for p in protocols}

    for proto in protocols:
        lat_off = load_latencies(FILES[(proto, "off")](), DUR_METRIC[proto], OP_TAG[proto])
        lat_on = load_latencies(FILES[(proto, "on-100")](), DUR_METRIC[proto], OP_TAG[proto])
        off_vals = sorted(v for vals in lat_off.values() for v in vals)
        on_vals = sorted(v for vals in lat_on.values() for v in vals)
        if not off_vals or not on_vals:
            continue
        for pct in (50, 95, 99):
            off_p = off_vals[max(0, int(len(off_vals) * pct / 100) - 1)]
            on_p = on_vals[max(0, int(len(on_vals) * pct / 100) - 1)]
            overhead[proto][f"p{pct}"] = (on_p - off_p) / off_p * 100 if off_p else 0

    x = np.arange(len(stats_keys))
    width = 0.26
    offsets = [-width, 0, width]

    fig, ax = plt.subplots(figsize=(6.0, 3.8))
    for proto, offset in zip(protocols, offsets):
        vals = [overhead[proto].get(k, 0) for k in stats_keys]
        bars = ax.bar(x + offset, vals, width, label=proto,
                      color=PROTO_COLORS[proto], alpha=0.85, zorder=3)
        for bar, val in zip(bars, vals):
            ax.text(bar.get_x() + bar.get_width() / 2,
                    bar.get_height() + (0.5 if val >= 0 else -2.5),
                    f"{val:+.0f}%", ha="center", va="bottom", fontsize=7)

    ax.axhline(0, color="#333", linewidth=0.8)
    ax.set_xticks(x)
    ax.set_xticklabels(["p50", "p95", "p99"])
    ax.set_ylabel("Wzrost latencji [%]")
    ax.set_title("Procentowy narzut instrumentacji OTel (on vs off)")
    ax.legend()
    fig.tight_layout()
    save(fig, "fig7_otel_overhead_pct")

if __name__ == "__main__":
    print(f"Output directory: {OUTPUT_DIR}\n")
    fig_latency_by_operation()
    fig_network_bytes()
    fig_payload_comparison()
    fig_otel_overhead()
    fig_percentile_summary()
    fig_bytes_per_request()
    fig_otel_overhead_pct()