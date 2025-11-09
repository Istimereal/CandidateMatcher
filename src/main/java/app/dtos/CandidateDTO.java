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
    private String phoneNumber;
    private String educationBackground;
    private Set<SkillDTO> skillEvaluations;
}
