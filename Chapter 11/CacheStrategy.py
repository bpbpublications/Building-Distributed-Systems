class CacheStrategy:
    def choose_cache_level(self, data_type):
        strategies = {
            "frequently_read": {
                "l1_ttl": HOURS_1,
                "l2_ttl": HOURS_24,
                "consistency": "eventual"
            },
            "frequently_updated": {
                "l1_ttl": MINUTES_5,
                "l2_ttl": HOURS_1,
                "consistency": "strong"
            },
            "session_data": {
                "l1_ttl": MINUTES_30,
                "l2_ttl": HOURS_4,
                "consistency": "eventual"
            }
        }
        return strategies.get(data_type, DEFAULT_STRATEGY)
