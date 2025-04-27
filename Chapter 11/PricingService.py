class PricingService:
    def update_price(self, product_id, new_price):
        # Update database first
        success = database.update_price(product_id, new_price)
        if success:
            # Then update cache
            cache.set(f"price:{product_id}", new_price)
            # Notify other services
            event_bus.publish("price_updated", {
                "product_id": product_id,
                "new_price": new_price
            })
        return success
