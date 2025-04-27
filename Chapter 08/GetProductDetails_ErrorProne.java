/**
This is an error prone implementation
This code will fail if any service is unavailable, leading to complete failure of the product detail page.
**/
public ProductDetails getProductDetails(String productId) {
    // DON'T DO THIS
    ProductBasicInfo basic = productService.getBasicInfo(productId);
    PricingInfo pricing = pricingService.getCurrentPrice(productId);
    InventoryStatus inventory = inventoryService.getStatus(productId);
    return new ProductDetails(basic, pricing, inventory);
}
