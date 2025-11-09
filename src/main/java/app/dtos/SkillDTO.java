package app.dtos;

import app.entities.Skill;
import app.enums.Category;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillDTO {
    private int id;
    private String name;
    private Category category;
    private String description;

    private int popularityScore;
    private int averageSalary;

}
