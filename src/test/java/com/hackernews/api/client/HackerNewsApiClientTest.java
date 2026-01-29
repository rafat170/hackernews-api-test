package com.hackernews.api.client;

import com.hackernews.api.model.Item;
import com.hackernews.api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HackerNewsApiClient.
 * This template demonstrates mocking HTTP clients and testing API interactions.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HackerNewsApiClient Tests")
class HackerNewsApiClientTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private HackerNewsApiClient client;

    @BeforeEach
    void setUp() {
        // Initialize the client with mocked HttpClient
        client = new HackerNewsApiClient(mockHttpClient);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create client with default HttpClient")
        void shouldCreateClientWithDefaultHttpClient() {
            // Act
            HackerNewsApiClient defaultClient = new HackerNewsApiClient();

            // Assert
            assertThat(defaultClient).isNotNull();
        }

        @Test
        @DisplayName("Should create client with custom HttpClient")
        void shouldCreateClientWithCustomHttpClient() {
            // Act
            HackerNewsApiClient customClient = new HackerNewsApiClient(mockHttpClient);

            // Assert
            assertThat(customClient).isNotNull();
        }
    }

    @Nested
    @DisplayName("Get Item Tests")
    class GetItemTests {

        @Test
        @DisplayName("Should retrieve item by ID successfully")
        void shouldRetrieveItemById() throws IOException, InterruptedException {
            // Arrange
            long itemId = 8863L;
            String jsonResponse = "{\"id\":8863,\"type\":\"story\",\"by\":\"dhouston\"," +
                    "\"time\":1175714200,\"title\":\"My YC app: Dropbox\"," +
                    "\"url\":\"http://www.getdropbox.com/u/2/screencast.html\"," +
                    "\"score\":111,\"descendants\":71}";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            Item result = client.getItem(itemId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(8863L);
            assertThat(result.getType()).isEqualTo("story");
            assertThat(result.getBy()).isEqualTo("dhouston");
            assertThat(result.getTitle()).isEqualTo("My YC app: Dropbox");
            assertThat(result.getScore()).isEqualTo(111);

            // Verify HTTP client was called
            verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        @DisplayName("Should return null when item not found")
        void shouldReturnNullWhenItemNotFound() throws IOException, InterruptedException {
            // Arrange
            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn("null");
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            Item result = client.getItem(999999L);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should throw IOException when HTTP request fails")
        void shouldThrowIOExceptionWhenRequestFails() throws IOException, InterruptedException {
            // Arrange
            when(mockHttpResponse.statusCode()).thenReturn(404);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act & Assert
            assertThatThrownBy(() -> client.getItem(123L))
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("HTTP request failed with status code: 404");
        }

        @Test
        @DisplayName("Should throw InterruptedException when request is interrupted")
        void shouldThrowInterruptedExceptionWhenInterrupted() throws IOException, InterruptedException {
            // Arrange
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new InterruptedException("Request interrupted"));

            // Act & Assert
            assertThatThrownBy(() -> client.getItem(123L))
                    .isInstanceOf(InterruptedException.class)
                    .hasMessageContaining("Request interrupted");
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should retrieve user by username successfully")
        void shouldRetrieveUserByUsername() throws IOException, InterruptedException {
            // Arrange
            String username = "jl";
            String jsonResponse = "{\"id\":\"jl\",\"created\":1173923446,\"karma\":2937}";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            User result = client.getUser(username);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("jl");
            assertThat(result.getCreated()).isEqualTo(1173923446L);
            assertThat(result.getKarma()).isEqualTo(2937);

            verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        @DisplayName("Should return null when user not found")
        void shouldReturnNullWhenUserNotFound() throws IOException, InterruptedException {
            // Arrange
            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn("null");
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            User result = client.getUser("nonexistentuser");

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Story List Tests")
    class StoryListTests {

        @Test
        @DisplayName("Should retrieve top stories list")
        void shouldRetrieveTopStories() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "[123,456,789,101112]";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            List<Long> result = client.getTopStories();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(4);
            assertThat(result).containsExactly(123L, 456L, 789L, 101112L);
        }

        @Test
        @DisplayName("Should retrieve new stories list")
        void shouldRetrieveNewStories() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "[111,222,333]";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            List<Long> result = client.getNewStories();

            // Assert
            assertThat(result).containsExactly(111L, 222L, 333L);
        }

        @Test
        @DisplayName("Should retrieve best stories list")
        void shouldRetrieveBestStories() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "[444,555]";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            List<Long> result = client.getBestStories();

            // Assert
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Should retrieve ask stories list")
        void shouldRetrieveAskStories() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "[666,777,888]";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            List<Long> result = client.getAskStories();

            // Assert
            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("Should retrieve show stories list")
        void shouldRetrieveShowStories() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "[999]";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            List<Long> result = client.getShowStories();

            // Assert
            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("Should retrieve job stories list")
        void shouldRetrieveJobStories() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "[1000,2000]";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            List<Long> result = client.getJobStories();

            // Assert
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Max Item ID Tests")
    class MaxItemIdTests {

        @Test
        @DisplayName("Should retrieve maximum item ID")
        void shouldRetrieveMaxItemId() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "37763867";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            Long result = client.getMaxItemId();

            // Assert
            assertThat(result).isEqualTo(37763867L);
            assertThat(result).isPositive();
        }
    }

    @Nested
    @DisplayName("Updates Tests")
    class UpdatesTests {

        @Test
        @DisplayName("Should retrieve updates with items and profiles")
        void shouldRetrieveUpdates() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "{\"items\":[123,456,789],\"profiles\":[\"user1\",\"user2\"]}";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            HackerNewsApiClient.Updates result = client.getUpdates();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getItems()).hasSize(3);
            assertThat(result.getProfiles()).hasSize(2);
            assertThat(result.getItems()).containsExactly(123L, 456L, 789L);
            assertThat(result.getProfiles()).containsExactly("user1", "user2");
        }

        @Test
        @DisplayName("Should handle empty updates")
        void shouldHandleEmptyUpdates() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "{\"items\":[],\"profiles\":[]}";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            HackerNewsApiClient.Updates result = client.getUpdates();

            // Assert
            assertThat(result.getItems()).isEmpty();
            assertThat(result.getProfiles()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle 500 server error")
        void shouldHandle500Error() throws IOException, InterruptedException {
            // Arrange
            when(mockHttpResponse.statusCode()).thenReturn(500);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act & Assert
            assertThatThrownBy(() -> client.getItem(123L))
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("500");
        }

        @Test
        @DisplayName("Should handle network timeout")
        void shouldHandleNetworkTimeout() throws IOException, InterruptedException {
            // Arrange
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IOException("Connection timeout"));

            // Act & Assert
            assertThatThrownBy(() -> client.getTopStories())
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("Connection timeout");
        }
    }

    @Nested
    @DisplayName("Integration-style Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should fetch item and verify all fields are parsed")
        void shouldFetchItemAndVerifyAllFields() throws IOException, InterruptedException {
            // Arrange
            String jsonResponse = "{" +
                    "\"id\":123," +
                    "\"deleted\":false," +
                    "\"type\":\"story\"," +
                    "\"by\":\"testuser\"," +
                    "\"time\":1234567890," +
                    "\"text\":\"Test text\"," +
                    "\"dead\":false," +
                    "\"parent\":456," +
                    "\"kids\":[789,101]," +
                    "\"url\":\"https://example.com\"," +
                    "\"score\":100," +
                    "\"title\":\"Test Title\"," +
                    "\"descendants\":50" +
                    "}";

            when(mockHttpResponse.statusCode()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(jsonResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpResponse);

            // Act
            Item result = client.getItem(123L);

            // Assert - verify all fields are correctly deserialized
            assertAll("All item fields",
                    () -> assertThat(result.getId()).isEqualTo(123L),
                    () -> assertThat(result.getDeleted()).isFalse(),
                    () -> assertThat(result.getType()).isEqualTo("story"),
                    () -> assertThat(result.getBy()).isEqualTo("testuser"),
                    () -> assertThat(result.getTime()).isEqualTo(1234567890L),
                    () -> assertThat(result.getText()).isEqualTo("Test text"),
                    () -> assertThat(result.getDead()).isFalse(),
                    () -> assertThat(result.getParent()).isEqualTo(456L),
                    () -> assertThat(result.getKids()).containsExactly(789L, 101L),
                    () -> assertThat(result.getUrl()).isEqualTo("https://example.com"),
                    () -> assertThat(result.getScore()).isEqualTo(100),
                    () -> assertThat(result.getTitle()).isEqualTo("Test Title"),
                    () -> assertThat(result.getDescendants()).isEqualTo(50)
            );
        }
    }
}
