package pl.project.benchmark.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "pl.project.benchmark")
@EntityScan("pl.project.benchmark.common.persistence")
@EnableJpaRepositories("pl.project.benchmark.common.repository")
public class GraphqlApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphqlApplication.class, args);
    }
}
