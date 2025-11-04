package app.routes;

import app.controllers.CandidateController;
import app.controllers.SkillController;
import app.security.SecurityController;
import io.javalin.apibuilder.EndpointGroup;
import app.security.SecurityController.Role;

public class CandidateRoutes {


       private final CandidateController candidateController;
       private final SkillController skillController; // Kun til den ene metode

    public CandidateRoutes(CandidateController candidateController, SkillController skillController) {
        this.candidateController = candidateController;
        this.skillController = skillController;
    }

    public EndpointGroup getCandidateRoutes() {
        return () -> {
        /*    get("/Skills/totalprice", CandidateController::totalPriceCandidatesBySkill, SecurityController.Role.ADMIN);
            get("/{id}/packing/weight", CandidateController::getPackingWeight, Role.ADMIN, Role.USER);
            put("/{CandidateId}/Skills/{SkillId}", CandidateController::linkSkillToCandidate, Role.ADMIN);
            get("/", CandidateController::getCandidates, Role.ADMIN, Role.USER);  // ?category=lake gives all Candidates by a category
            post("/", CandidateController::createCandidate, Role.ADMIN);
            put("/{id}", CandidateController::updateCandidate);
            delete("/{id}", CandidateController::deleteCandidate, Role.ADMIN);

            get("/{id}", CandidateController::getCandidateById, Role.ADMIN, Role.USER);


            post("/Skills", SkillController::createSkill, Role.ADMIN);
       */ };
    }

}
