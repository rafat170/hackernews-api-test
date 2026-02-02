# Hacker News API Test Suite

A comprehensive test suite for the Hacker News API with **105 integration tests** validating all API functionality.

## Overview

This project is a pure test suite designed to validate the Hacker News API endpoints, error handling, boundary conditions, and performance. All tests run against the live Hacker News API at `https://hacker-news.firebaseio.com/v0`.

## Features

- **105 comprehensive tests** covering all API endpoints
- **API health checks** that verify API availability before running tests
- **Integration tests** against the live Hacker News API
- **Error handling tests** for edge cases and invalid inputs
- **Boundary tests** for extreme values and limits
- **Performance tests** with timing benchmarks
- **Workflow tests** demonstrating real-world usage patterns

## Project Structure

```
src/test/java/com/hackernews/api/
├── client/
│   ├── AApiHealthCheckTest.java     # API health checks (runs first)
│   ├── HackerNewsApiClient.java     # API client implementation
│   ├── HackerNewsApiClientTest.java # API client tests
│   ├── ItemsApiTest.java            # Items API tests
│   └── TopStoriesApiTest.java       # Top Stories API tests
└── model/
    ├── Item.java                     # Item model (stories, comments, etc.)
    ├── ItemTest.java                 # Item model tests
    └── User.java                     # User model
```

## Prerequisites

- **Java 11** or higher
- **Maven 3.6+**
- **Git**
- Internet connection (tests call the live Hacker News API)

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/rafat170/hackernews-api-test.git
cd hackernews-api-test
```

### 2. Run All Tests

```bash
mvn test
```

**Expected Output**: 105 tests passing

### 3. Run Specific Test Categories

```bash
# Run only health checks
mvn test -Dgroups=health-check

# Run integration tests
mvn test -Dgroups=integration

# Run performance tests
mvn test -Dgroups=performance

# Run error handling tests
mvn test -Dgroups=error-handling

# Run boundary tests
mvn test -Dgroups=boundary
```

## Test Suite Breakdown

| Test File | Tests | Description |
|-----------|-------|-------------|
| **AApiHealthCheckTest** | 5 | API health checks (runs first to verify API availability) |
| **TopStoriesApiTest** | 23 | Top Stories API integration tests with error handling and boundary tests |
| **ItemsApiTest** | 40 | Items API, workflow tests, comprehensive error handling, and boundary tests |
| **HackerNewsApiClientTest** | 20 | Complete API client coverage |
| **ItemTest** | 17 | Model validation tests |
| **TOTAL** | **105** | **Complete API coverage** |

## Test Categories

### Health Checks (5 tests)
- Verify API base endpoint is reachable
- Check all story type endpoints are accessible
- Validate API response time

### Integration Tests (48 tests)
- Real API calls with live data
- Top stories, items, users, and updates endpoints
- Workflow validations (story-to-comment traversal)

### Error Handling Tests (20 tests)
- Invalid item IDs (negative, zero, Long.MAX_VALUE)
- Null and deleted item handling
- Response structure validation
- Mixed valid/invalid ID handling

### Boundary Tests (16 tests)
- Extreme values (Long.MIN_VALUE, Long.MAX_VALUE)
- Max item ID boundaries
- Very old items from early HN history
- All item types validation
- Deep comment nesting
- Large kids arrays

### Performance Tests (4 tests)
- Response time benchmarks
- Concurrent request handling
- Story-to-comment workflow performance

## Test Execution Strategy

Tests run in **alphabetical order** (configured via Maven Surefire):

1. **AApiHealthCheckTest** runs first to verify API availability
2. If health checks pass, remaining integration tests proceed
3. If API is unavailable, integration tests skip automatically with a helpful message

This ensures graceful failure when the API is down instead of cascading timeout errors.

## API Endpoints Tested

- **Items**: `GET /v0/item/{id}.json`
- **Users**: `GET /v0/user/{username}.json`
- **Top Stories**: `GET /v0/topstories.json`
- **New Stories**: `GET /v0/newstories.json`
- **Best Stories**: `GET /v0/beststories.json`
- **Ask Stories**: `GET /v0/askstories.json`
- **Show Stories**: `GET /v0/showstories.json`
- **Job Stories**: `GET /v0/jobstories.json`
- **Max Item**: `GET /v0/maxitem.json`
- **Updates**: `GET /v0/updates.json`

## Running Specific Tests

### Run a single test class

```bash
mvn test -Dtest=AApiHealthCheckTest
mvn test -Dtest=TopStoriesApiTest
mvn test -Dtest=ItemsApiTest
```

### Run a specific test method

```bash
mvn test -Dtest=ItemsApiTest#shouldRetrieveTopStoryAndFirstComment
```

### Run multiple test classes

```bash
mvn test -Dtest=TopStoriesApiTest,ItemsApiTest
```

## Documentation

For detailed testing documentation, including complete test listings and examples, see [TESTING.md](TESTING.md).

## Dependencies

- **Gson 2.10.1** - JSON parsing
- **Java 11+ HttpClient** - HTTP requests
- **JUnit 5** - Testing framework
- **Mockito 5.5.0** - Mocking framework
- **AssertJ 3.24.2** - Fluent assertions

## API Reference

This test suite validates the official Hacker News API: https://github.com/HackerNews/API

## Test Results

All **105 tests** pass successfully:

```
[INFO] Results:
[INFO]
[INFO] Tests run: 105, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

## License

See LICENSE file for details.
