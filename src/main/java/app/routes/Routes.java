package app.routes;

//import app.controllers.SkillController;
//import app.controllers.CandidateController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final SkillRoutes SkillRoutes;
    private final CandidateRoutes CandidateRoutes;

    public Routes(SkillRoutes  SkillRoutes, CandidateRoutes CandidateRoutes) {
        this.SkillRoutes = SkillRoutes;
        this.CandidateRoutes = CandidateRoutes;
    }

    public EndpointGroup getEndpoints() {

        return () -> {
            path("/skills", SkillRoutes.getSkillRoutes());
            path("/candidates", CandidateRoutes.getCandidateRoutes());
        };
    }
}
