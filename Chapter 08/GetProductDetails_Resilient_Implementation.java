/**
This is a more resilient implementation because it considers failure scenarios
This implementation includes:
•	Parallel execution of requests
•	Retry logic with exponential backoff
•	Circuit breaker pattern
•	Fallback mechanisms
•	Timeout handling

**/

public ProductDetails getProductDetails(String productId) {
    ProductDetails details = new ProductDetails();

    // Use CompletableFuture for parallel execution
    CompletableFuture<ProductBasicInfo> basicInfoFuture = CompletableFuture
        .supplyAsync(() -> getBasicInfoWithRetry(productId))
        .exceptionally(ex -> getFallbackBasicInfo(productId));

    CompletableFuture<PricingInfo> pricingFuture = CompletableFuture
        .supplyAsync(() -> getPricingWithRetry(productId))
        .exceptionally(ex -> getFallbackPricing(productId));

    CompletableFuture<InventoryStatus> inventoryFuture = CompletableFuture
        .supplyAsync(() -> getInventoryWithRetry(productId))
        .exceptionally(ex -> getFallbackInventory(productId));

    // Combine results with timeouts
    try {
        details.setBasicInfo(basicInfoFuture.get(1, TimeUnit.SECONDS));
        details.setPricing(pricingFuture.get(1, TimeUnit.SECONDS));
        details.setInventory(inventoryFuture.get(1, TimeUnit.SECONDS));
    } catch (TimeoutException e) {
        // Handle timeouts gracefully
        handleTimeout(details);
    }

    return details;
}

private ProductBasicInfo getBasicInfoWithRetry(String productId) {
    return new RetryTemplate()
        .withExpBackoff(100, 3) // Start at 100ms, try 3 times
        .withCircuitBreaker(5, 60000) // Break after 5 failures, reset after 60s
        .execute(() -> productService.getBasicInfo(productId));
}
