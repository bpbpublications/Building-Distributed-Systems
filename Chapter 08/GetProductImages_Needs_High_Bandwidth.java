/**
This simple implementation has several problems:
•	It returns all images at full resolution
•	There's no pagination
•	No consideration of client capabilities
•	No optimization for network conditions
**/
@GetMapping("/products/{id}/images")
public List<ProductImage> getProductImages(@PathVariable String id) {
    return productImageRepository.findAllByProductId(id);
}
