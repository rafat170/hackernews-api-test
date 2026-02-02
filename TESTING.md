# Testing Guide - Hacker News API Client

This guide provides instructions for running the comprehensive test suite for the Hacker News API Client.

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

### 2. Build the Project

```bash
mvn clean install
```

This will:
- Download all dependencies (JUnit 5, Mockito, AssertJ)
- Compile the source code
- Run all tests
- Create the JAR file in `target/`

### 3. Run All Tests

```bash
mvn test
```

**Expected Output**: 105 tests passing

## Test Suite Overview

### Complete Test Inventory (105 Tests Total)

| Test File | Tests | Description |
|-----------|-------|-------------|
| **AApiHealthCheckTest** | 5 | API health checks (runs first to verify API availability) |
| **TopStoriesApiTest** | 23 | Top Stories API integration tests with error handling and boundary tests |
| **ItemsApiTest** | 40 | Items API, workflow tests, comprehensive error handling, and boundary tests |
| **HackerNewsApiClientTest** | 20 | Complete API client coverage |
| **ItemTest** | 17 | Model validation tests |

### Test Execution Order

Tests run in alphabetical order (configured via Maven Surefire). The health check test ([AApiHealthCheckTest.java](src/test/java/com/hackernews/api/client/AApiHealthCheckTest.java)) runs first to verify API availability. If health checks pass, the remaining integration tests proceed. If the API is unavailable, integration tests are skipped with a helpful message.

---

## Detailed Test Listings

### 1. AApiHealthCheckTest (5 tests)

**Location**: [src/test/java/com/hackernews/api/client/AApiHealthCheckTest.java](src/test/java/com/hackernews/api/client/AApiHealthCheckTest.java)

#### Health Check Tests (5 tests)
- ✓ Should verify API base endpoint is reachable
- ✓ Should verify top stories endpoint is accessible
- ✓ Should verify items endpoint is accessible
- ✓ Should verify all story type endpoints are accessible
- ✓ Should verify API response time is acceptable

**Purpose**: These tests run **first** (alphabetically) to verify the Hacker News API is available before running the full test suite. If any health check fails, the API is considered unavailable and integration tests are skipped.

**Run Command**:
```bash
mvn test -Dtest=AApiHealthCheckTest
```

Or run health checks by tag:
```bash
mvn test -Dgroups=health-check
```

---

### 2. TopStoriesApiTest (23 tests)

**Location**: `src/test/java/com/hackernews/api/client/TopStoriesApiTest.java`

#### Integration Tests (5 tests)
- ✓ Should fetch real top stories from HN API
- ✓ Should fetch and parse top story item
- ✓ Should fetch and validate multiple top stories
- ✓ Should verify top stories have expected properties
- ✓ Should handle fetching non-existent story gracefully

#### Workflow Tests (1 test)
- ✓ Should demonstrate typical top stories usage pattern

#### Error Handling Tests (9 tests)
- ✓ Should return null for extremely large non-existent item ID
- ✓ Should return null for zero item ID
- ✓ Should return null for negative item ID
- ✓ Should handle deleted items gracefully
- ✓ Should validate top stories list is not null
- ✓ Should handle fetching multiple invalid items without throwing exceptions
- ✓ Should verify API returns valid response structure for top stories
- ✓ Should verify item response structure for valid story
- ✓ Should handle mix of valid and invalid item IDs correctly

#### Boundary Tests (6 tests)
- ✓ Should return null for Long.MIN_VALUE item ID
- ✓ Should validate top stories list size is within bounds
- ✓ Should verify all story types from different endpoints
- ✓ Should verify first and last IDs in top stories list are valid
- ✓ Should handle checking items just before and after max item ID
- ✓ Should verify top stories IDs are monotonically increasing

#### Performance Tests (2 tests)
- ✓ Should fetch top stories within reasonable time
- ✓ Should handle concurrent top stories requests

**Run Command**:
```bash
mvn test -Dtest=TopStoriesApiTest
```

---

### 3. ItemsApiTest (40 tests)

**Location**: `src/test/java/com/hackernews/api/client/ItemsApiTest.java`

#### Get Item Tests (4 tests)
- ✓ Should fetch current top story using Items API
- ✓ Should fetch multiple items from top stories
- ✓ Should handle non-existent item ID
- ✓ Should fetch item with all story properties

#### Item Type Tests (2 tests)
- ✓ Should identify story items correctly
- ✓ Should handle different item types from top stories

#### Item Properties Validation (4 tests)
- ✓ Should validate item has valid timestamp
- ✓ Should validate item has valid author
- ✓ Should validate story has score
- ✓ Should validate item with kids has valid kids array

#### Item Comments Tests (2 tests)
- ✓ Should fetch comment item from story
- ✓ Should verify comment structure is valid

#### Story to Comment Workflow Tests (5 tests)
- ✓ Should retrieve top story and its first comment
- ✓ Should retrieve multiple comments from a top story
- ✓ Should validate comment hierarchy and relationships
- ✓ Should handle story with no comments gracefully
- ✓ Should measure performance of story-to-comment retrieval

#### Performance Tests (2 tests)
- ✓ Should fetch item within reasonable time
- ✓ Should fetch multiple items efficiently

#### Error Handling Tests (11 tests)
- ✓ Should handle null response gracefully
- ✓ Should handle deleted item
- ✓ Should return null for zero item ID
- ✓ Should return null for negative item ID
- ✓ Should return null for extremely large item ID
- ✓ Should handle multiple invalid item IDs without throwing exceptions
- ✓ Should validate item response structure
- ✓ Should handle mix of valid and invalid item IDs correctly
- ✓ Should verify all top story IDs return valid items or null
- ✓ Should validate timestamp is within reasonable range
- ✓ Should validate author username format

#### Boundary Tests (10 tests)
- ✓ Should return null for Long.MIN_VALUE
- ✓ Should handle item ID just beyond max item ID
- ✓ Should verify current max item ID is fetchable
- ✓ Should handle very old item IDs from early HN history
- ✓ Should find and validate all item types
- ✓ Should handle item with exactly one comment
- ✓ Should find and validate Ask HN post with no URL
- ✓ Should validate descendants count matches kids array
- ✓ Should test deep comment nesting hierarchy
- ✓ Should handle items with large kids arrays

**Run Command**:
```bash
mvn test -Dtest=ItemsApiTest
```

**Run Story-to-Comment Workflow Only**:
```bash
mvn test -Dtest=ItemsApiTest$StoryToCommentWorkflowTests
```

---

### 4. HackerNewsApiClientTest (20 tests)

**Location**: `src/test/java/com/hackernews/api/client/HackerNewsApiClientTest.java`

#### Constructor Tests (2 tests)
- ✓ Should create client with default HttpClient
- ✓ Should create client with custom HttpClient

#### Get Item Tests (4 tests)
- ✓ Should retrieve item by ID successfully
- ✓ Should return null when item not found
- ✓ Should throw IOException when HTTP request fails
- ✓ Should throw InterruptedException when request is interrupted

#### Get User Tests (2 tests)
- ✓ Should retrieve user by username successfully
- ✓ Should return null when user not found

#### Story List Tests (6 tests)
- ✓ Should retrieve top stories list
- ✓ Should retrieve new stories list
- ✓ Should retrieve best stories list
- ✓ Should retrieve ask stories list
- ✓ Should retrieve show stories list
- ✓ Should retrieve job stories list

#### Max Item ID Tests (1 test)
- ✓ Should retrieve maximum item ID

#### Updates Tests (2 tests)
- ✓ Should retrieve updates with items and profiles
- ✓ Should handle empty updates

#### Error Handling Tests (2 tests)
- ✓ Should handle 500 server error
- ✓ Should handle network timeout

#### Integration Tests (1 test)
- ✓ Should fetch item and verify all fields are parsed

**Run Command**:
```bash
mvn test -Dtest=HackerNewsApiClientTest
```

---

### 5. ItemTest (17 tests)

**Location**: `src/test/java/com/hackernews/api/model/ItemTest.java`

#### Constructor Tests (3 tests)
- ✓ Should create item with null properties by default
- ✓ Should set and get id correctly
- ✓ Should set and get type correctly

#### Story Tests (2 tests)
- ✓ Should handle story with title and url
- ✓ Should handle descendants count

#### Comment Tests (1 test)
- ✓ Should handle comment with parent and text

#### Array Tests (3 tests)
- ✓ Should handle kids array
- ✓ Should handle empty kids array
- ✓ Should handle parts array for polls

#### Boolean Tests (2 tests)
- ✓ Should handle deleted flag
- ✓ Should handle dead flag

#### Timestamp and User Tests (2 tests)
- ✓ Should handle timestamp
- ✓ Should handle author username

#### toString Tests (2 tests)
- ✓ Should generate valid toString output
- ✓ Should handle null kids in toString

#### Integration Tests (2 tests)
- ✓ Should create a complete story item
- ✓ Should create a complete comment item

**Run Command**:
```bash
mvn test -Dtest=ItemTest
```

---

## Running Tests by Category

### Run All Integration Tests
```bash
mvn test -Dgroups=integration
```

### Run All Performance Tests
```bash
mvn test -Dgroups=performance
```

### Run All Error Handling Tests
```bash
mvn test -Dgroups=error-handling
```

### Run All Boundary Tests
```bash
mvn test -Dgroups=boundary
```

### Run Multiple Test Classes
```bash
mvn test -Dtest=TopStoriesApiTest,ItemsApiTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=ItemsApiTest#shouldRetrieveTopStoryAndFirstComment
```

---

## Common Commands

### Clean and Run All Tests
```bash
mvn clean test
```

### Run Tests with Verbose Output
```bash
mvn test -X
```

### Skip Tests (for build only)
```bash
mvn install -DskipTests
```

### Run Tests and Continue on Failure
```bash
mvn test -Dmaven.test.failure.ignore=true
```

---

## Understanding Test Output

### Successful Test Run
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.hackernews.api.client.ItemsApiTest
✓ Top story fetched: We can't send mail farther than 500 miles (2002)
  ID: 46805665
  Type: story
  Author: giancarlostoro
  Score: 109

[INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Test Reports Location
After running tests, find detailed reports at:
- Console output (immediate)
- `target/surefire-reports/*.txt` (text format)
- `target/surefire-reports/*.xml` (XML format)

---

## Test Characteristics

### Integration Tests
- All tests call the **live Hacker News API**
- Tests use **real, current data** from the API
- No mocking - actual HTTP requests are made
- Tests may vary slightly based on current HN content

### Performance Benchmarks
- Top Stories API: < 2 seconds
- Single Item fetch: < 1 second
- Story-to-comment workflow: < 3 seconds
- Average item fetch: < 100ms

### Test Data
Tests automatically:
- Fetch current top stories
- Retrieve real items and comments
- Validate against live API responses
- Handle edge cases (deleted items, missing comments, etc.)

---

## Troubleshooting

### Tests Fail to Compile
```bash
# Ensure Java 11+ is installed
java -version

# Reload Maven dependencies
mvn clean install -U
```

### Tests Timeout or Fail
```bash
# Check internet connection (tests require live API access)
curl https://hacker-news.firebaseio.com/v0/topstories.json

# Run tests with longer timeout
mvn test -Dsurefire.timeout=300
```

### Dependency Issues
```bash
# Force update dependencies
mvn clean install -U

# Verify pom.xml has correct dependencies
mvn dependency:tree
```

---

## Test Examples

### Example: Running Story-to-Comment Workflow Test

```bash
mvn test -Dtest=ItemsApiTest$StoryToCommentWorkflowTests#shouldRetrieveTopStoryAndFirstComment
```

**Expected Output**:
```
=== Story to Comment Workflow ===
Step 1: Retrieved 500 top story IDs
Step 2: Found story with comments after checking 1 stories
  Story ID: 46805665
  Title: We can't send mail farther than 500 miles (2002)
  Author: giancarlostoro
  Score: 109
  Comment count: 11
Step 3: Retrieved first comment
  Comment ID: 46806161
  Comment Author: rented_mule
  Comment Parent: 46805665
✓ Successfully completed story-to-comment workflow
```

---

## Additional Resources

- [Test Template Guide](src/test/resources/TEST_TEMPLATE_GUIDE.md) - Detailed testing patterns and best practices
- [Hacker News API Documentation](https://github.com/HackerNews/API) - Official API reference
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)

---

## Summary

- **Total Tests**: 105
- **Test Execution Time**: ~90-120 seconds
- **API Endpoints Tested**: Items, Users, Top Stories, New Stories, Best Stories, Ask, Show, Jobs
- **Test Coverage**: API health checks, integration tests, performance tests, comprehensive error handling, boundary tests, workflow validation, response structure validation
- **Health Check Tests**: 5 smoke tests to verify API availability before running integration tests
- **Error Path Tests**: 20 comprehensive error handling and validation tests
- **Boundary Tests**: 16 boundary and edge case tests
- **Return Code Validation**: Included in response structure validation tests

**Test Categories by Tag**:
- `health-check` - API health and availability tests (run first)
- `integration` - Integration tests calling live API
- `performance` - Performance benchmark tests
- `error-handling` - Error path and validation tests
- `boundary` - Boundary and edge case tests

**Test Execution Strategy**:
Maven Surefire is configured to run tests in alphabetical order. [AApiHealthCheckTest](src/test/java/com/hackernews/api/client/AApiHealthCheckTest.java) runs first to verify API availability. If health checks pass, integration tests proceed. If the API is unavailable, integration tests skip automatically with a helpful message.

**Quick Run**: `mvn test` - Runs all 105 tests and validates complete API functionality with comprehensive error handling and boundary testing!
