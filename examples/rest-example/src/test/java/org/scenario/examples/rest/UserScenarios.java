package org.scenario.examples.rest;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.scenario.annotations.Resource;
import org.scenario.annotations.ScenarioDefinition;
import org.scenario.annotations.Step;
import org.scenario.definitions.Scenario;
import org.scenario.definitions.ScenarioContext;
import org.scenario.definitions.ScenarioFlow;

import java.util.Optional;

import static io.restassured.RestAssured.given;

public class UserScenarios {
    @ScenarioDefinition
    public Scenario postScenario() {
        return new Scenario.Builder()
                .name("User's posts")
                .description("The flow of user posting something")
                .flow(new ScenarioFlow.Builder()
                        .instance(this)
                        .step("createUser")
                        .step("publishPost")
                        .step("comment")
                        .build()
                ).build();
    }

    @ScenarioDefinition
    public Scenario feedScenario() {
        return new Scenario.Builder()
                .name("User's feed")
                .description("Getting the feed of the home page for a user")
                .flow(new ScenarioFlow.Builder()
                        .instance(this)
                        .step("createUser")
                        .step("getFeed")
                        .build()
                ).build();
    }

    @Step(description = "Create a user")
    public void createUser(final ScenarioContext context, final @Resource("create-user-request.json") String body) {
        final String baseUrl = Optional.ofNullable(context.global().get("baseUrl"))
                .map(Object::toString)
                .orElseThrow();

        final String url = String.format("%s/%s", baseUrl, "users");

        final ValidatableResponse response = given().body(body)
                .contentType(ContentType.JSON)
                .post(url)
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON);

        final String id = response.extract()
                .response()
                .path("id");

        context.put("userId", id);
    }

    @Step(description = "Publish a post")
    public void publishPost(final ScenarioContext context, final @Resource("publish-post-request.json") String body) {
        final String baseUrl = Optional.ofNullable(context.global().get("baseUrl"))
                .map(Object::toString)
                .orElseThrow();

        final String url = String.format("%s/%s", baseUrl, "posts");

        final ValidatableResponse response = given().body(body)
                .contentType(ContentType.JSON)
                .post(url)
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON);

        final String id = response.extract()
                .response()
                .path("id");

        context.put("postId", id);
    }

    @Step(description = "Comment on the post")
    public void comment(final ScenarioContext context, final @Resource("publish-comment-request.json") String body) {
        final String baseUrl = Optional.ofNullable(context.global().get("baseUrl"))
                .map(Object::toString)
                .orElseThrow();

        final String postId = (String) context.get("postId");

        final String url = String.format("%s/%s/%s/%s", baseUrl, "posts", postId, "comments");

        given().body(body)
                .contentType(ContentType.JSON)
                .post(url)
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON);
    }

    @Step(description = "Get user feed for the home page")
    public void getFeed(final ScenarioContext context) {
        final String baseUrl = Optional.ofNullable(context.global().get("baseUrl"))
                .map(Object::toString)
                .orElseThrow();

        final String userId = (String) context.get("userId");

        final String url = String.format("%s/%s/%s/%s", baseUrl, "users", userId, "feed");

        given().get(url)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
