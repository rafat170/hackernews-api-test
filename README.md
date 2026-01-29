# Hacker News API Client

A Java client library for interacting with the Hacker News API.

## Features

- Object-oriented design with clean, reusable classes
- Comprehensive API coverage (items, users, stories, etc.)
- Built with Java 11+ HttpClient
- JSON parsing with Gson
- Type-safe model classes

## Project Structure

```
src/main/java/com/hackernews/api/
├── client/
│   └── HackerNewsApiClient.java    # Main API client
├── model/
│   ├── Item.java                    # Item model (stories, comments, etc.)
│   └── User.java                    # User model
└── example/
    └── Example.java                 # Example usage
```

## Installation

### Prerequisites

- Java 11 or higher
- Maven 3.6+

### Build

```bash
mvn clean install
```

## Usage

### Creating a Client

```java
HackerNewsApiClient client = new HackerNewsApiClient();
```

### Get an Item

```java
Item item = client.getItem(8863);
System.out.println("Title: " + item.getTitle());
```

### Get Top Stories

```java
List<Long> topStories = client.getTopStories();
for (Long storyId : topStories.subList(0, 10)) {
    Item story = client.getItem(storyId);
    System.out.println(story.getTitle());
}
```

### Get User Information

```java
User user = client.getUser("jl");
System.out.println("Karma: " + user.getKarma());
```

### Available Methods

- `getItem(long itemId)` - Get a specific item by ID
- `getUser(String username)` - Get a user by username
- `getTopStories()` - Get top story IDs
- `getNewStories()` - Get new story IDs
- `getBestStories()` - Get best story IDs
- `getAskStories()` - Get Ask HN story IDs
- `getShowStories()` - Get Show HN story IDs
- `getJobStories()` - Get job story IDs
- `getMaxItemId()` - Get the current largest item ID
- `getUpdates()` - Get recently changed items and profiles

## Running the Example

```bash
mvn clean compile exec:java -Dexec.mainClass="com.hackernews.api.example.Example"
```

## API Reference

This client uses the official Hacker News API: https://github.com/HackerNews/API

## Dependencies

- Gson 2.10.1 - JSON parsing
- Java 11+ HttpClient - HTTP requests
- JUnit 5 - Testing (test scope)

## License

See LICENSE file for details.