package com.myorg.orderservice.controller;

import com.myorg.orderservice.dto.OrderRequest;
import com.myorg.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name="inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name="inventory")
    @Retry(name="inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest order) {
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(order));
    }

    public CompletableFuture<String> fallbackMethod(OrderRequest order, RuntimeException e) {
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong! " + e.getMessage());
    }
}
