package com.marketplace.repository;

import com.marketplace.entity.Product;
import com.marketplace.entity.Review;
import com.marketplace.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProduct(Product product, Pageable pageable);
    Page<Review> findByUser(User user, Pageable pageable);
    boolean existsByIdAndUser(Long id, User user);
} 