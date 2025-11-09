package app.config;

import app.controllers.CandidateController;
import app.controllers.SkillController;
import app.daos.CandidateDAO;
import app.daos.SkillDAO;
import app.exceptions.ApiException;

import app.routes.CandidateRoutes;
import app.routes.Routes;
import app.routes.SecurityRoutes;
import app.routes.SkillRoutes;
import app.security.SecurityController;
import app.security.SecurityDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static Javalin startServer(int port, EntityManagerFactory emf) {

        // Init security + routes

        SkillDAO skillDAO = SkillDAO.getInstance(emf);
        CandidateDAO candidateDAO = CandidateDAO.getInstance(emf);

        SkillController skillController = new SkillController(skillDAO);
        CandidateController candidateController = new CandidateController(candidateDAO, skillDAO);

        SecurityDAO securityDAO = new SecurityDAO(emf);
        SecurityController securityController = new SecurityController(securityDAO);
        SecurityRoutes securityRoutes = new SecurityRoutes(securityController);

        SkillRoutes skillRoutes = new SkillRoutes(skillController);
        CandidateRoutes candidateRoutes = new CandidateRoutes(candidateController, skillController);

        Routes routes = new Routes(skillRoutes, candidateRoutes);

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.bundledPlugins.enableRouteOverview("/routes");
            config.router.contextPath = "/api/v1";

            // 👇 BEGGE linjer her skal være der
            config.router.apiBuilder(routes.getEndpoints());
            config.router.apiBuilder(securityRoutes.getSecurityRoutes());
        });


        // Åbne endpoints (ingen auth)
        app.get("/", ctx -> ctx.json(Map.of("status", "API is running")));


        app.beforeMatched(securityController.authenticate());
        app.beforeMatched(securityController.authorize());

        // CORS + exception handling
        setCORS(app);
        setGeneralExceptionHandling(app);

        // Dev logging
        if (System.getenv("DEPLOYED") == null) {
            beforeFilter(app);
        }

        app.start(port);
        return app;
    }

    private static void setCORS(Javalin app) {
        app.before(ApplicationConfig::setCorsHeaders);
        app.options("/*", ApplicationConfig::setCorsHeaders);
    }

    private static void setCorsHeaders(io.javalin.http.Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }
    //ændringer
    private static void setGeneralExceptionHandling(Javalin app) {
        app.exception(Exception.class, (e, ctx) -> {
            int statusCode = (e instanceof ApiException apiEx) ? apiEx.getStatusCode() : 500;
            String message = (e instanceof ApiException) ? e.getMessage() : "Internal server error";

            logger.error("An exception occurred", e);

            ObjectNode on = jsonMapper.createObjectNode()
                    .put("status", statusCode)
                    .put("msg", message);

            ctx.json(on);
            ctx.status(statusCode);
        });
    }

    private static void beforeFilter(Javalin app) {
        app.before(ctx -> {
            // Debug-request headers, valgfrit
            ctx.req().getHeaderNames().asIterator().forEachRemaining(System.out::println);
        });
    }
}
