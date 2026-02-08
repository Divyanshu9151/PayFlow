package com.payflow.repository;

import com.payflow.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyKey,Long> {
    Optional<IdempotencyKey>findByIdempotencyKey(String idempotencyKey);
}
