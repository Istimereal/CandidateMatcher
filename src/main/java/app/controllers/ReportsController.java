package app.controllers;

import app.daos.CandidateDAO;
import app.dtos.CandidateDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static app.service.CandidateConverters.convertToCandidateDTO;

public class ReportsController {
    private static final Logger logger = LoggerFactory.getLogger("production");
    private static final Logger debugLogProd = LoggerFactory.getLogger("debug");
    CandidateDAO candidateDAO;

    public ReportsController(CandidateDAO candidateDAO){
        this.candidateDAO = candidateDAO;
    }

  /*  public void getTopCandidateByPopularity(Context ctx){

        try{
            List<CandidateDTO> allCandidateDTOs = convertToCandidateDTO(candidateDAO.getAllCandidates());

            if(allCandidateDTOs.isEmpty()){
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("status", HttpStatus.NOT_FOUND.getCode(), "msg", "No Candidates in database"));
                logger.warn("No Candidates in database");
            }
            else{


            }

        }
    }  */
}
