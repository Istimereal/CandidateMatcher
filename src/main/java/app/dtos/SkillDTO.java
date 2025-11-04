package app.dtos;

import app.entities.Skill;
import app.enums.Category;
import lombok.*;

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

    public SkillDTO(Skill skill) {
        this.name = skill.getName();
        this.category = skill.getCategory();
    }
}
