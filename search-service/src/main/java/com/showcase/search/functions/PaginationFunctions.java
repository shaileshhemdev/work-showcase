package com.showcase.search.functions;

public final class PaginationFunctions {
    /**
     * This function will calculate the start position
     *
     * @param <PageNumber>    The current page number
     * @param <PageSize>      The size of the page
     * @param <StartPosition> The start position
     */
    interface CalculateStartPosition<PageNumber, PageSize, StartPosition> {
        /** */
        StartPosition apply(PageNumber pageNumber, PageSize pageSize);
    }

    /**
     * Calculates whether there are more pages
     *
     * @param <TotalRecords> The total number of records
     * @param <PageNumber>   The current page number
     * @param <PageSize>     The size of records per page
     * @param <HasMore>      Whether there are more pages to browse
     */
    interface CalculateHasMore<TotalRecords, PageNumber, PageSize, HasMore> {
        /** */
        HasMore apply(TotalRecords totalRecords, PageNumber pageNumber, PageSize pageSize);
    }

    /**
     * Determine the start of page
     *
     * @param pageNumberParam The current page number
     * @param pageSizeParam   The size of records per page
     * @return
     */
    public static int calculateStartPosition(int pageNumberParam, int pageSizeParam) {
        CalculateStartPosition<Integer, Integer, Integer> startRecordPosition = (pageNumber, pageSize) -> {
            /**
             * First find the records returned and following is the algorithm
             * a) If current pageNumber is 0 or less then we make previousPageNumber to 0
             * b) Else previous page number is one less than current page
             * */
            int previousPageNumber = pageNumber <= 0 ? 0 : (pageNumber - 1);

            /**
             * a) If previous pageNumber is 0 then it means that lastRecordCounter is 1 less than the page Size
             * b) If not lastRecordCounter is previousPageNumber times pageSize plus page Size (for 0 index) + 1
             */
            int lastRecordCounter = previousPageNumber <= 0 ? pageSize - 1 : (previousPageNumber * pageSize) + pageSize - 1;

            /**
             * Once last record counter has been determined, the start record for this search is 1 more than last Record counter.
             * For current page number this is 0
             * */
            int startRecord = pageNumber <= 0 ? 0 : lastRecordCounter + 1;

            return startRecord;
        };

        return startRecordPosition.apply(pageNumberParam, pageSizeParam);
    }


    /**
     * Provides the implementation to determine if there are more pages
     *
     * @param totalRecordsParam The total number of records matching the search
     * @param pageNumberParam   The current page number
     * @param pageSizeParam     The size of individual pages
     * @return
     */
    public static boolean hasMorePages(int totalRecordsParam, int pageNumberParam, int pageSizeParam) {
        CalculateHasMore<Integer, Integer, Integer, Boolean> hasMoreFunction = (totalRecords, pageNumber, pageSize) -> {
            /** Result to determine pages */
            boolean hasMore = false;

            /**
             * First calculate total number of pages. Total number of pages is calculated as follows
             *
             * 1) First get the result of dividing total number of records by the page size
             * 2) For the base case if total number of records is less than or equal to page size then we know
             *      totalNumberOfPages is 0 given that page numbers are 0 based
             * 3) TotalNumberOfPages needs to be incremented by 1 if the result in Step 1 has a remainder. However since
             *      pageNumbers are zero based we want to decrement by 1. So we can do that only for the case of remainder being 0
             * */
            int totalNumberOfPages = totalRecords / pageSize;
            if (totalRecords <= pageSize) {
                totalNumberOfPages = 0;
            } else if (totalRecords > pageSize && totalRecords % pageSize == 0) {
                totalNumberOfPages--;
            }

            /** If the current page number is less than total number of pages then it means we have more pages */
            if (pageNumber < totalNumberOfPages) {
                hasMore = true;
            }

            return new Boolean(hasMore);
        };

        return hasMoreFunction.apply(totalRecordsParam, pageNumberParam, pageSizeParam);
    }
}
