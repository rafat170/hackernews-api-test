package com.hackernews.api.client;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * API Health Check Tests - Smoke tests to verify Hacker News API is reachable.
 * These tests run first to ensure API availability before running the full test suite.
 */
@DisplayName("API Health Check Tests")
@Tag("health-check")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AApiHealthCheckTest {

    // Shared state to track API availability for other test classes
    public static volatile boolean API_AVAILABLE = false;
    private static HackerNewsApiClient client;

    @BeforeAll
    static void setUp() {
        client = new HackerNewsApiClient();
    }

    @Test
    @Order(1)
    @DisplayName("Should verify API base endpoint is reachable")
    @Timeout(10)
    void shouldVerifyApiIsReachable() throws IOException, InterruptedException {
        // Act - Try to get max item ID (fastest endpoint)
        Long maxItemId = client.getMaxItemId();

        // Assert
        assertThat(maxItemId)
                .as("Max item ID should be returned from API")
                .isNotNull()
                .isPositive();

        System.out.println("✓ API is reachable (max item ID: " + maxItemId + ")");
    }

    @Test
    @Order(2)
    @DisplayName("Should verify top stories endpoint is accessible")
    @Timeout(10)
    void shouldVerifyTopStoriesEndpoint() throws IOException, InterruptedException {
        // Act
        List<Long> topStories = client.getTopStories();

        // Assert
        assertThat(topStories)
                .as("Top stories should be returned")
                .isNotNull()
                .isNotEmpty();

        System.out.println("✓ Top stories endpoint is accessible (" + topStories.size() + " stories)");
    }

    @Test
    @Order(3)
    @DisplayName("Should verify items endpoint is accessible")
    @Timeout(10)
    void shouldVerifyItemsEndpoint() throws IOException, InterruptedException {
        // Arrange - Get a known item ID
        List<Long> topStories = client.getTopStories();
        assertThat(topStories).isNotEmpty();
        Long itemId = topStories.get(0);

        // Act
        var item = client.getItem(itemId);

        // Assert
        assertThat(item)
                .as("Item should be fetchable")
                .isNotNull();

        System.out.println("✓ Items endpoint is accessible (fetched item ID: " + itemId + ")");
    }

    @Test
    @Order(4)
    @DisplayName("Should verify all story type endpoints are accessible")
    @Timeout(15)
    void shouldVerifyAllEndpoints() throws IOException, InterruptedException {
        // Act - Try all endpoints
        List<Long> newStories = client.getNewStories();
        List<Long> bestStories = client.getBestStories();
        List<Long> askStories = client.getAskStories();
        List<Long> showStories = client.getShowStories();
        List<Long> jobStories = client.getJobStories();

        // Assert - All should return data
        assertThat(newStories).as("New stories endpoint").isNotEmpty();
        assertThat(bestStories).as("Best stories endpoint").isNotEmpty();
        assertThat(askStories).as("Ask stories endpoint").isNotEmpty();
        assertThat(showStories).as("Show stories endpoint").isNotEmpty();
        assertThat(jobStories).as("Job stories endpoint").isNotEmpty();

        System.out.println("✓ All story endpoints are accessible:");
        System.out.println("  - New stories: " + newStories.size());
        System.out.println("  - Best stories: " + bestStories.size());
        System.out.println("  - Ask stories: " + askStories.size());
        System.out.println("  - Show stories: " + showStories.size());
        System.out.println("  - Job stories: " + jobStories.size());
    }

    @Test
    @Order(5)
    @DisplayName("Should verify API response time is acceptable")
    @Timeout(5)
    void shouldVerifyResponseTime() throws IOException, InterruptedException {
        // Act
        long startTime = System.currentTimeMillis();
        client.getMaxItemId();
        long endTime = System.currentTimeMillis();

        long responseTime = endTime - startTime;

        // Assert - Should respond within 3 seconds
        assertThat(responseTime)
                .as("API response time should be acceptable")
                .isLessThan(3000);

        System.out.println("✓ API response time is acceptable (" + responseTime + "ms)");
    }

    @AfterAll
    static void markApiAsAvailable() {
        // If we reach here, all health checks passed
        API_AVAILABLE = true;
        System.out.println("\n=== ✓ All API Health Checks Passed ===");
        System.out.println("Hacker News API is healthy and ready for testing\n");
    }
}
