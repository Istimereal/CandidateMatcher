package app.dtos;

import lombok.*;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillEvaluationResponseDTO {
    private Set<SkillEvaluationDTO> data;
}
