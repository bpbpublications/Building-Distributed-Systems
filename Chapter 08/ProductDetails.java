public ProductDetails getProductDetails(String productId) {
    // DON'T DO THIS
    ProductBasicInfo basic = productService.getBasicInfo(productId);
    PricingInfo pricing = pricingService.getCurrentPrice(productId);
    InventoryStatus inventory = inventoryService.getStatus(productId);
    return new ProductDetails(basic, pricing, inventory);
}
