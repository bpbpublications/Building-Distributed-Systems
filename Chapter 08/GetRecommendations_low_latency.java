/**
Each of these operations introduces latency, and the cumulative effect can significantly impact over performance.
Effective latency management requires:
•	Concurrent processing of independent operations
•	Prioritization of critical paths over optional features
•	Caching strategies at multiple levels
•	Dynamic timeout management based on system conditions
•	Graceful degradation of non-essential features

This example illustrates how to handle latency properly

**/
public List<Recommendation> getRecommendations(String userId) {
    // Use caching for relatively static data
    List<Product> trending = cache.get("trending", () ->
        analyticsService.getTrending(), Duration.ofMinutes(5));

    // Parallel execution of necessary real-time calls
    CompletableFuture<UserProfile> profileFuture = CompletableFuture
        .supplyAsync(() -> userService.getProfile(userId));

    CompletableFuture<List<Purchase>> historyFuture = CompletableFuture
        .supplyAsync(() -> orderService.getHistory(userId));

    // Wait for all data with timeout
    try {
        UserProfile profile = profileFuture.get(200, TimeUnit.MILLISECONDS);
        List<Purchase> history = historyFuture.get(200, TimeUnit.MILLISECONDS);

        // Calculate recommendations asynchronously
        return CompletableFuture
            .supplyAsync(() -> calculateRecommendations(profile, history, trending))
            .get(500, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
        return getFallbackRecommendations(userId);
    }
}
// Implement a circuit breaker for external service calls
@CircuitBreaker(name = "similarityService", fallbackMethod = "getFallbackSimilar")
private List<Product> getSimilarProducts(List<Purchase> history) {
    return similarityService.findSimilar(history);
}

private List<Product> getFallbackSimilar(List<Purchase> history, Exception e) {
    return cache.get("default_recommendations");
}
