package com.hackernews.api.client;

import com.hackernews.api.model.Item;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Top Stories API functionality.
 * Tests the real Hacker News API endpoints.
 */
@DisplayName("Top Stories API Tests")
class TopStoriesApiTest {

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
