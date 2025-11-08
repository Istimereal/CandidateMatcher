package app.controllers;

import app.daos.SkillDAO;
import app.dtos.SkillDTO;
import app.entities.Skill;
import app.exceptions.ApiException;
import app.service.SkillConverters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static app.utils.ResponseUtil.disableCache;

public class SkillController {

    LocalDateTime timeStamp = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedTime = timeStamp.format(formatter);

    private static final Logger logger = LoggerFactory.getLogger("production");
    private static final Logger debugLogProd = LoggerFactory.getLogger("debug");
    private final SkillDAO skillDAO;

    public SkillController(SkillDAO skillDAO) {
        this.skillDAO = skillDAO;
    }

    public void createSkill(Context ctx) {
        try {
            SkillDTO newSkill = ctx.bodyAsClass(SkillDTO.class);
            if(newSkill == null){
                throw new ApiException(400, "Invalid post, see documentation for correct form");
            }

            SkillDTO createdSkill = SkillConverters.convertToSkillDTO(skillDAO.createSkill(SkillConverters.convertToSkill(newSkill)));
            ctx.status(HttpStatus.CREATED).json(createdSkill);
        }
        catch(BadRequestResponse br) {
            ctx.status(HttpStatus.BAD_REQUEST).
                    json(Map.of("status", HttpStatus.BAD_REQUEST.getCode(),
                            "msg", "Invalid post, see documentation for correct form"));
        }
        catch (ApiException ae){
            int code = ae.getStatusCode();
            ctx.status(code).json(Map.of("status", code,
                    "msg","Database problems, try agian later"));
            debugLogProd.debug(formattedTime, "Error with database trying to create Skill", ae);
        }
        catch(Exception e) {
            if (
                    e.getCause() instanceof com.fasterxml.jackson.core.JacksonException) {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of(
                        "status", HttpStatus.BAD_REQUEST.getCode(),
                        "msg", "Invalid post, see documentation for correct form"
                ));
            } else {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.getCode(),
                        "msg", "There was an unexpected problem with the server"
                ));
                debugLogProd.error(formattedTime, "Unexpected server problem while creating a Skill", e);
            }}
    }

    public void getAllSkills(Context ctx){
        try {
            disableCache(ctx);
            Set<SkillDTO> skillDTOs = SkillConverters.convertToSkillDTOList(skillDAO.getAllSkills().stream().collect(Collectors.toSet()));

            if(skillDTOs.isEmpty()) {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("status",HttpStatus.NOT_FOUND.getCode(),
                        "msg", "No Skills in database"));
                logger.warn("No Skills in database");
            }
            else {
                ctx.status(200).json(skillDTOs);
            }
        }
        catch (ApiException ae){
            int code = ae.getStatusCode();
            ctx.status(code).json(Map.of("status", code,
                    "msg","Database problems, try agian later"));
            debugLogProd.debug(formattedTime, "Error with database trying to get all Skills", ae);

        }
        catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("status",
                    HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "msg",
                    "There was an unexpected error with the server, try again later"));
            debugLogProd.debug(formattedTime, "unexpected error with the server from createSkill ", e);
        }
    }

    public  void getSkillById(Context ctx){
        int id = 0;
        try {
            disableCache(ctx);
            id = Integer.parseInt(ctx.pathParam("id"));
            if (id > 0) {
                SkillDTO skillDTO = SkillConverters.convertToSkillDTO(skillDAO.getSkillById(id));
                ctx.status(200).json(skillDTO);
            }
            else {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("status",HttpStatus.BAD_REQUEST.getCode(),
                        "msg", "You need to type at id above 0"));
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
            debugLogProd.debug(formattedTime, "Error with database trying to find Skill by Id: " + id + " ", ae);
        }
        catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("status",
                    HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "msg",
                    "There was an unexpected error with the server, try again later"));
            debugLogProd.debug(formattedTime, "unexpected error with the server trying to find Skill by ID: ", e);
        }
    }

    public void updateSkill(Context ctx){

        int id = 0;
        try {
            disableCache(ctx);
            id = Integer.parseInt(ctx.pathParam("id"));

            Skill existing = skillDAO.getSkillById(id);
            if (existing == null) {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                        "status", HttpStatus.NOT_FOUND.getCode(),
                        "msg", "Skill not found"
                ));
                return;
            }

            SkillDTO skillDTO = ctx.bodyAsClass(SkillDTO.class);
            if (skillDTO.getName() != null && skillDTO.getName().isEmpty()) {
                throw new BadRequestResponse("Name cannot be empty");
            }

            Skill forUpdate = SkillConverters.convertToSkill(skillDTO);
            Skill updatedEntity = skillDAO.updateSkill(id, forUpdate);
            SkillDTO updated = SkillConverters.convertToSkillDTO(updatedEntity);

            ctx.status(HttpStatus.OK).json(updated);
        }
        catch(BadRequestResponse bre) {
            String message;

            if (bre.getMessage() == null || bre.getMessage().isBlank()) {
                // A: JSON-formatfejl (bodyAsClass fejlede)
                message = "Skill with id: " + ctx.pathParam("id") +
                        " was not in valid JSON format. See API documentation for correct structure.";
            } else { message = bre.getMessage(); }
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("status", HttpStatus.BAD_REQUEST.getCode(), "msg", message));
        }
        catch (ApiException ae){
            int code = ae.getStatusCode();
            ctx.status(code).json(Map.of("status", code,
                    "msg","Database problems, try agian later"));
            debugLogProd.debug(formattedTime, "Error with database trying to trying to update Skill: " + id + " ", ae);
        }
        catch (Exception e) {
            ctx.json(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "msg", "Unexpected error updating Skill" + ctx.pathParam("id")));
            debugLogProd.debug(formattedTime + "; Unexpected error trying to update Skill:" + id + "OperationState: ", e);
        }
    }

    public void deleteSkill(Context ctx) {
        int id = 0;
        try {
            disableCache(ctx);
            id = Integer.parseInt(ctx.pathParam("id"));

            if (id > 0) {
                skillDAO.deleteSkill(id);
                ctx.json(Map.of("status", HttpStatus.OK.getCode(), "msg", "Skill deleted"));
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of(
                        "status", HttpStatus.BAD_REQUEST.getCode(),
                        "msg", "Id must be greater than 0"
                ));
            }
        }
        catch (NumberFormatException ne) {
            ctx.json(Map.of(
                    "status", HttpStatus.BAD_REQUEST.getCode(),
                    "msg", "Invalid id format: " + ctx.pathParam("id")
            ));
        }
        catch (ApiException ae) {
            int code = ae.getStatusCode();
            ctx.status(code).json(Map.of("status", code, "msg", "Database problems, try again later"));
            debugLogProd.debug(formattedTime, "DB error deleting skill: " + id, ae);
        }
        catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.getCode(),
                    "msg", "Unexpected server error"
            ));
            debugLogProd.debug(formattedTime, "Unexpected error deleting skill " + id, e);
        }
    }
}

