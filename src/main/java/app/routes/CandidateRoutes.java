package app.routes;

import app.controllers.CandidateController;
import app.controllers.SkillController;
import app.security.SecurityController;
import io.javalin.apibuilder.EndpointGroup;
import app.security.SecurityController.Role;
import static io.javalin.apibuilder.ApiBuilder.*;

public class CandidateRoutes {


       private final CandidateController candidateController;
       private final SkillController skillController; // Kun til den ene metode

    public CandidateRoutes(CandidateController candidateController, SkillController skillController) {
        this.candidateController = candidateController;
        this.skillController = skillController;
    }

    public EndpointGroup getCandidateRoutes() {
        return () -> {
                get("/skills/totalprice", candidateController::getCandidates, Role.ADMIN);
                put("/{candidateId}/skills/{skillId}", candidateController::linkSkillToCandidate, Role.ADMIN);
                get("/", candidateController::getCandidates, Role.ADMIN, Role.USER);
                post("/", candidateController::createCandidate, Role.ADMIN);
                put("/{id}", candidateController::updateCandidate, Role.ADMIN);
                delete("/{id}", candidateController::deleteCandidate, Role.ADMIN);
                get("/{id}", candidateController::getCandidateById, Role.ADMIN, Role.USER);
                post("/skills", skillController::createSkill, Role.ADMIN);
        };
    }

}
