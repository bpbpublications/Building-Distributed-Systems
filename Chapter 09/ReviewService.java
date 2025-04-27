public class ReviewService {
    public void submitReview(ProductReview review) {
        // Write to primary storage
        reviewRepository.save(review);

        // Asynchronously update search index and analytics
        eventPublisher.publish(new ReviewSubmittedEvent(review));

        // Update product rating asynchronously
        asyncTaskExecutor.execute(() ->
            updateProductAggregates(review.getProductId())
        );
    }

    private void updateProductAggregates(String productId) {
        try {
            // Recalculate product rating
            List<ProductReview> reviews =
                reviewRepository.findByProductId(productId);

            double averageRating = calculateAverageRating(reviews);

            // Update product rating in cache and database
            productService.updateRating(productId, averageRating);
        } catch (Exception e) {
            // Queue for retry in case of failure
            retryQueue.add(new RatingUpdateTask(productId));
        }
    }
}
