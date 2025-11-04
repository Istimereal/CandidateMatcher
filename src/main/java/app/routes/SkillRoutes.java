package app.routes;

import app.controllers.SkillController;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;
import app.security.SecurityController.Role;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SkillRoutes {

    private final SkillController skillController;

    public SkillRoutes(SkillController skillController) {
        this.skillController = skillController;
    }


    public EndpointGroup getSkillRoutes() {
        return () -> {
         /*   // GET /Skills
            get("/", SkillController::geAllSkills, Role.ADMIN, Role.USER);

            // GET /Skills/{id}
            get("/{id}", SkillController::getSkillById, Role.ADMIN, Role.USER);

            post("/", SkillController::createSkill, Role.ADMIN);
            // PUT /Skills/{id}
            put("/{id}", SkillController::updateSkill, Role.ADMIN);

            // DELETE /Skills/{id}
            delete("/{id}", SkillController::deleteSkill, Role.ADMIN);
      */  };
    }
}
