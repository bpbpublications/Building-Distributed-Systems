# Without read-through caching
def get_product(product_id):
    product = cache.get(product_id)
    if not product:
        product = database.get(product_id)
        cache.set(product_id, product)
    return product

# With read-through caching
def get_product(product_id):
    return cache_client.get(product_id)  # Cache handles DB interaction if needed
