import { check } from "k6";
import { Rate, Trend } from "k6/metrics";

export const errorRate = new Rate("errors");

export const responseBytes = new Trend("response_body_bytes", true);

export function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * Checks that an HTTP response has a 2xx status and records the result in the
 * shared error-rate metric.
 *
 * @param {Object} res   - k6 HTTP response object
 * @param {string} name  - label used in check names
 * @returns {boolean}      true when all checks pass
 */
export function checkHttp(res, name) {
  const ok = check(res, {
    [`${name}: status 2xx`]: (r) => r.status >= 200 && r.status < 300,
  });
  errorRate.add(!ok);
  return ok;
}

export function checkHttpOrNotFound(res, name) {
  const isServerError =
    res.status < 200 || (res.status >= 300 && res.status !== 404);
  const ok = check(res, {
    [`${name}: status 2xx or 404`]: (r) =>
      (r.status >= 200 && r.status < 300) || r.status === 404,
  });
  errorRate.add(isServerError);
  return ok;
}

export function jsonHeaders() {
  return { "Content-Type": "application/json" };
}
