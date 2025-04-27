class ReviewService:
    def submit_review(self, product_id, review_data):
        # Generate review ID
        review_id = generate_unique_id()

        # Add to cache and write queue
        cache.set(f"review:{review_id}", review_data)
        write_queue.push({
            "review_id": review_id,
            "product_id": product_id,
            "data": review_data
        })

        # Acknowledge submission immediately
        return review_id

    # Background process
    def process_write_queue(self):
        while True:
            batch = write_queue.get_batch(size=100)
            for review in batch:
                try:
                    database.save_review(review)
                    metrics.increment("review_write_success")
                except DatabaseError:
                    retry_queue.push(review)
                    metrics.increment("review_write_failure")
