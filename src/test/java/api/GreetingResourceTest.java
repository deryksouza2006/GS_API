package api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHealthEndpoint() {
        given()
                .when().get("/api/health")
                .then()
                .statusCode(200);
    }

    @Test
    public void testTasksEndpoint() {
        given()
                .when().get("/api/tasks")
                .then()
                .statusCode(200);
    }
}