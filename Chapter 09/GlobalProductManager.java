public class GlobalProductManager {
    public void updateProduct(Product product) {
        // Update in primary region
        ProductUpdateResult primaryUpdate =
            primaryRegionManager.updateProduct(product);

        if (!primaryUpdate.isSuccessful()) {
            throw new ProductUpdateException("Primary update failed");
        }

        // Propagate to other regions asynchronously
        for (RegionManager region : secondaryRegions) {
            replicationQueue.submit(new ReplicationTask(
                product,
                primaryUpdate.getVersion(),
                region.getRegionId()
            ));
        }

        // Monitor replication progress
        replicationMonitor.track(
            primaryUpdate.getVersion(),
            secondaryRegions.size()
        );
    }

    @Scheduled(fixedRate = 5000)
    public void checkReplicationHealth() {
        // Monitor replication lag and health
        Map<String, ReplicationStatus> status =
            replicationMonitor.getReplicationStatus();

        for (Map.Entry<String, ReplicationStatus> entry :
             status.entrySet()) {
            if (entry.getValue().getLagSeconds() >
                acceptableReplicationLag) {
                alertingService.sendAlert(
                    new ReplicationLagAlert(entry.getKey())
                );
            }
        }
    }
}
