class CoherentCacheManager:
    def update_data(self, key, value):
        # Update shared cache first
        self.l2_cache.set(key, value)

        # Broadcast invalidation to all instances
        self.broadcast_invalidation(key, time.now())

        # Update local cache
        self.l1_cache.set(key, value)

    def handle_invalidation(self, message):
        key = message["key"]
        timestamp = message["timestamp"]

        if self.l1_cache.get_timestamp(key) < timestamp:
            self.l1_cache.invalidate(key)
            metrics.increment("cache_invalidation")
