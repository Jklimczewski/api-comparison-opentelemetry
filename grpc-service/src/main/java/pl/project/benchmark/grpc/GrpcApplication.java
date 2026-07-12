package pl.project.benchmark.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "pl.project.benchmark")
@EntityScan("pl.project.benchmark.common.persistence")
@EnableConfigurationProperties()
@EnableJpaRepositories("pl.project.benchmark.common.repository")
public class GrpcApplication {
    public static void main(String[] args) {
        SpringApplication.run(GrpcApplication.class, args);
    }
}
