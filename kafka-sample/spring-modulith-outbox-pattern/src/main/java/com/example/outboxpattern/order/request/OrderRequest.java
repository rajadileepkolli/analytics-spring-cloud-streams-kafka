package com.example.outboxpattern.order.request;

import jakarta.validation.constraints.NotEmpty;

public record OrderRequest(@NotEmpty(message = "Product cannot be empty") String product) {}
