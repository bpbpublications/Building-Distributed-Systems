class LocalCacheManager:
    def __init__(self):
        self.l1_cache = LRUCache(max_size=10000)  # Local cache
        self.l2_cache = DistributedCache()        # Remote shared cache

    def get_data(self, key):
        # Fast path: check local cache
        data = self.l1_cache.get(key)
        if data:
            metrics.increment("l1_cache_hit")
            return data

        # Slower path: check distributed cache
        data = self.l2_cache.get(key)
        if data:
            metrics.increment("l2_cache_hit")
            # Populate local cache for future requests
            self.l1_cache.set(key, data)
            return data

        # Slowest path: fetch from source
        metrics.increment("cache_miss")
        return None
