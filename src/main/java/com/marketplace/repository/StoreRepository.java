package com.marketplace.repository;

import com.marketplace.entity.Store;
import com.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByOwner(User owner);
    boolean existsByIdAndOwner(Long id, User owner);
} 