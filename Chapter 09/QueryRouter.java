public class QueryRouter {
    public SearchResult executeSearch(SearchCriteria criteria) {
        if (isLocalizedSearch(criteria)) {
            // Query single shard for region-specific searches
            return executeLocalizedSearch(criteria);
        } else {
            // For global searches, fan out to multiple shards
            return executeDistributedSearch(criteria);
        }
    }

    private SearchResult executeDistributedSearch(SearchCriteria criteria) {
        // Parallelize queries across relevant shards
        List<Future<PartialResult>> futureResults =
            relevantShards.stream()
                .map(shard -> executorService.submit(
                    () -> queryShard(shard, criteria)))
                .collect(Collectors.toList());

        // Gather and merge results
        return mergeResults(futureResults);
    }
}
