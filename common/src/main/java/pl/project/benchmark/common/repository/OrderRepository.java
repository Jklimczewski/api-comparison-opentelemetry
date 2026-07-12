package pl.project.benchmark.common.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.project.benchmark.common.persistence.Order;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"user", "books"})
    Optional<Order> findDetailedById(Long id);
}
