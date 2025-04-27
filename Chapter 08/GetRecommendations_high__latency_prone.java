/**
Each of these operations introduces latency, and the cumulative effect can
significantly impact over performance. Effective latency management requires
**/

public List<Recommendation> getRecommendations(String userId) {
    UserProfile profile = userService.getProfile(userId);  // ~100ms
    List<Purchase> history = orderService.getHistory(userId);  // ~200ms
    List<Product> trending = analyticsService.getTrending();   // ~150ms
    List<Product> similar = similarityService.findSimilar(history);  // ~300ms

    // Total time: ~750ms + processing time
    return calculateRecommendations(profile, history, trending, similar);
}
