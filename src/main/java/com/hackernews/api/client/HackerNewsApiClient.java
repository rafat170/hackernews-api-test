package com.hackernews.api.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hackernews.api.model.Item;
import com.hackernews.api.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Client for interacting with the Hacker News API.
 *
 * API Documentation: https://github.com/HackerNews/API
 */
public class HackerNewsApiClient {
    private static final String BASE_URL = "https://hacker-news.firebaseio.com/v0";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final Gson gson;

    /**
     * Creates a new HackerNewsApiClient with default settings.
     */
    public HackerNewsApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(DEFAULT_TIMEOUT)
                .build();
        this.gson = new Gson();
    }

    /**
     * Creates a new HackerNewsApiClient with a custom HttpClient.
     *
     * @param httpClient the HTTP client to use
     */
    public HackerNewsApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.gson = new Gson();
    }

    /**
     * Gets an item by its ID.
     *
     * @param itemId the ID of the item
     * @return the Item object, or null if not found
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public Item getItem(long itemId) throws IOException, InterruptedException {
        String url = String.format("%s/item/%d.json", BASE_URL, itemId);
        String response = sendGetRequest(url);

        if (response == null || response.equals("null")) {
            return null;
        }

        return gson.fromJson(response, Item.class);
    }

    /**
     * Gets a user by their username.
     *
     * @param username the username
     * @return the User object, or null if not found
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public User getUser(String username) throws IOException, InterruptedException {
        String url = String.format("%s/user/%s.json", BASE_URL, username);
        String response = sendGetRequest(url);

        if (response == null || response.equals("null")) {
            return null;
        }

        return gson.fromJson(response, User.class);
    }

    /**
     * Gets the IDs of the top stories.
     *
     * @return list of item IDs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Long> getTopStories() throws IOException, InterruptedException {
        String url = BASE_URL + "/topstories.json";
        String response = sendGetRequest(url);
        return gson.fromJson(response, new TypeToken<List<Long>>(){}.getType());
    }

    /**
     * Gets the IDs of the newest stories.
     *
     * @return list of item IDs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Long> getNewStories() throws IOException, InterruptedException {
        String url = BASE_URL + "/newstories.json";
        String response = sendGetRequest(url);
        return gson.fromJson(response, new TypeToken<List<Long>>(){}.getType());
    }

    /**
     * Gets the IDs of the best stories.
     *
     * @return list of item IDs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Long> getBestStories() throws IOException, InterruptedException {
        String url = BASE_URL + "/beststories.json";
        String response = sendGetRequest(url);
        return gson.fromJson(response, new TypeToken<List<Long>>(){}.getType());
    }

    /**
     * Gets the IDs of Ask HN stories.
     *
     * @return list of item IDs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Long> getAskStories() throws IOException, InterruptedException {
        String url = BASE_URL + "/askstories.json";
        String response = sendGetRequest(url);
        return gson.fromJson(response, new TypeToken<List<Long>>(){}.getType());
    }

    /**
     * Gets the IDs of Show HN stories.
     *
     * @return list of item IDs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Long> getShowStories() throws IOException, InterruptedException {
        String url = BASE_URL + "/showstories.json";
        String response = sendGetRequest(url);
        return gson.fromJson(response, new TypeToken<List<Long>>(){}.getType());
    }

    /**
     * Gets the IDs of job stories.
     *
     * @return list of item IDs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Long> getJobStories() throws IOException, InterruptedException {
        String url = BASE_URL + "/jobstories.json";
        String response = sendGetRequest(url);
        return gson.fromJson(response, new TypeToken<List<Long>>(){}.getType());
    }

    /**
     * Gets the current largest item ID.
     *
     * @return the max item ID
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public Long getMaxItemId() throws IOException, InterruptedException {
        String url = BASE_URL + "/maxitem.json";
        String response = sendGetRequest(url);
        return gson.fromJson(response, Long.class);
    }

    /**
     * Gets recently changed items and profiles.
     *
     * @return list of item IDs and usernames that have changed
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public Updates getUpdates() throws IOException, InterruptedException {
        String url = BASE_URL + "/updates.json";
        String response = sendGetRequest(url);
        return gson.fromJson(response, Updates.class);
    }

    /**
     * Sends a GET request to the specified URL.
     *
     * @param url the URL to request
     * @return the response body as a string
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    private String sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(DEFAULT_TIMEOUT)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("HTTP request failed with status code: " + response.statusCode());
        }
    }

    /**
     * Represents updates from the API containing changed items and profiles.
     */
    public static class Updates {
        private List<Long> items;
        private List<String> profiles;

        public List<Long> getItems() {
            return items;
        }

        public void setItems(List<Long> items) {
            this.items = items;
        }

        public List<String> getProfiles() {
            return profiles;
        }

        public void setProfiles(List<String> profiles) {
            this.profiles = profiles;
        }

        @Override
        public String toString() {
            return "Updates{" +
                    "items=" + (items != null ? items.size() + " items" : "null") +
                    ", profiles=" + (profiles != null ? profiles.size() + " profiles" : "null") +
                    '}';
        }
    }
}
