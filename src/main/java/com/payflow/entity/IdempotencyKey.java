package com.payflow.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name="idempotency_keys",uniqueConstraints = @UniqueConstraint(columnNames = "idempotency_key"))
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false,unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private String requestHash;

    private LocalDateTime createdAt=LocalDateTime.now();

    public IdempotencyKey() {
    }

    public IdempotencyKey(String key, String requestHash) {
        this.idempotencyKey = key;
        this.requestHash = requestHash;
    }


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }


    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
