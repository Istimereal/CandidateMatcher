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
            get("/", skillController::getAllSkills, Role.ADMIN, Role.USER);
            get("/{id}", skillController::getSkillById, Role.ADMIN, Role.USER);
            post("/", skillController::createSkill, Role.ADMIN);
            put("/{id}", skillController::updateSkill, Role.ADMIN);
            delete("/{id}", skillController::deleteSkill, Role.ADMIN);
        };
    }
}
