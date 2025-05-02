package com.marketplace.repository;

import com.marketplace.entity.Order;
import com.marketplace.entity.OrderStatus;
import com.marketplace.entity.Store;
import com.marketplace.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByStore(Store store, Pageable pageable);
} 