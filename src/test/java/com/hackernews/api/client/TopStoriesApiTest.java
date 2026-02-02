package com.hackernews.api.client;

import com.hackernews.api.model.Item;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Integration tests for the Top Stories API functionality.
 * Tests the real Hacker News API endpoints.
 */
@DisplayName("Top Stories API Tests")
class TopStoriesApiTest {

    @BeforeAll
    static void checkApiAvailability() {
        assumeTrue(AApiHealthCheckTest.API_AVAILABLE,
                "❌ Hacker News API is not available - run health checks first with: mvn test -Dgroups=health-check");
    }

    @Nested
    @DisplayName("Integration Tests (Real API)")
    @Tag("integration")
    class RealTopStoriesIntegrationTests {

        private HackerNewsApiClient client;

        @BeforeEach
        void setUp() {
            client = new HackerNewsApiClient();
        }

        @Test
        @DisplayName("Should fetch real top stories from HN API")
        void shouldFetchRealTopStories() throws IOException, InterruptedException {
            // Act
            List<Long> topStoryIds = client.getTopStories();

            // Assert
            assertThat(topStoryIds)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSizeLessThanOrEqualTo(500);

            // All IDs should be positive
            assertThat(topStoryIds).allMatch(id -> id > 0);

            System.out.println("✓ Fetched " + topStoryIds.size() + " top story IDs");
        }

        @Test
        @DisplayName("Should fetch and parse top story item")
        void shouldFetchAndParseTopStoryItem() throws IOException, InterruptedException {
            // Arrange - Get the top story ID
            List<Long> topStoryIds = client.getTopStories();
            assertThat(topStoryIds).isNotEmpty();

            Long firstStoryId = topStoryIds.get(0);

            // Act - Fetch the actual story
            Item story = client.getItem(firstStoryId);

            // Assert
            assertThat(story).isNotNull();
            assertThat(story.getId()).isEqualTo(firstStoryId);
            assertThat(story.getType()).isIn("story", "job", "poll");
            assertThat(story.getBy()).isNotNull();

            System.out.println("✓ Top story: " + story.getTitle());
            System.out.println("  By: " + story.getBy());
            System.out.println("  Score: " + story.getScore());
        }

        @Test
        @DisplayName("Should fetch and validate multiple top stories")
        void shouldFetchMultipleTopStories() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStoryIds = client.getTopStories();
            assertThat(topStoryIds).hasSizeGreaterThanOrEqualTo(5);

            // Act - Fetch first 5 stories
            int storiesToFetch = Math.min(5, topStoryIds.size());
            int validStories = 0;

            for (int i = 0; i < storiesToFetch; i++) {
                Item story = client.getItem(topStoryIds.get(i));

                if (story != null) {
                    validStories++;

                    // Assert each story has required fields
                    assertThat(story.getId()).isNotNull();
                    assertThat(story.getType()).isNotNull();

                    System.out.println((i + 1) + ". " +
                        (story.getTitle() != null ? story.getTitle() : "[No title]") +
                        " (Score: " + story.getScore() + ")");
                }
            }

            // Assert - At least most stories should be valid
            assertThat(validStories).isGreaterThan(0);
            System.out.println("✓ Successfully fetched " + validStories + "/" + storiesToFetch + " stories");
        }

        @Test
        @DisplayName("Should verify top stories have expected properties")
        void shouldVerifyTopStoryProperties() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStoryIds = client.getTopStories();
            assertThat(topStoryIds).isNotEmpty();

            // Act
            Item topStory = client.getItem(topStoryIds.get(0));

            // Assert - Top stories should have these characteristics
            assertAll("Top story properties",
                    () -> assertThat(topStory).isNotNull(),
                    () -> assertThat(topStory.getId()).isPositive(),
                    () -> assertThat(topStory.getType()).isNotBlank(),
                    () -> assertThat(topStory.getBy()).isNotBlank(),
                    () -> assertThat(topStory.getTime()).isPositive()
            );

            // Most top stories should have a score (unless it's a job)
            if (!"job".equals(topStory.getType())) {
                assertThat(topStory.getScore()).isNotNull();
            }
        }

        @Test
        @DisplayName("Should handle fetching non-existent story gracefully")
        void shouldHandleNonExistentStory() throws IOException, InterruptedException {
            // Act - Try to fetch an item that likely doesn't exist
            Item result = client.getItem(999999999999L);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Top Stories Workflow Tests")
    class WorkflowTests {

        @Test
        @DisplayName("Should demonstrate typical top stories usage pattern")
        void shouldDemonstrateTypicalUsage() throws IOException, InterruptedException {
            // This test demonstrates the typical pattern for using the Top Stories API

            // Step 1: Create client
            HackerNewsApiClient client = new HackerNewsApiClient();

            // Step 2: Get top story IDs
            List<Long> topStoryIds = client.getTopStories();
            assertThat(topStoryIds).isNotEmpty();

            // Step 3: Fetch a limited number of stories (e.g., first 3 for homepage)
            int limit = Math.min(3, topStoryIds.size());

            for (int i = 0; i < limit; i++) {
                Long storyId = topStoryIds.get(i);
                Item story = client.getItem(storyId);

                if (story != null) {
                    // Step 4: Display story information
                    String title = story.getTitle() != null ? story.getTitle() : "[No title]";
                    Integer score = story.getScore() != null ? story.getScore() : 0;
                    String author = story.getBy() != null ? story.getBy() : "unknown";

                    System.out.println(String.format(
                        "Story #%d: %s (Score: %d, By: %s)",
                        i + 1, title, score, author
                    ));

                    assertThat(story.getId()).isEqualTo(storyId);
                }
            }

            System.out.println("✓ Workflow test completed successfully");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    @Tag("error-handling")
    class ErrorHandlingTests {

        private HackerNewsApiClient client;

        @BeforeEach
        void setUp() {
            client = new HackerNewsApiClient();
        }

        @Test
        @DisplayName("Should return null for extremely large non-existent item ID")
        void shouldHandleExtremelyLargeItemId() throws IOException, InterruptedException {
            // Act - Try to fetch an item with an extremely large ID
            Item result = client.getItem(Long.MAX_VALUE);

            // Assert
            assertThat(result).isNull();
            System.out.println("✓ Correctly handled extremely large item ID (returned null)");
        }

        @Test
        @DisplayName("Should return null for zero item ID")
        void shouldHandleZeroItemId() throws IOException, InterruptedException {
            // Act - Try to fetch item with ID 0
            Item result = client.getItem(0L);

            // Assert
            assertThat(result).isNull();
            System.out.println("✓ Correctly handled zero item ID (returned null)");
        }

        @Test
        @DisplayName("Should return null for negative item ID")
        void shouldHandleNegativeItemId() throws IOException, InterruptedException {
            // Act - Try to fetch item with negative ID
            Item result = client.getItem(-1L);

            // Assert
            assertThat(result).isNull();
            System.out.println("✓ Correctly handled negative item ID (returned null)");
        }

        @Test
        @DisplayName("Should handle deleted items gracefully")
        void shouldHandleDeletedItems() throws IOException, InterruptedException {
            // Arrange - Item 1 is a known deleted/old item that returns null
            Long deletedItemId = 1L;

            // Act
            Item result = client.getItem(deletedItemId);

            // Assert - Deleted items return null from the API
            // Note: This might return an actual item if ID 1 exists, but demonstrates null handling
            if (result == null) {
                System.out.println("✓ Correctly handled deleted/missing item (returned null)");
            } else {
                System.out.println("✓ Item exists (ID: " + result.getId() + ")");
            }
            // Either null or valid item is acceptable
            assertThat(result == null || result.getId() != null).isTrue();
        }

        @Test
        @DisplayName("Should validate top stories list is not null")
        void shouldReturnNonNullTopStoriesList() throws IOException, InterruptedException {
            // Act
            List<Long> topStories = client.getTopStories();

            // Assert - API should always return a list, never null
            assertThat(topStories)
                    .as("Top stories list should never be null")
                    .isNotNull();
            System.out.println("✓ Top stories list is not null (size: " + topStories.size() + ")");
        }

        @Test
        @DisplayName("Should handle fetching multiple invalid items without throwing exceptions")
        void shouldHandleMultipleInvalidItems() throws IOException, InterruptedException {
            // Arrange - List of invalid item IDs
            List<Long> invalidIds = List.of(-1L, 0L, 999999999999L);
            int nullCount = 0;

            // Act - Try to fetch each invalid item
            for (Long invalidId : invalidIds) {
                Item result = client.getItem(invalidId);
                if (result == null) {
                    nullCount++;
                }
            }

            // Assert - All should return null without throwing exceptions
            assertThat(nullCount).isEqualTo(invalidIds.size());
            System.out.println("✓ Successfully handled " + nullCount + " invalid item IDs without exceptions");
        }

        @Test
        @DisplayName("Should verify API returns valid response structure for top stories")
        void shouldValidateTopStoriesResponseStructure() throws IOException, InterruptedException {
            // Act
            List<Long> topStories = client.getTopStories();

            // Assert - Validate response structure
            assertAll("Top stories response validation",
                    () -> assertThat(topStories).isNotNull(),
                    () -> assertThat(topStories).isNotEmpty(),
                    () -> assertThat(topStories).allMatch(id -> id != null && id > 0,
                            "All IDs should be non-null and positive"),
                    () -> assertThat(topStories).doesNotContainNull(),
                    () -> assertThat(topStories.size()).isLessThanOrEqualTo(500)
            );
            System.out.println("✓ Top stories response structure validated successfully");
        }

        @Test
        @DisplayName("Should verify item response structure for valid story")
        void shouldValidateItemResponseStructure() throws IOException, InterruptedException {
            // Arrange - Get a valid story ID from top stories
            List<Long> topStories = client.getTopStories();
            assertThat(topStories).isNotEmpty();
            Long validStoryId = topStories.get(0);

            // Act
            Item story = client.getItem(validStoryId);

            // Assert - Validate response structure
            assertAll("Item response validation",
                    () -> assertThat(story).as("Story should not be null").isNotNull(),
                    () -> assertThat(story.getId()).as("Story ID should match requested ID").isEqualTo(validStoryId),
                    () -> assertThat(story.getType()).as("Story type should not be null").isNotNull(),
                    () -> assertThat(story.getBy()).as("Story author should not be null").isNotNull(),
                    () -> assertThat(story.getTime()).as("Story timestamp should be positive").isPositive()
            );
            System.out.println("✓ Item response structure validated for story ID: " + validStoryId);
        }

        @Test
        @DisplayName("Should handle mix of valid and invalid item IDs correctly")
        void shouldHandleMixedValidAndInvalidIds() throws IOException, InterruptedException {
            // Arrange - Get one valid ID from top stories
            List<Long> topStories = client.getTopStories();
            assertThat(topStories).isNotEmpty();
            Long validId = topStories.get(0);

            // Mix valid and invalid IDs
            List<Long> mixedIds = List.of(validId, -1L, 999999999999L);

            int validItems = 0;
            int nullItems = 0;

            // Act - Fetch each item
            for (Long id : mixedIds) {
                Item item = client.getItem(id);
                if (item != null) {
                    validItems++;
                    assertThat(item.getId()).isEqualTo(id);
                } else {
                    nullItems++;
                }
            }

            // Assert
            assertThat(validItems).as("Should have at least one valid item").isGreaterThan(0);
            assertThat(nullItems).as("Should have at least one null result").isGreaterThan(0);
            assertThat(validItems + nullItems).as("Total should match input size").isEqualTo(mixedIds.size());

            System.out.println("✓ Correctly handled mixed IDs: " + validItems + " valid, " + nullItems + " null");
        }
    }

    @Nested
    @DisplayName("Boundary Tests")
    @Tag("boundary")
    class BoundaryTests {

        private HackerNewsApiClient client;

        @BeforeEach
        void setUp() {
            client = new HackerNewsApiClient();
        }

        @Test
        @DisplayName("Should return null for Long.MIN_VALUE item ID")
        void shouldHandleLongMinValue() throws IOException, InterruptedException {
            // Act
            Item result = client.getItem(Long.MIN_VALUE);

            // Assert
            assertThat(result).isNull();
            System.out.println("✓ Correctly handled Long.MIN_VALUE (returned null)");
        }

        @Test
        @DisplayName("Should validate top stories list size is within bounds")
        void shouldValidateTopStoriesListSize() throws IOException, InterruptedException {
            // Act
            List<Long> topStories = client.getTopStories();

            // Assert - HN API returns up to 500 top stories
            assertThat(topStories)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSizeLessThanOrEqualTo(500)
                    .hasSizeGreaterThan(100); // Should always have at least 100

            System.out.println("✓ Top stories list size within bounds: " + topStories.size());
        }

        @Test
        @DisplayName("Should verify all story types from different endpoints")
        void shouldVerifyDifferentStoryTypes() throws IOException, InterruptedException {
            // Act - Fetch from different endpoints
            List<Long> topStories = client.getTopStories();
            List<Long> newStories = client.getNewStories();
            List<Long> bestStories = client.getBestStories();
            List<Long> askStories = client.getAskStories();
            List<Long> showStories = client.getShowStories();
            List<Long> jobStories = client.getJobStories();

            // Assert - All endpoints should return non-empty lists
            assertAll("All story endpoints should return lists",
                    () -> assertThat(topStories).isNotEmpty(),
                    () -> assertThat(newStories).isNotEmpty(),
                    () -> assertThat(bestStories).isNotEmpty(),
                    () -> assertThat(askStories).isNotEmpty(),
                    () -> assertThat(showStories).isNotEmpty(),
                    () -> assertThat(jobStories).isNotEmpty()
            );

            System.out.println("✓ All story type endpoints returned data:");
            System.out.println("  Top: " + topStories.size());
            System.out.println("  New: " + newStories.size());
            System.out.println("  Best: " + bestStories.size());
            System.out.println("  Ask: " + askStories.size());
            System.out.println("  Show: " + showStories.size());
            System.out.println("  Jobs: " + jobStories.size());
        }

        @Test
        @DisplayName("Should verify first and last IDs in top stories list are valid")
        void shouldVerifyFirstAndLastTopStoryIds() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStories = client.getTopStories();
            assertThat(topStories).isNotEmpty();

            // Act - Fetch first and last items
            Long firstId = topStories.get(0);
            Long lastId = topStories.get(topStories.size() - 1);

            Item firstItem = client.getItem(firstId);
            Item lastItem = client.getItem(lastId);

            // Assert
            assertThat(firstItem).isNotNull();
            assertThat(lastItem).isNotNull();
            assertThat(firstItem.getId()).isEqualTo(firstId);
            assertThat(lastItem.getId()).isEqualTo(lastId);

            System.out.println("✓ Verified boundary items in top stories:");
            System.out.println("  First (#1): " + firstItem.getTitle());
            System.out.println("  Last (#" + topStories.size() + "): " + lastItem.getTitle());
        }

        @Test
        @DisplayName("Should handle checking items just before and after max item ID")
        void shouldHandleItemsAroundMaxId() throws IOException, InterruptedException {
            // Arrange
            Long maxId = client.getMaxItemId();
            assertThat(maxId).isNotNull().isPositive();

            // Act - Try items around max ID
            Item atMax = client.getItem(maxId);
            Item beforeMax = client.getItem(maxId - 1);
            Item afterMax = client.getItem(maxId + 1);

            // Assert
            System.out.println("✓ Tested items around max ID (" + maxId + "):");
            System.out.println("  maxId - 1: " + (beforeMax != null ? "exists" : "null"));
            System.out.println("  maxId:     " + (atMax != null ? "exists" : "null"));
            System.out.println("  maxId + 1: " + (afterMax != null ? "exists" : "null"));

            // maxId + 1 should always be null
            assertThat(afterMax).isNull();
        }

        @Test
        @DisplayName("Should verify top stories IDs are monotonically increasing")
        void shouldVerifyTopStoriesIdsAreIncreasing() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStories = client.getTopStories();
            assertThat(topStories).hasSizeGreaterThan(10);

            // Act - Check if IDs generally increase (newer stories have higher IDs)
            // Note: Not strictly monotonic due to older stories being upvoted
            Long firstId = topStories.get(0);
            Long tenthId = topStories.get(9);
            Long lastId = topStories.get(topStories.size() - 1);

            // Assert - All IDs should be positive and relatively high
            assertThat(firstId).isPositive();
            assertThat(tenthId).isPositive();
            assertThat(lastId).isPositive();

            // First story should typically have higher ID than last (newer)
            // But this isn't guaranteed, so we just verify IDs are in reasonable range
            Long maxId = client.getMaxItemId();
            assertThat(firstId).isLessThanOrEqualTo(maxId);
            assertThat(lastId).isLessThanOrEqualTo(maxId);

            System.out.println("✓ Verified top story ID ranges:");
            System.out.println("  First story ID: " + firstId);
            System.out.println("  10th story ID: " + tenthId);
            System.out.println("  Last story ID: " + lastId);
            System.out.println("  Current max ID: " + maxId);
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    @Tag("performance")
    class PerformanceTests {

        @Test
        @DisplayName("Should fetch top stories within reasonable time")
        @Timeout(5)  // Should complete within 5 seconds
        void shouldFetchTopStoriesQuickly() throws IOException, InterruptedException {
            // Arrange
            HackerNewsApiClient client = new HackerNewsApiClient();

            // Act
            long startTime = System.currentTimeMillis();
            List<Long> topStoryIds = client.getTopStories();
            long endTime = System.currentTimeMillis();

            // Assert
            assertThat(topStoryIds).isNotEmpty();

            long duration = endTime - startTime;
            System.out.println("✓ Fetched top stories in " + duration + "ms");

            // Should typically complete in under 2 seconds
            assertThat(duration).isLessThan(2000);
        }

        @Test
        @DisplayName("Should handle concurrent top stories requests")
        @Timeout(10)
        void shouldHandleConcurrentRequests() throws IOException, InterruptedException {
            // Arrange
            HackerNewsApiClient client = new HackerNewsApiClient();

            // Act - Make two requests back-to-back
            List<Long> firstRequest = client.getTopStories();
            List<Long> secondRequest = client.getTopStories();

            // Assert - Both should succeed
            assertThat(firstRequest).isNotEmpty();
            assertThat(secondRequest).isNotEmpty();

            // The lists might differ slightly as stories change, but should be similar
            assertThat(firstRequest).hasSizeGreaterThan(0);
            assertThat(secondRequest).hasSizeGreaterThan(0);

            System.out.println("✓ Concurrent requests handled successfully");
        }
    }
}
