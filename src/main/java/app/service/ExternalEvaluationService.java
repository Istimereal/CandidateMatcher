package app.service;

import app.dtos.SkillEvaluationDTO;
import app.dtos.SkillEvaluationResponseDTO;

import java.util.Set;

public class ExternalEvaluationService {

    static ApiService apiService = new ApiService();

    public static SkillEvaluationResponseDTO getSkillEvaluationList (String skills){
        ConvertJsonToSkillEvaluationSetDTO convertJson = new ConvertJsonToSkillEvaluationSetDTO();

        String url = "https://apiprovider.cphbusinessapps.dk/api/v1/skills/stats?slugs=" + skills;

        String response = apiService.fetchFromApi(url);

        System.out.println("FETCH RESULT:\n" + response);

        SkillEvaluationResponseDTO results = convertJson.skillApiToSkillEvaluationResponseDTO(response);

        return results;
    }
/*
    public static Integer calcPackingTotalWeight(SkillEvaluationResponseDTO SkillEvaluationResponseDTO){
        Integer result;

        return result = SkillEvaluationResponseDTO.getItems().stream()
                .mapToInt(ItemForPackagingDTO::getWeightInGrams)
                .sum();
    }  */

}
