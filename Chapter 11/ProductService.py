# Simplified product service showcasing different caching approaches
class ProductService:
    def get_product_details(self, product_id):
        # Cache with long TTL - Product details rarely change
        cached_details = l2_cache.get(f"product:{product_id}")
        if cached_details:
            return cached_details

        details = database.get_product(product_id)
        l2_cache.set(f"product:{product_id}", details, ttl=HOURS_24)
        return details

    def get_current_price(self, product_id):
        # Cache with shorter TTL - Prices change more frequently
        cached_price = l1_cache.get(f"price:{product_id}")
        if cached_price:
            return cached_price

        price = pricing_service.get_price(product_id)
        l1_cache.set(f"price:{product_id}", price, ttl=MINUTES_15)
        return price

    def get_inventory_level(self, product_id):
        # Minimal caching - Inventory needs to be near real-time
        return inventory_service.get_current_stock(product_id)
