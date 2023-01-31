package com.lucas.springbootpostgres.repositories;

import com.lucas.springbootpostgres.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
