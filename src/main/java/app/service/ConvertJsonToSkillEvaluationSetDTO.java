package app.service;

import app.dtos.SkillEvaluationResponseDTO;
import app.exceptions.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ConvertJsonToSkillEvaluationSetDTO {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SkillEvaluationResponseDTO skillApiToSkillEvaluationResponseDTO(String json) {
        try {
            objectMapper.registerModule(new JavaTimeModule());

            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            return objectMapper.readValue(json, SkillEvaluationResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new ApiException(500, e.getMessage());
        }
    }
}
