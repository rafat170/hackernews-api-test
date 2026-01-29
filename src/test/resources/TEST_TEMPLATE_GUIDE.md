# Unit Test Template Guide

This guide explains the testing patterns and templates available in this project.

## Running Tests

```bash
# Run all tests
mvn test

# Run tests with verbose output
mvn test -X

# Run a specific test class
mvn test -Dtest=ItemTest

# Run a specific test method
mvn test -Dtest=ItemTest#shouldSetAndGetId

# Run tests and generate coverage report (if jacoco is configured)
mvn clean test jacoco:report
```

## Test Structure

Tests are organized in `src/test/java` mirroring the `src/main/java` structure:

```
src/test/java/
└── com/hackernews/api/
    ├── model/
    │   └── ItemTest.java          # Model/POJO tests
    └── client/
        └── HackerNewsApiClientTest.java  # API client tests
```

## Testing Frameworks

### JUnit 5 (Jupiter)
- Modern testing framework with improved assertions and annotations
- `@Test` - marks test methods
- `@BeforeEach` - runs before each test
- `@DisplayName` - provides readable test names
- `@Nested` - groups related tests

### Mockito
- Mocking framework for creating test doubles
- `@Mock` - creates mock objects
- `@ExtendWith(MockitoExtension.class)` - enables Mockito annotations
- `when().thenReturn()` - stubs method calls
- `verify()` - verifies interactions

### AssertJ
- Fluent assertion library for readable tests
- `assertThat(actual).isEqualTo(expected)`
- `assertThat(list).hasSize(3).containsExactly(1, 2, 3)`

## Test Templates

### 1. Model/POJO Testing (ItemTest.java)

**Pattern**: Test getters, setters, and business logic

```java
@DisplayName("Item Model Tests")
class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
    }

    @Test
    @DisplayName("Should set and get property correctly")
    void shouldSetAndGetProperty() {
        // Arrange
        String expected = "value";

        // Act
        item.setProperty(expected);

        // Assert
        assertThat(item.getProperty()).isEqualTo(expected);
    }
}
```

**When to use**: Testing data objects, models, DTOs, entities

**Key features**:
- Use `@Nested` classes to group related tests
- Use `@BeforeEach` to initialize fresh objects
- Follow Arrange-Act-Assert pattern
- Test edge cases (null, empty, boundary values)

### 2. API Client Testing (HackerNewsApiClientTest.java)

**Pattern**: Mock HTTP dependencies and verify interactions

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("API Client Tests")
class ApiClientTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private HackerNewsApiClient client;

    @BeforeEach
    void setUp() {
        client = new HackerNewsApiClient(mockHttpClient);
    }

    @Test
    @DisplayName("Should fetch data successfully")
    void shouldFetchData() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = "{\"id\":123}";
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(), any())).thenReturn(mockHttpResponse);

        // Act
        Item result = client.getItem(123L);

        // Assert
        assertThat(result.getId()).isEqualTo(123L);
        verify(mockHttpClient, times(1)).send(any(), any());
    }
}
```

**When to use**: Testing classes with external dependencies (HTTP, DB, File I/O)

**Key features**:
- Mock external dependencies
- Verify method calls with `verify()`
- Test error scenarios (timeouts, 404s, exceptions)
- Use `@ExtendWith(MockitoExtension.class)` for Mockito support

### 3. Common Testing Patterns

#### Arrange-Act-Assert (AAA)

```java
@Test
void testExample() {
    // Arrange - Set up test data and conditions
    Item item = new Item();
    String expectedTitle = "Test";

    // Act - Execute the code under test
    item.setTitle(expectedTitle);

    // Assert - Verify the results
    assertThat(item.getTitle()).isEqualTo(expectedTitle);
}
```

#### Testing Exceptions

```java
@Test
void shouldThrowException() {
    assertThatThrownBy(() -> client.getItem(-1L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid ID");
}
```

#### Testing Collections

```java
@Test
void shouldReturnList() {
    List<Long> ids = client.getTopStories();

    assertThat(ids)
        .isNotNull()
        .isNotEmpty()
        .hasSize(3)
        .containsExactly(1L, 2L, 3L);
}
```

#### Testing Multiple Assertions

```java
@Test
void shouldVerifyMultipleProperties() {
    assertAll("Item properties",
        () -> assertThat(item.getId()).isEqualTo(123L),
        () -> assertThat(item.getTitle()).isEqualTo("Title"),
        () -> assertThat(item.getScore()).isPositive()
    );
}
```

## Best Practices

### 1. Test Naming
- Use descriptive names: `shouldReturnItemWhenIdExists()`
- Use `@DisplayName` for even more clarity
- Name should describe what is being tested and expected outcome

### 2. Test Organization
- Group related tests with `@Nested` classes
- One assertion concept per test (not necessarily one assert statement)
- Keep tests independent - no shared state between tests

### 3. Test Coverage
- Test happy paths (normal operation)
- Test error paths (exceptions, invalid input)
- Test edge cases (null, empty, boundary values)
- Test integration points (full workflows)

### 4. Mocking Guidelines
- Mock external dependencies (HTTP, databases, files)
- Don't mock the class under test
- Don't mock simple data objects
- Verify important interactions with `verify()`

### 5. Assertions
- Use AssertJ for fluent, readable assertions
- Be specific with assertions (`isEqualTo(5)` not `isNotNull()`)
- Use `assertAll()` for multiple related assertions
- Provide failure messages when helpful

## Common Patterns by Class Type

### Model/POJO Classes
```java
✓ Test getters and setters
✓ Test toString()
✓ Test equals() and hashCode() if overridden
✓ Test validation logic
✓ Test business methods
```

### Service/Client Classes
```java
✓ Mock dependencies
✓ Test successful operations
✓ Test error handling
✓ Test retry logic
✓ Verify interactions
```

### Utility Classes
```java
✓ Test each public method
✓ Test edge cases thoroughly
✓ Test null handling
✓ No mocking needed usually
```

## Maven Surefire Configuration

The `maven-surefire-plugin` in pom.xml automatically:
- Discovers test classes matching `*Test.java`, `Test*.java`, `*TestCase.java`
- Runs tests during `mvn test` or `mvn verify`
- Generates test reports in `target/surefire-reports/`
- Fails the build if tests fail

## Extending the Templates

To create a new test class:

1. Create the test file in the corresponding package under `src/test/java`
2. Add appropriate annotations (`@DisplayName`, `@ExtendWith`)
3. Use `@Mock` for dependencies
4. Initialize objects in `@BeforeEach`
5. Write tests using Arrange-Act-Assert pattern
6. Group related tests with `@Nested`

## Troubleshooting

### Tests not running
- Ensure test class name matches pattern: `*Test.java`
- Check test methods are public and annotated with `@Test`
- Verify `maven-surefire-plugin` is in pom.xml

### Mock not working
- Add `@ExtendWith(MockitoExtension.class)` to test class
- Ensure Mockito dependencies are in pom.xml
- Initialize mocks in `@BeforeEach` or use `@Mock` annotation

### Compilation errors
- Check all imports are correct
- Ensure test dependencies have `<scope>test</scope>`
- Reload Maven project if dependencies were just added

## Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
