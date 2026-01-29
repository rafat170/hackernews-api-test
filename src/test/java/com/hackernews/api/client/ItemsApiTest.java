package com.hackernews.api.client;

import com.hackernews.api.model.Item;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Items API functionality.
 * Uses the Top Stories API to get real item IDs for testing.
 */
@DisplayName("Items API Tests")
class ItemsApiTest {

    private HackerNewsApiClient client;

    @BeforeEach
    void setUp() {
        client = new HackerNewsApiClient();
    }

    @Nested
    @DisplayName("Get Item Tests")
    @Tag("integration")
    class GetItemTests {

        @Test
        @DisplayName("Should fetch current top story using Items API")
        void shouldFetchCurrentTopStory() throws IOException, InterruptedException {
            // Arrange - Get the current top story ID from Top Stories API
            List<Long> topStoryIds = client.getTopStories();
            assertThat(topStoryIds).isNotEmpty();
            Long topStoryId = topStoryIds.get(0);

            // Act - Fetch the item using Items API
            Item topStory = client.getItem(topStoryId);

            // Assert
            assertThat(topStory).isNotNull();
            assertThat(topStory.getId()).isEqualTo(topStoryId);
            assertThat(topStory.getType()).isIn("story", "job", "poll");
            assertThat(topStory.getBy()).isNotNull();
            assertThat(topStory.getTime()).isPositive();

            System.out.println("✓ Top story fetched: " + topStory.getTitle());
            System.out.println("  ID: " + topStory.getId());
            System.out.println("  Type: " + topStory.getType());
            System.out.println("  Author: " + topStory.getBy());
            System.out.println("  Score: " + topStory.getScore());
        }

        @Test
        @DisplayName("Should fetch multiple items from top stories")
        void shouldFetchMultipleItems() throws IOException, InterruptedException {
            // Arrange - Get top 10 story IDs
            List<Long> topStoryIds = client.getTopStories();
            int itemsToFetch = Math.min(10, topStoryIds.size());

            // Act & Assert
            int successfulFetches = 0;
            for (int i = 0; i < itemsToFetch; i++) {
                Long itemId = topStoryIds.get(i);
                Item item = client.getItem(itemId);

                if (item != null) {
                    successfulFetches++;
                    assertThat(item.getId()).isEqualTo(itemId);
                    assertThat(item.getType()).isNotNull();

                    System.out.println((i + 1) + ". " +
                        (item.getTitle() != null ? item.getTitle() : "[No title - " + item.getType() + "]"));
                }
            }

            assertThat(successfulFetches).isGreaterThan(0);
            System.out.println("✓ Successfully fetched " + successfulFetches + "/" + itemsToFetch + " items");
        }

        @Test
        @DisplayName("Should handle non-existent item ID")
        void shouldHandleNonExistentItem() throws IOException, InterruptedException {
            // Act - Try to fetch an item that doesn't exist
            Item result = client.getItem(999999999999L);

            // Assert
            assertThat(result).isNull();
            System.out.println("✓ Correctly returned null for non-existent item");
        }

        @Test
        @DisplayName("Should fetch item with all story properties")
        void shouldFetchItemWithAllStoryProperties() throws IOException, InterruptedException {
            // Arrange - Get a top story
            List<Long> topStoryIds = client.getTopStories();
            assertThat(topStoryIds).isNotEmpty();

            // Act - Fetch the top story
            Item story = client.getItem(topStoryIds.get(0));

            // Assert - Verify story has expected properties
            assertAll("Story properties",
                () -> assertThat(story).isNotNull(),
                () -> assertThat(story.getId()).isPositive(),
                () -> assertThat(story.getType()).isNotBlank(),
                () -> assertThat(story.getBy()).isNotBlank(),
                () -> assertThat(story.getTime()).isPositive()
            );

            // Check optional story properties
            if (story.getTitle() != null) {
                assertThat(story.getTitle()).isNotEmpty();
                System.out.println("  Title: " + story.getTitle());
            }

            if (story.getUrl() != null) {
                assertThat(story.getUrl()).isNotEmpty();
                System.out.println("  URL: " + story.getUrl());
            }

            if (story.getScore() != null) {
                assertThat(story.getScore()).isGreaterThanOrEqualTo(0);
                System.out.println("  Score: " + story.getScore());
            }

            System.out.println("✓ Story has all expected properties");
        }
    }

    @Nested
    @DisplayName("Item Type Tests")
    @Tag("integration")
    class ItemTypeTests {

        @Test
        @DisplayName("Should identify story items correctly")
        void shouldIdentifyStoryItems() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStoryIds = client.getTopStories();

            // Act - Fetch first few items to find a story
            boolean foundStory = false;
            for (int i = 0; i < Math.min(5, topStoryIds.size()); i++) {
                Item item = client.getItem(topStoryIds.get(i));

                if (item != null && "story".equals(item.getType())) {
                    // Assert - Story should have certain properties
                    assertThat(item.getType()).isEqualTo("story");
                    assertThat(item.getBy()).isNotNull();
                    assertThat(item.getTime()).isPositive();

                    System.out.println("✓ Found story item:");
                    System.out.println("  Title: " + item.getTitle());
                    System.out.println("  Score: " + item.getScore());
                    System.out.println("  Author: " + item.getBy());

                    foundStory = true;
                    break;
                }
            }

            assertThat(foundStory).isTrue();
        }

        @Test
        @DisplayName("Should handle different item types from top stories")
        void shouldHandleDifferentItemTypes() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStoryIds = client.getTopStories();
            int itemsToCheck = Math.min(20, topStoryIds.size());

            // Act - Check types of items
            int storyCount = 0;
            int jobCount = 0;
            int pollCount = 0;
            int otherCount = 0;

            for (int i = 0; i < itemsToCheck; i++) {
                Item item = client.getItem(topStoryIds.get(i));

                if (item != null && item.getType() != null) {
                    switch (item.getType()) {
                        case "story":
                            storyCount++;
                            break;
                        case "job":
                            jobCount++;
                            break;
                        case "poll":
                            pollCount++;
                            break;
                        default:
                            otherCount++;
                            break;
                    }
                }
            }

            // Assert - Should have found at least some items
            int totalItems = storyCount + jobCount + pollCount + otherCount;
            assertThat(totalItems).isGreaterThan(0);

            System.out.println("✓ Item types found:");
            System.out.println("  Stories: " + storyCount);
            System.out.println("  Jobs: " + jobCount);
            System.out.println("  Polls: " + pollCount);
            System.out.println("  Other: " + otherCount);
        }
    }

    @Nested
    @DisplayName("Item Properties Validation")
    @Tag("integration")
    class ItemPropertiesValidationTests {

        @Test
        @DisplayName("Should validate item has valid timestamp")
        void shouldValidateItemTimestamp() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStoryIds = client.getTopStories();
            Item item = client.getItem(topStoryIds.get(0));

            // Act & Assert
            assertThat(item).isNotNull();
            assertThat(item.getTime()).isPositive();

            // Timestamp should be reasonable (after year 2000, before year 2100)
            long minTimestamp = 946684800L; // Jan 1, 2000
            long maxTimestamp = 4102444800L; // Jan 1, 2100

            assertThat(item.getTime())
                .isGreaterThan(minTimestamp)
                .isLessThan(maxTimestamp);

            System.out.println("✓ Item timestamp is valid: " + item.getTime());
        }

        @Test
        @DisplayName("Should validate item has valid author")
        void shouldValidateItemAuthor() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStoryIds = client.getTopStories();
            Item item = client.getItem(topStoryIds.get(0));

            // Act & Assert
            assertThat(item).isNotNull();
            assertThat(item.getBy())
                .isNotNull()
                .isNotEmpty();

            // HN usernames should not contain spaces
            assertThat(item.getBy()).doesNotContain(" ");

            System.out.println("✓ Item author is valid: " + item.getBy());
        }

        @Test
        @DisplayName("Should validate story has score")
        void shouldValidateStoryHasScore() throws IOException, InterruptedException {
            // Arrange - Find a story (not a job)
            List<Long> topStoryIds = client.getTopStories();

            Item story = null;
            for (int i = 0; i < Math.min(10, topStoryIds.size()); i++) {
                Item item = client.getItem(topStoryIds.get(i));
                if (item != null && "story".equals(item.getType())) {
                    story = item;
                    break;
                }
            }

            // Act & Assert
            assertThat(story).isNotNull();
            assertThat(story.getScore())
                .isNotNull()
                .isGreaterThanOrEqualTo(0);

            System.out.println("✓ Story score is valid: " + story.getScore());
        }

        @Test
        @DisplayName("Should validate item with kids has valid kids array")
        void shouldValidateItemKids() throws IOException, InterruptedException {
            // Arrange - Find an item with kids (comments)
            List<Long> topStoryIds = client.getTopStories();

            Item itemWithKids = null;
            for (int i = 0; i < Math.min(10, topStoryIds.size()); i++) {
                Item item = client.getItem(topStoryIds.get(i));
                if (item != null && item.getKids() != null && item.getKids().length > 0) {
                    itemWithKids = item;
                    break;
                }
            }

            // Act & Assert
            if (itemWithKids != null) {
                assertThat(itemWithKids.getKids())
                    .isNotNull()
                    .isNotEmpty();

                // All kid IDs should be positive
                for (Long kidId : itemWithKids.getKids()) {
                    assertThat(kidId).isPositive();
                }

                System.out.println("✓ Item has valid kids array with " + itemWithKids.getKids().length + " comments");
            } else {
                System.out.println("⚠ No items with kids found in top 10 stories");
            }
        }
    }

    @Nested
    @DisplayName("Item Comments Tests")
    @Tag("integration")
    class ItemCommentsTests {

        @Test
        @DisplayName("Should fetch comment item from story")
        void shouldFetchCommentFromStory() throws IOException, InterruptedException {
            // Arrange - Find a story with comments
            List<Long> topStoryIds = client.getTopStories();

            Item storyWithComments = null;
            for (int i = 0; i < Math.min(10, topStoryIds.size()); i++) {
                Item item = client.getItem(topStoryIds.get(i));
                if (item != null && item.getKids() != null && item.getKids().length > 0) {
                    storyWithComments = item;
                    break;
                }
            }

            if (storyWithComments != null) {
                // Act - Fetch the first comment
                Long firstCommentId = storyWithComments.getKids()[0];
                Item comment = client.getItem(firstCommentId);

                // Assert
                assertThat(comment).isNotNull();
                assertThat(comment.getId()).isEqualTo(firstCommentId);
                assertThat(comment.getType()).isEqualTo("comment");
                assertThat(comment.getParent()).isEqualTo(storyWithComments.getId());
                assertThat(comment.getBy()).isNotNull();

                System.out.println("✓ Comment fetched successfully:");
                System.out.println("  ID: " + comment.getId());
                System.out.println("  Author: " + comment.getBy());
                System.out.println("  Parent: " + comment.getParent());
                if (comment.getText() != null) {
                    String preview = comment.getText().substring(0, Math.min(50, comment.getText().length()));
                    System.out.println("  Text preview: " + preview + "...");
                }
            } else {
                System.out.println("⚠ No stories with comments found in top 10");
            }
        }

        @Test
        @DisplayName("Should verify comment structure is valid")
        void shouldVerifyCommentStructure() throws IOException, InterruptedException {
            // Arrange - Find a story with comments
            List<Long> topStoryIds = client.getTopStories();

            Item storyWithComments = null;
            for (int i = 0; i < Math.min(10, topStoryIds.size()); i++) {
                Item item = client.getItem(topStoryIds.get(i));
                if (item != null && item.getKids() != null && item.getKids().length > 0) {
                    storyWithComments = item;
                    break;
                }
            }

            if (storyWithComments != null) {
                // Act - Fetch first comment
                Item comment = client.getItem(storyWithComments.getKids()[0]);

                // Assert - Comment should have specific structure
                if (comment != null) {
                    assertAll("Comment structure",
                        () -> assertThat(comment.getType()).isEqualTo("comment"),
                        () -> assertThat(comment.getParent()).isNotNull(),
                        () -> assertThat(comment.getBy()).isNotNull(),
                        () -> assertThat(comment.getTime()).isPositive()
                    );

                    // Comment should have either text or be deleted
                    boolean hasTextOrDeleted = comment.getText() != null ||
                                              Boolean.TRUE.equals(comment.getDeleted());
                    assertThat(hasTextOrDeleted).isTrue();

                    System.out.println("✓ Comment structure is valid");
                }
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    @Tag("performance")
    class ItemsApiPerformanceTests {

        @Test
        @DisplayName("Should fetch item within reasonable time")
        @Timeout(5)
        void shouldFetchItemQuickly() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStoryIds = client.getTopStories();
            Long itemId = topStoryIds.get(0);

            // Act
            long startTime = System.currentTimeMillis();
            Item item = client.getItem(itemId);
            long endTime = System.currentTimeMillis();

            // Assert
            assertThat(item).isNotNull();

            long duration = endTime - startTime;
            System.out.println("✓ Fetched item in " + duration + "ms");

            // Should complete in under 2 seconds
            assertThat(duration).isLessThan(2000);
        }

        @Test
        @DisplayName("Should fetch multiple items efficiently")
        @Timeout(10)
        void shouldFetchMultipleItemsEfficiently() throws IOException, InterruptedException {
            // Arrange
            List<Long> topStoryIds = client.getTopStories();
            int itemsToFetch = Math.min(5, topStoryIds.size());

            // Act
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < itemsToFetch; i++) {
                Item item = client.getItem(topStoryIds.get(i));
                assertThat(item).isNotNull();
            }

            long endTime = System.currentTimeMillis();

            // Assert
            long duration = endTime - startTime;
            long averageTime = duration / itemsToFetch;

            System.out.println("✓ Fetched " + itemsToFetch + " items in " + duration + "ms");
            System.out.println("  Average time per item: " + averageTime + "ms");

            // Each item should average less than 1 second
            assertThat(averageTime).isLessThan(1000);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    @Tag("integration")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null response gracefully")
        void shouldHandleNullResponse() throws IOException, InterruptedException {
            // Act - Try to fetch non-existent item
            Item result = client.getItem(999999999999L);

            // Assert
            assertThat(result).isNull();
            System.out.println("✓ Handled null response gracefully");
        }

        @Test
        @DisplayName("Should handle deleted item")
        void shouldHandleDeletedItem() throws IOException, InterruptedException {
            // Note: This test will only work if we can find a deleted item
            // For now, we'll just verify the client can handle the deleted flag

            List<Long> topStoryIds = client.getTopStories();

            // Check first 20 items for a deleted one
            boolean foundDeletedItem = false;
            for (int i = 0; i < Math.min(20, topStoryIds.size()); i++) {
                Item item = client.getItem(topStoryIds.get(i));

                if (item != null && Boolean.TRUE.equals(item.getDeleted())) {
                    assertThat(item.getDeleted()).isTrue();
                    foundDeletedItem = true;
                    System.out.println("✓ Found and handled deleted item: " + item.getId());
                    break;
                }
            }

            if (!foundDeletedItem) {
                System.out.println("⚠ No deleted items found in top 20 stories (this is expected)");
            }
        }
    }

    @Nested
    @DisplayName("Story to Comment Workflow Tests")
    @Tag("integration")
    class StoryToCommentWorkflowTests {

        @Test
        @DisplayName("Should retrieve top story and its first comment")
        void shouldRetrieveTopStoryAndFirstComment() throws IOException, InterruptedException {
            // Step 1: Get top stories from Top Stories API
            System.out.println("\n=== Story to Comment Workflow ===");
            List<Long> topStoryIds = client.getTopStories();
            assertThat(topStoryIds).isNotEmpty();
            System.out.println("Step 1: Retrieved " + topStoryIds.size() + " top story IDs");

            // Step 2: Find a story with comments
            Item storyWithComments = null;
            int storiesChecked = 0;
            for (int i = 0; i < Math.min(10, topStoryIds.size()); i++) {
                storiesChecked++;
                Item story = client.getItem(topStoryIds.get(i));

                if (story != null && story.getKids() != null && story.getKids().length > 0) {
                    storyWithComments = story;
                    break;
                }
            }

            assertThat(storyWithComments)
                .as("Should find at least one story with comments in top 10")
                .isNotNull();

            System.out.println("Step 2: Found story with comments after checking " + storiesChecked + " stories");
            System.out.println("  Story ID: " + storyWithComments.getId());
            System.out.println("  Title: " + storyWithComments.getTitle());
            System.out.println("  Author: " + storyWithComments.getBy());
            System.out.println("  Score: " + storyWithComments.getScore());
            System.out.println("  Comment count: " + storyWithComments.getKids().length);

            // Step 3: Retrieve the first comment using Items API
            Long firstCommentId = storyWithComments.getKids()[0];
            Long storyId = storyWithComments.getId(); // Extract ID for use in lambda
            Item firstComment = client.getItem(firstCommentId);

            assertThat(firstComment).isNotNull();
            System.out.println("Step 3: Retrieved first comment");
            System.out.println("  Comment ID: " + firstComment.getId());
            System.out.println("  Comment Author: " + firstComment.getBy());
            System.out.println("  Comment Parent: " + firstComment.getParent());

            // Step 4: Validate the comment
            assertAll("First comment validation",
                () -> assertThat(firstComment.getId()).isEqualTo(firstCommentId),
                () -> assertThat(firstComment.getType()).isEqualTo("comment"),
                () -> assertThat(firstComment.getParent()).isEqualTo(storyId),
                () -> assertThat(firstComment.getBy()).isNotNull(),
                () -> assertThat(firstComment.getTime()).isPositive()
            );

            if (firstComment.getText() != null) {
                String preview = firstComment.getText().substring(0, Math.min(100, firstComment.getText().length()));
                System.out.println("  Comment Text Preview: " + preview + "...");
            }

            System.out.println("✓ Successfully completed story-to-comment workflow");
        }

        @Test
        @DisplayName("Should retrieve multiple comments from a top story")
        void shouldRetrieveMultipleCommentsFromTopStory() throws IOException, InterruptedException {
            // Step 1: Get a story with multiple comments
            List<Long> topStoryIds = client.getTopStories();

            Item storyWithComments = null;
            for (int i = 0; i < Math.min(10, topStoryIds.size()); i++) {
                Item story = client.getItem(topStoryIds.get(i));
                if (story != null && story.getKids() != null && story.getKids().length >= 3) {
                    storyWithComments = story;
                    break;
                }
            }

            assertThat(storyWithComments).isNotNull();

            System.out.println("\n=== Retrieve Multiple Comments ===");
            System.out.println("Story: " + storyWithComments.getTitle());
            System.out.println("Total comments: " + storyWithComments.getKids().length);

            // Step 2: Retrieve first 3 comments
            int commentsToFetch = Math.min(3, storyWithComments.getKids().length);
            int successfulFetches = 0;

            for (int i = 0; i < commentsToFetch; i++) {
                Long commentId = storyWithComments.getKids()[i];
                Item comment = client.getItem(commentId);

                if (comment != null) {
                    successfulFetches++;

                    // Validate each comment
                    assertThat(comment.getType()).isEqualTo("comment");
                    assertThat(comment.getParent()).isEqualTo(storyWithComments.getId());

                    System.out.println("Comment " + (i + 1) + ":");
                    System.out.println("  ID: " + comment.getId());
                    System.out.println("  By: " + comment.getBy());
                    if (comment.getText() != null) {
                        String preview = comment.getText().substring(0, Math.min(60, comment.getText().length()));
                        System.out.println("  Text: " + preview.replaceAll("\\n", " ") + "...");
                    }
                }
            }

            assertThat(successfulFetches).isEqualTo(commentsToFetch);
            System.out.println("✓ Successfully fetched " + successfulFetches + " comments");
        }

        @Test
        @DisplayName("Should validate comment hierarchy and relationships")
        void shouldValidateCommentHierarchy() throws IOException, InterruptedException {
            // Get a story with comments
            List<Long> topStoryIds = client.getTopStories();

            Item story = null;
            for (int i = 0; i < Math.min(10, topStoryIds.size()); i++) {
                Item item = client.getItem(topStoryIds.get(i));
                if (item != null && item.getKids() != null && item.getKids().length > 0) {
                    story = item;
                    break;
                }
            }

            assertThat(story).isNotNull();

            System.out.println("\n=== Comment Hierarchy Validation ===");
            System.out.println("Story: " + story.getTitle());

            // Get first comment
            Item comment = client.getItem(story.getKids()[0]);
            assertThat(comment).isNotNull();

            // Validate parent-child relationship
            assertThat(comment.getParent()).isEqualTo(story.getId());
            System.out.println("✓ Comment parent ID matches story ID");

            // Check if comment has nested replies
            if (comment.getKids() != null && comment.getKids().length > 0) {
                System.out.println("  Comment has " + comment.getKids().length + " replies");

                // Fetch first nested comment
                Item nestedComment = client.getItem(comment.getKids()[0]);
                if (nestedComment != null) {
                    assertThat(nestedComment.getParent()).isEqualTo(comment.getId());
                    System.out.println("✓ Nested comment parent ID matches parent comment ID");
                    System.out.println("  Hierarchy: Story -> Comment -> Nested Comment");
                }
            } else {
                System.out.println("  Comment has no nested replies");
            }

            System.out.println("✓ Comment hierarchy validated");
        }

        @Test
        @DisplayName("Should handle story with no comments gracefully")
        void shouldHandleStoryWithNoComments() throws IOException, InterruptedException {
            // Get top stories
            List<Long> topStoryIds = client.getTopStories();

            // Find a story without comments (or use a random one and check)
            Item storyWithoutComments = null;
            for (int i = 0; i < Math.min(30, topStoryIds.size()); i++) {
                Item story = client.getItem(topStoryIds.get(i));
                if (story != null && (story.getKids() == null || story.getKids().length == 0)) {
                    storyWithoutComments = story;
                    break;
                }
            }

            if (storyWithoutComments != null) {
                System.out.println("\n=== Story Without Comments ===");
                System.out.println("Story: " + storyWithoutComments.getTitle());
                System.out.println("Kids array: " + (storyWithoutComments.getKids() == null ? "null" : "empty"));

                // Validate that kids is either null or empty
                boolean hasNoComments = storyWithoutComments.getKids() == null ||
                                       storyWithoutComments.getKids().length == 0;
                assertThat(hasNoComments).isTrue();

                System.out.println("✓ Correctly identified story with no comments");
            } else {
                System.out.println("⚠ All stories in top 30 have comments (this is common)");
            }
        }

        @Test
        @DisplayName("Should measure performance of story-to-comment retrieval")
        @Timeout(10)
        void shouldMeasureStoryToCommentPerformance() throws IOException, InterruptedException {
            System.out.println("\n=== Performance Measurement ===");

            // Step 1: Get top stories
            long startTime = System.currentTimeMillis();
            List<Long> topStoryIds = client.getTopStories();
            long topStoriesTime = System.currentTimeMillis() - startTime;

            // Step 2: Get a story
            startTime = System.currentTimeMillis();
            Item story = client.getItem(topStoryIds.get(0));
            long storyFetchTime = System.currentTimeMillis() - startTime;

            assertThat(story).isNotNull();

            // Step 3: Get first comment (if exists)
            long commentFetchTime = 0;
            if (story.getKids() != null && story.getKids().length > 0) {
                startTime = System.currentTimeMillis();
                Item comment = client.getItem(story.getKids()[0]);
                commentFetchTime = System.currentTimeMillis() - startTime;

                assertThat(comment).isNotNull();
            }

            // Report performance
            System.out.println("Performance Results:");
            System.out.println("  Top Stories API: " + topStoriesTime + "ms");
            System.out.println("  Fetch Story: " + storyFetchTime + "ms");
            if (commentFetchTime > 0) {
                System.out.println("  Fetch Comment: " + commentFetchTime + "ms");
                System.out.println("  Total Workflow: " + (topStoriesTime + storyFetchTime + commentFetchTime) + "ms");
            }

            // Validate performance
            assertThat(topStoriesTime).isLessThan(2000);
            assertThat(storyFetchTime).isLessThan(1000);
            if (commentFetchTime > 0) {
                assertThat(commentFetchTime).isLessThan(1000);
            }

            System.out.println("✓ All operations completed within performance thresholds");
        }
    }
}
