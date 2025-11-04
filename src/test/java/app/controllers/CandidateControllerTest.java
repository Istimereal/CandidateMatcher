package app.controllers;

import org.junit.jupiter.api.Test;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.service.Populator;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.jupiter.api.Assertions.*;

class CandidateControllerTest {

    private static EntityManagerFactory emfTest;
    private static Javalin app;
    private static String adminToken;

    @BeforeAll
    static void setupServer() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        app = ApplicationConfig.startServer(7080, emfTest);
        RestAssured.baseURI = "http://localhost:7082/api/v1";
    }

    @BeforeEach
    void resetDB() {
        try (EntityManager em = emfTest.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE trip RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE guide RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE role_user RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE role RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();

            Populator pop = new Populator(emfTest);
            pop.createUsersAndRolesTest();
            pop.poppulateDBTest();

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

            System.out.println();
        }
    }

    @AfterAll
    static void stopServer() {
        if (app != null) app.stop();
    }



    @Test
    void getCandidates() {
    }

    @Test
    void getCandidateById() {
    }

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
    }
}