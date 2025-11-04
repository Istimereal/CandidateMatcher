package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;
import app.security.SecurityController.Role;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class G {

 /*   private final GuideController guideController;

    public GuideRoutes(GuideController guideController) {
        this.guideController = guideController;
    }


    public EndpointGroup getGuideRoutes() {
        return () -> {
            // GET /guides
            get("/", guideController::geAllGuides, Role.ADMIN, Role.USER);

            // GET /guides/{id}
            get("/{id}", guideController::getGuideById, Role.ADMIN, Role.USER);

            post("/", guideController::createGuide, Role.ADMIN);
            // PUT /guides/{id}
            put("/{id}", guideController::updateGuide, Role.ADMIN);

            // DELETE /guides/{id}
            delete("/{id}", guideController::deleteGuide, Role.ADMIN);
        };
    }  */
}
