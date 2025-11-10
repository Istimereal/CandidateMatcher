package app.controllers;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.service.Populator;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class CandidateControllerTest {

    private static EntityManagerFactory emfTest;
    private static Javalin app;
    private static String adminToken;

    @BeforeAll
    static void setupServer() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        app = ApplicationConfig.startServer(7085, emfTest);
        RestAssured.baseURI = "http://localhost:7085/api/v1";
    }

    @BeforeEach
    void resetDB() {
        try (EntityManager em = emfTest.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE candidate RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE skill RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE role_user RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE role RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();

            Populator pop = new Populator(emfTest);
            pop.createUsersAndRolesTest();
            pop.populateTestDB();

            String loginJson = """
                    {
                      "username": "admin",
                      "password": "admin123"
                    }
                    """;

            adminToken =
                    given()
                            .contentType("application/json")
                            .body(loginJson)
                            .when()
                            .post("/auth/login")
                            .then()
                            .statusCode(200)
                            .extract()
                            .path("token");

        }
    }

    @AfterAll
    static void stopServer() {
        if (app != null) app.stop();
    }



    @Test
    @DisplayName("Get candidates success")
    void getCandidates() {
        //Arrange  done in beforeEach/populateTestDB

        //Act and Assert
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/candidates/")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].id", equalTo(1))
                .body("[0].name", equalTo("Cody"))
                .body("[0].phoneNumber", equalTo("12345678"))
                .body("[0].educationBackground", equalTo("Software engineer"))
                .body("[1].id", equalTo(2))
                .body("[1].name", equalTo("Bobby"))
                .body("[1].phoneNumber", equalTo("34567812"))
                .body("[1].educationBackground", equalTo("Datalog"));

    }


    @Test
    @DisplayName("No Candidates in database, NOT_FOUND response ")
    public void emptyCandidateTable() {
        //Arrange
        try (EntityManager em = emfTest.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE candidate RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();

            //Act+Assert
            given()
                    .header("Authorization", "Bearer " + adminToken)
                    .when()
                    .get("/candidates/")
                    .then()
                    .statusCode(404)
                    .body("status", equalTo(404))
                    .body("msg", equalTo("No Candidates in database"));

        }
        catch (Exception e){
            System.out.println("could not delete Candidates in DB");
        }
    }

    @Test
            @DisplayName("Get Candidate By id, Succes, external evaluation included")
    void getCandidateById() {
            //Act in beforeAll
            //Arrange+Assert
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")   // .contentType("application/json")
                .when()
                .get("/candidates/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Cody"))
                .body("phoneNumber", equalTo("12345678"))
                .body("educationBackground", equalTo("Software engineer"))
                .body("skillEvaluations.id", hasItem(1))     // Forkert version: equalTo("<[1]>")
                .body("skillEvaluations.name", hasItem("Java"))
                .body("skillEvaluations.category", hasItem("PROG_LANG"))
                .body("skillEvaluations.description", hasItem("General-purpose programming languages"))
                .body("skillEvaluations.popularityScore", hasItem(93))
                .body("skillEvaluations.averageSalary", hasItem(120000));

        }

/*
    @Test
    void createCandidate() {
    }

    @Test
    void updateCandidate() {
    }

    @Test
    void deleteCandidate() {
    }

    @Test
    void totalPriceCandidatesByGuide() {
    }

    @Test
    void getPackingWeight() {
    }

    @Test
    void linkGuideToCandidate() {
    }  */
}