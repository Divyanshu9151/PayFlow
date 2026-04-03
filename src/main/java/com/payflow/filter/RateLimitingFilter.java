package com.payflow.filter;

import com.payflow.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
//   ------OLD Custom Filter with basic Rate Limiting---------
//    private static final int MAX_REQUESTS = 10;
//    private static final long WINDOW_MS = 60_000;
//
//    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String ip = request.getRemoteAddr();
//        long now = System.currentTimeMillis();
//
//        RequestCounter counter = requestCounts.computeIfAbsent(
//                ip,
//                k -> new RequestCounter(0, now)
//        );
//
//        synchronized (counter) {
//            if (now - counter.startTime > WINDOW_MS) {
//                counter.count = 0;
//                counter.startTime = now;
//            }
//
//            counter.count++;
//
//            if (counter.count > MAX_REQUESTS) {
//                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//                response.getWriter().write("Too many requests");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private static class RequestCounter {
//        int count;
//        long startTime;
//
//        RequestCounter(int count, long startTime) {
//            this.count = count;
//            this.startTime = startTime;
//        }
//   ------Redis with Bucket4j---------

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String key=request.getRemoteAddr();
        Bucket bucket= rateLimiterService.resolveBucket(key);
        if(bucket.tryConsume(1))
        {
            filterChain.doFilter(request,response);
        }
        else {
            response.setStatus(429);
            response.getWriter().write("Too many request");
        }
    }
}
