package com.marketplace.repository;

import com.marketplace.entity.Product;
import com.marketplace.entity.Store;
import com.marketplace.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByStore(Store store, Pageable pageable);
    boolean existsByIdAndStoreOwner(Long id, User owner);
} 