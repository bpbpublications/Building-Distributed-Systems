public class OrderProcessor {
    public OrderResult processOrder(Order order) {
        TransactionContext ctx = transactionManager.begin();
        try {
            // Check inventory availability
            InventoryStatus status = inventoryService.checkAndReserve(
                order.getItems(),
                ctx.getTransactionId()
            );

            if (!status.isAvailable()) {
                throw new InsufficientInventoryException();
            }

            // Process payment
            PaymentResult payment = paymentService.processPayment(
                order.getPaymentDetails(),
                order.getTotalAmount(),
                ctx.getTransactionId()
            );

            if (!payment.isSuccessful()) {
                throw new PaymentFailedException();
            }

            // Create order record
            OrderRecord record = orderRepository.createOrder(
                order,
                status.getReservationId(),
                payment.getTransactionId(),
                ctx.getTransactionId()
            );

            // Commit the transaction
            ctx.commit();

            return OrderResult.success(record.getOrderId());
        } catch (Exception e) {
            // Rollback on any failure
            ctx.rollback();
            return OrderResult.failure(e);
        }
    }
}
