package app.controllers;

import app.daos.CandidateDAO;
import app.daos.SkillDAO;
import app.dtos.CandidateDTO;
import app.dtos.SkillDTO;
import app.dtos.SkillEvaluationResponseDTO;
import app.entities.Candidate;
import app.enums.Category;
import app.exceptions.ApiException;
import app.service.CandidateConverters;
import app.service.ExternalEvaluationService;
import app.service.SkillConverters;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static app.service.CandidateConverters.convertToCandidateDTO;
import static app.utils.ResponseUtil.disableCache;

public class CandidateController {
    LocalDateTime timeStamp = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedTime = timeStamp.format(formatter);

    private static final Logger logger = LoggerFactory.getLogger("production");
    private static final Logger debugLogProd = LoggerFactory.getLogger("debug");
    private final CandidateDAO candidateDAO;
    private final SkillDAO skillDAO;

    public CandidateController(CandidateDAO candidateDAO, SkillDAO skillDAO) {
        this.candidateDAO = candidateDAO;
        this.skillDAO =skillDAO;
    }

    public void getCandidates(Context ctx) {
        Category category;
        List<CandidateDTO> allCandidateDTOs;
        try {
            disableCache(ctx);

            String request = ctx.queryParam("category");

            if (request != null && !request.isEmpty()) {
                try {
                    category = Category.valueOf(request.toUpperCase());
                } catch (IllegalArgumentException iae) {
                    ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("status", HttpStatus.BAD_REQUEST.getCode(),
                            "msg", "Invalid category. Valid categories are: PROG_LANG, DB, DEVOPS, FRONTEND, TESTING, DATA, FRAMEWORK"));
                    return;
                }
                 allCandidateDTOs = convertToCandidateDTO(candidateDAO.getAllCandidates());
                Set<CandidateDTO> filteredForSkills = allCandidateDTOs.stream()
                        .filter(can -> can.getSkillDTOs()
                                .stream()
                                .anyMatch(skill -> skill.equals(request)))
                        .collect(Collectors.toSet());

                Set<SkillDTO> allSkills = SkillConverters.convertToSkillDTOList(skillDAO.getAllSkills().stream().collect(Collectors.toSet()));
                Set<SkillDTO> sortedSkills = allSkills.stream()
                        .filter(Skill -> Skill.getCategory().equals(category))
                        .collect(Collectors.toSet());

                ctx.status(HttpStatus.OK).json(sortedSkills);
            } else {
                List<CandidateDTO> candidateDTOs = convertToCandidateDTO(candidateDAO.getAllCandidates());
                if (candidateDTOs.isEmpty()) {
                    ctx.status(HttpStatus.NOT_FOUND).json(Map.of("status", HttpStatus.NOT_FOUND.getCode(), "msg", "No Candidates in database"));
                    logger.warn("No Candidates in database");
                } else {
                    ctx.status(200).json(candidateDTOs);
                }
            }
        }
        catch (ApiException ae){
            int code = ae.getStatusCode();
            ctx.status(code).json(Map.of("status", code,
                    "msg","Database problems, try agian later"));
            debugLogProd.debug(formattedTime, "Error with database trying to trying to get all", ae);
        }
        catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("status",
                    HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "msg",
                    "There was an unexpected error with the server, try again later"));
            debugLogProd.debug(formattedTime, "unexpected error with the server ", e);
        }
    }

    public void getCandidateById(Context ctx) {
        int id = 0;

        try {
            disableCache(ctx);
            id = Integer.parseInt(ctx.pathParam("id"));
            if (id > 0) {
                String request = "";
                CandidateDTO candidateDTO = CandidateConverters.convertToCandidateDTO(candidateDAO.getCandidateById(id));

                String allSkillNames = candidateDTO.getSkillDTOs().stream()
                        .map(s -> s.getName())
                        .collect(Collectors.joining(","));

                SkillEvaluationResponseDTO skillSet = ExternalEvaluationService.getSkillEvaluationList(allSkillNames);
                // Tried but was far from able to make ie correct
                List<SkillDTO> enhancedDTO = candidateDTO.getSkillDTOs().stream()
                        .map(s -> {
                            skillSet.getData().stream()
                                    .filter(sk -> sk.getName().equalsIgnoreCase(s.getName()))
                                    .findFirst()
                                    .ifPresent(sk -> {
                                        s.setPopularityScore(sk.getPopularityScore());
                                        s.setAverageSalary(sk.getAverageSalary());
                                    });
                            return s;
                        })
                        .toList();

                ctx.status(200).json(enhancedDTO);
            }
            else {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("status",HttpStatus.BAD_REQUEST.getCode(),"msg", "You need to type at id above 0"));
            }
        }
        catch (NumberFormatException ne) {
            ctx.status(HttpStatus.BAD_REQUEST.getCode()).json(Map.of("status", HttpStatus.BAD_REQUEST.getCode(), "msg",
                    "Invalid id format: " + ctx.pathParam("id")));
        }
        catch (ApiException ae) {
            int code = ae.getStatusCode();
            String msg = "";
            String debugMsg = "";
            if(code == 404){ msg = "Candidate with id " + id + " not found in database";}
            else {
                msg = "Problems getting popularity externally, try again later";
                debugLogProd.error(formattedTime, debugMsg, ae);
            }
            ctx.status(code).json(
                    Map.of("status", HttpStatus.forStatus(code).getCode(),
                            "msg", msg ));
        }
        catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("status",
                    HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "msg",
                    "There was an unexpected error with the server, try again later"));
            debugLogProd.debug(formattedTime, "unexpected error with the server trying to find Candidate by ID: ", e);
        }
    }

    public void createCandidate(Context ctx) {
        try {
            CandidateDTO newCandidate = ctx.bodyAsClass(CandidateDTO.class);

            CandidateDTO createdCandidate = convertToCandidateDTO(candidateDAO.createCandidate(CandidateConverters.convertToCandidate(newCandidate)));
            ctx.status(HttpStatus.CREATED).json(createdCandidate);
        }
        catch(BadRequestResponse br) {
            ctx.status(HttpStatus.BAD_REQUEST).
                    json(Map.of("status", HttpStatus.BAD_REQUEST.getCode(),
                            "msg", "Invalid post, see documentation for correct form"));
        }
        catch (ApiException ae){
            int code = ae.getStatusCode();
            ctx.status(code).json(Map.of("status", code,
                    "msg","Database problems, try again later"));
            debugLogProd.debug(formattedTime, "Error with database trying to trying to create Candidate", ae);
        }
        catch(Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("status",HttpStatus.INTERNAL_SERVER_ERROR.getCode(),
                    "msg", "There was an unexpected problem with the server"));
            debugLogProd.error(formattedTime, "Unexpected server problem while creating a Candidate", e);
        }
    }

    public void updateCandidate(Context ctx) {
        int id = 0;
        Candidate Candidate = null;
        try {
            id = Integer.parseInt(ctx.pathParam("id"));
            if (id > 0) {
                Candidate = candidateDAO.getCandidateById(id);

                if (Candidate == null) {
                    ctx.status(HttpStatus.NOT_FOUND).json(Map.of("status", HttpStatus.NOT_FOUND.getCode(), "msg", "guide not found"));
                    return;
                }
            }
            CandidateDTO candidateUpdateDTO = ctx.bodyAsClass(CandidateDTO.class);
            if(candidateUpdateDTO.getName() != null && candidateUpdateDTO.getName().isEmpty()) {
                throw new BadRequestResponse("Name cannot be empty, exclude or put desired name");
            }
            Candidate forUpdate = CandidateConverters.convertToCandidate(candidateUpdateDTO);
            Candidate updateResult = candidateDAO.updateCandidate(id, forUpdate);
            CandidateDTO updated = convertToCandidateDTO(updateResult);
            ctx.status(HttpStatus.OK).json(updated);
        }
        catch(BadRequestResponse bre) {

            String message;

            if (bre.getMessage() == null || bre.getMessage().isBlank()) {

                message = "Candidate with id: " + ctx.pathParam("id") +
                        " was not in valid JSON format. See API documentation for correct structure.";
            } else {
                message = bre.getMessage();
            }
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("status", HttpStatus.BAD_REQUEST.getCode(), "msg", message));
        }
        catch (ApiException ae){
            int code = ae.getStatusCode();
            ctx.status(code).json(Map.of("status", code,
                    "msg","Database problems, try agian later"));
            debugLogProd.debug(formattedTime, " Database error trying to update Candidate ", ae);
        }
        catch (Exception e) {
            ctx.json(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "msg", "Unexpected error updating Candidate" + ctx.pathParam("id")));
            debugLogProd.debug(formattedTime + "; Unexpected error trying to update Candidate:" + id + "OperationState: ", e);
        }
    }

    public void deleteCandidate(Context ctx) {
        int id = 0;
        try {
            disableCache(ctx);
            id = Integer.parseInt(ctx.pathParam("id"));
            if (id > 0) {
                candidateDAO.deleteCandidate(id);
                ctx.status(HttpStatus.OK).json(Map.of("status",HttpStatus.OK.getCode(),"msg", "Candidate with id: " + id + " was deleted"));
            }
            else {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("status",HttpStatus.BAD_REQUEST.getCode(),"msg", "You need to type at id above 0"));
            }
        }
        catch (NumberFormatException ne) {
            ctx.json(Map.of("status", HttpStatus.BAD_REQUEST.getCode(), "msg",
                    "Invalid id format:" + ctx.pathParam("id")));
        }
        catch (ApiException ae){
            int code = ae.getStatusCode();
            ctx.status(code).json(Map.of("status", code,
                    "msg","Database problems, try agian later"));
            debugLogProd.debug(formattedTime, " Database error trying to delete Candidate ", ae);
        }
        catch(Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.getCode(),
                    "msg", "There was an unexpected server error with the server"));
            debugLogProd.debug(formattedTime + "; Unexpected server while trying to delete Candidate with Id: " + id, e);
        }
    }

    public void linkSkillToCandidate(Context ctx) {
        int candidateId = 0;
        int skillId = 0;
        try {
            disableCache(ctx);
            System.out.println("c1");
            candidateId = Integer.parseInt(ctx.pathParam("candidateId"));
            System.out.println("c2");
            skillId = Integer.parseInt(ctx.pathParam("skillId"));
            System.out.println("c3");
            if (candidateId <= 0) {
                System.out.println("c3A");
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("status", HttpStatus.BAD_REQUEST.getCode(),
                        "msg", "candidateId must be greater than 0"));
                return;
            }
            if (skillId <= 0) {
                System.out.println("c3B");
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of(
                        "status", HttpStatus.BAD_REQUEST.getCode(),
                        "msg", "skillId must be greater than 0"));
                return;
            }
            System.out.println("c4");
             candidateDAO.addSkillToCandidate(candidateId, skillId);
            System.out.println("c5");
            ctx.status(HttpStatus.OK).json(Map.of(
                    "status", HttpStatus.OK.getCode(),"msg", "Skill has been added to candidate" ));
        }
        catch (NumberFormatException ne) {
            ctx.json(Map.of(
                    "status", HttpStatus.BAD_REQUEST.getCode(), "msg", "Invalid id format. Must be a number"                                                         ));
        }
        catch (ApiException ae) {
            int code = ae.getStatusCode();
            ctx.status(code).json(Map.of(
                    "status", code,
                    "msg", ae.getMessage()));
        }
        catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.getCode(),
                    "msg", "Unexpected server error"));
            debugLogProd.debug(formattedTime, "Unexpected error linking skill to candidate", e);
        }
    }
}