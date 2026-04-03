package com.payflow.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
   private final Map<String, Bucket> cache=new ConcurrentHashMap<>();
   public Bucket resolveBucket(String key)
   {
       return cache.computeIfAbsent(key,k->Bucket.builder().addLimit(Bandwidth.simple(5, Duration.ofSeconds(1))).build());
   }
}
