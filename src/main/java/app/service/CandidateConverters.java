package app.service;


import app.dtos.CandidateDTO;
import app.dtos.SkillDTO;
import app.entities.Candidate;
import app.entities.Skill;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static app.service.SkillConverters.convertToSkillDTO;
import static app.service.SkillConverters.convertToSkillDTOList;
import static org.hibernate.internal.util.collections.ArrayHelper.forEach;

public class CandidateConverters {

    public static CandidateDTO convertToCandidateDTO(Candidate candidate){

        CandidateDTO.CandidateDTOBuilder builder = CandidateDTO.builder()
                .name(candidate.getName())
                .phoneNumber(candidate.getPhoneNumber())
                .educationBackground(candidate.getEducationBackground());

        if(candidate.getId() > 0){
            builder.id(candidate.getId());
        }
        if(candidate.getSkills() != null){
            Set<SkillDTO> skillDTOs = convertToSkillDTOList(candidate.getSkills());

            builder.skillEvaluations(skillDTOs);
        }
        return builder.build();
    }

    public static Candidate convertToCandidate(CandidateDTO candidateDTO) {
        Candidate.CandidateBuilder builder = Candidate.builder();

        if (candidateDTO.getName() != null) {
            builder.name(candidateDTO.getName());
        }

        if (candidateDTO.getPhoneNumber() != null) {
            builder.phoneNumber(candidateDTO.getPhoneNumber());
        }

        if(candidateDTO.getEducationBackground() != null){
            builder.educationBackground(candidateDTO.getEducationBackground());
        }

        if (candidateDTO.getId() > 0) {
            builder.id(candidateDTO.getId());
        }

        return builder.build();
    }
    public static List<CandidateDTO> convertToCandidateDTO(List<Candidate> candidates){
        return candidates.stream()
                .map(CandidateConverters::convertToCandidateDTO)
                .toList();
    }
}
