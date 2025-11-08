package app.dtos;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillEvaluationDTO {

    private String id;
    private String slug;
    private String name;
    private String categoryKey;
    private String description;
    private int popularityScore;
    private int averageSalary;
    private String updatedAt;
}

