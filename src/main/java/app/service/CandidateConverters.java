package app.service;


import app.dtos.CandidateDTO;
import app.entities.Candidate;

import java.util.List;

public class CandidateConverters {

    public static CandidateDTO convertToCandidateDTO(Candidate candidate){

        CandidateDTO.CandidateDTOBuilder builder = CandidateDTO.builder()
                .name(candidate.getName())
                .phonenumber(candidate.getPhoneNumber());

        if(candidate.getId() > 0){
            builder.id(candidate.getId());
        }
        return builder.build();
    }

    public static Candidate convertToCandidate(CandidateDTO candidateDTO) {
        Candidate.CandidateBuilder builder = Candidate.builder();

        if (candidateDTO.getName() != null) {
            builder.name(candidateDTO.getName());
        }

        if (candidateDTO.getPhonenumber() != null) {
            builder.phoneNumber(candidateDTO.getPhonenumber());
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
