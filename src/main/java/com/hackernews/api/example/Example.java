package com.hackernews.api.example;

import com.hackernews.api.client.HackerNewsApiClient;
import com.hackernews.api.model.Item;
import com.hackernews.api.model.User;

import java.io.IOException;
import java.util.List;

/**
 * Example usage of the Hacker News API Client.
 */
public class Example {

    public static void main(String[] args) {
        HackerNewsApiClient client = new HackerNewsApiClient();

        try {
            System.out.println("=== Hacker News API Client Example ===\n");

            // Get top stories
            System.out.println("Fetching top stories...");
            List<Long> topStories = client.getTopStories();
            System.out.println("Found " + topStories.size() + " top stories");

            // Get details of the first 5 top stories
            System.out.println("\nTop 5 stories:");
            for (int i = 0; i < Math.min(5, topStories.size()); i++) {
                Long storyId = topStories.get(i);
                Item story = client.getItem(storyId);

                if (story != null) {
                    System.out.println("\n" + (i + 1) + ". " + story.getTitle());
                    System.out.println("   By: " + story.getBy());
                    System.out.println("   Score: " + story.getScore());
                    System.out.println("   URL: " + story.getUrl());
                    System.out.println("   Comments: " + story.getDescendants());
                }
            }

            // Get a specific item
            System.out.println("\n\nFetching specific item (ID: 8863)...");
            Item item = client.getItem(8863);
            if (item != null) {
                System.out.println("Title: " + item.getTitle());
                System.out.println("Type: " + item.getType());
                System.out.println("Author: " + item.getBy());
            }

            // Get user information
            System.out.println("\n\nFetching user information (jl)...");
            User user = client.getUser("jl");
            if (user != null) {
                System.out.println("User ID: " + user.getId());
                System.out.println("Karma: " + user.getKarma());
                System.out.println("Created: " + user.getCreated());
                if (user.getSubmitted() != null) {
                    System.out.println("Submissions: " + user.getSubmitted().length);
                }
            }

            // Get Ask HN stories
            System.out.println("\n\nFetching Ask HN stories...");
            List<Long> askStories = client.getAskStories();
            System.out.println("Found " + askStories.size() + " Ask HN stories");

            // Get the first Ask HN story
            if (!askStories.isEmpty()) {
                Item askStory = client.getItem(askStories.get(0));
                if (askStory != null) {
                    System.out.println("\nLatest Ask HN:");
                    System.out.println("Title: " + askStory.getTitle());
                    System.out.println("By: " + askStory.getBy());
                }
            }

            // Get max item ID
            System.out.println("\n\nFetching max item ID...");
            Long maxItemId = client.getMaxItemId();
            System.out.println("Max Item ID: " + maxItemId);

            System.out.println("\n=== Example completed successfully ===");

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Request interrupted: " + e.getMessage());
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
