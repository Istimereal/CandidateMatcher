package app.dtos;

import app.entities.Candidate;
import app.entities.Skill;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDTO {
    private int id;
    private String name;
    private String phonenumber;

    private Set<SkillDTO> skillDTOs;

    SkillEvaluationResponseDTO evaluations;

    public Set<SkillDTO> getSkillDTOs(){

        return this.skillDTOs;
    }


}
