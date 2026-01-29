package com.hackernews.api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Item model class.
 * This template demonstrates various testing patterns for model/POJO classes.
 */
@DisplayName("Item Model Tests")
class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        // Initialize a fresh Item instance before each test
        item = new Item();
    }

    @Nested
    @DisplayName("Constructor and Basic Property Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create item with null properties by default")
        void shouldCreateItemWithNullProperties() {
            // Arrange & Act
            Item newItem = new Item();

            // Assert
            assertThat(newItem.getId()).isNull();
            assertThat(newItem.getType()).isNull();
            assertThat(newItem.getTitle()).isNull();
        }

        @Test
        @DisplayName("Should set and get id correctly")
        void shouldSetAndGetId() {
            // Arrange
            Long expectedId = 12345L;

            // Act
            item.setId(expectedId);

            // Assert
            assertThat(item.getId()).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("Should set and get type correctly")
        void shouldSetAndGetType() {
            // Arrange
            String expectedType = "story";

            // Act
            item.setType(expectedType);

            // Assert
            assertThat(item.getType()).isEqualTo(expectedType);
        }
    }

    @Nested
    @DisplayName("Story-specific Properties")
    class StoryTests {

        @Test
        @DisplayName("Should handle story with title and url")
        void shouldHandleStoryWithTitleAndUrl() {
            // Arrange
            String title = "Example Story Title";
            String url = "https://example.com";
            Integer score = 100;

            // Act
            item.setTitle(title);
            item.setUrl(url);
            item.setScore(score);

            // Assert
            assertThat(item.getTitle()).isEqualTo(title);
            assertThat(item.getUrl()).isEqualTo(url);
            assertThat(item.getScore()).isEqualTo(score);
        }

        @Test
        @DisplayName("Should handle descendants count")
        void shouldHandleDescendantsCount() {
            // Arrange
            Integer expectedDescendants = 42;

            // Act
            item.setDescendants(expectedDescendants);

            // Assert
            assertThat(item.getDescendants()).isEqualTo(expectedDescendants);
        }
    }

    @Nested
    @DisplayName("Comment-specific Properties")
    class CommentTests {

        @Test
        @DisplayName("Should handle comment with parent and text")
        void shouldHandleCommentWithParentAndText() {
            // Arrange
            Long parentId = 98765L;
            String text = "This is a comment";

            // Act
            item.setParent(parentId);
            item.setText(text);

            // Assert
            assertThat(item.getParent()).isEqualTo(parentId);
            assertThat(item.getText()).isEqualTo(text);
        }
    }

    @Nested
    @DisplayName("Array Properties Tests")
    class ArrayTests {

        @Test
        @DisplayName("Should handle kids array")
        void shouldHandleKidsArray() {
            // Arrange
            Long[] kids = {1L, 2L, 3L, 4L};

            // Act
            item.setKids(kids);

            // Assert
            assertThat(item.getKids()).isNotNull();
            assertThat(item.getKids()).hasSize(4);
            assertThat(item.getKids()).containsExactly(1L, 2L, 3L, 4L);
        }

        @Test
        @DisplayName("Should handle empty kids array")
        void shouldHandleEmptyKidsArray() {
            // Arrange
            Long[] emptyKids = {};

            // Act
            item.setKids(emptyKids);

            // Assert
            assertThat(item.getKids()).isEmpty();
        }

        @Test
        @DisplayName("Should handle parts array for polls")
        void shouldHandlePartsArray() {
            // Arrange
            Long[] parts = {100L, 101L};

            // Act
            item.setParts(parts);

            // Assert
            assertThat(item.getParts()).containsExactly(100L, 101L);
        }
    }

    @Nested
    @DisplayName("Boolean Properties Tests")
    class BooleanTests {

        @Test
        @DisplayName("Should handle deleted flag")
        void shouldHandleDeletedFlag() {
            // Act
            item.setDeleted(true);

            // Assert
            assertThat(item.getDeleted()).isTrue();
        }

        @Test
        @DisplayName("Should handle dead flag")
        void shouldHandleDeadFlag() {
            // Act
            item.setDead(false);

            // Assert
            assertThat(item.getDead()).isFalse();
        }
    }

    @Nested
    @DisplayName("Timestamp and User Tests")
    class TimestampAndUserTests {

        @Test
        @DisplayName("Should handle timestamp")
        void shouldHandleTimestamp() {
            // Arrange
            Long timestamp = System.currentTimeMillis() / 1000;

            // Act
            item.setTime(timestamp);

            // Assert
            assertThat(item.getTime()).isEqualTo(timestamp);
        }

        @Test
        @DisplayName("Should handle author username")
        void shouldHandleAuthor() {
            // Arrange
            String author = "testuser";

            // Act
            item.setBy(author);

            // Assert
            assertThat(item.getBy()).isEqualTo(author);
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate valid toString output")
        void shouldGenerateValidToStringOutput() {
            // Arrange
            item.setId(123L);
            item.setType("story");
            item.setTitle("Test Title");

            // Act
            String result = item.toString();

            // Assert
            assertThat(result).contains("Item{");
            assertThat(result).contains("id=123");
            assertThat(result).contains("type='story'");
            assertThat(result).contains("title='Test Title'");
        }

        @Test
        @DisplayName("Should handle null kids in toString")
        void shouldHandleNullKidsInToString() {
            // Arrange
            item.setId(456L);

            // Act
            String result = item.toString();

            // Assert
            assertThat(result).contains("kids=null");
        }
    }

    @Nested
    @DisplayName("Integration Tests - Full Object")
    class IntegrationTests {

        @Test
        @DisplayName("Should create a complete story item")
        void shouldCreateCompleteStoryItem() {
            // Arrange & Act
            item.setId(8863L);
            item.setDeleted(false);
            item.setType("story");
            item.setBy("dhouston");
            item.setTime(1175714200L);
            item.setTitle("My YC app: Dropbox - Throw away your USB drive");
            item.setUrl("http://www.getdropbox.com/u/2/screencast.html");
            item.setScore(111);
            item.setDescendants(71);
            item.setKids(new Long[]{8952L, 9224L, 8917L});

            // Assert - verify all properties are set correctly
            assertAll("Complete story item",
                () -> assertThat(item.getId()).isEqualTo(8863L),
                () -> assertThat(item.getDeleted()).isFalse(),
                () -> assertThat(item.getType()).isEqualTo("story"),
                () -> assertThat(item.getBy()).isEqualTo("dhouston"),
                () -> assertThat(item.getTime()).isEqualTo(1175714200L),
                () -> assertThat(item.getTitle()).isEqualTo("My YC app: Dropbox - Throw away your USB drive"),
                () -> assertThat(item.getUrl()).isEqualTo("http://www.getdropbox.com/u/2/screencast.html"),
                () -> assertThat(item.getScore()).isEqualTo(111),
                () -> assertThat(item.getDescendants()).isEqualTo(71),
                () -> assertThat(item.getKids()).hasSize(3)
            );
        }

        @Test
        @DisplayName("Should create a complete comment item")
        void shouldCreateCompleteCommentItem() {
            // Arrange & Act
            item.setId(2921983L);
            item.setType("comment");
            item.setBy("norvig");
            item.setParent(2921506L);
            item.setText("Aw shucks, guys ... you make me blush with your compliments.");
            item.setTime(1314211127L);

            // Assert
            assertAll("Complete comment item",
                () -> assertThat(item.getId()).isEqualTo(2921983L),
                () -> assertThat(item.getType()).isEqualTo("comment"),
                () -> assertThat(item.getBy()).isEqualTo("norvig"),
                () -> assertThat(item.getParent()).isEqualTo(2921506L),
                () -> assertThat(item.getText()).isNotEmpty(),
                () -> assertThat(item.getTime()).isPositive()
            );
        }
    }
}
