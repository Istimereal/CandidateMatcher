package app.service;


import app.dtos.SkillDTO;
import app.entities.Skill;

import java.util.List;

public class SkillConverters {

    public static SkillDTO convertToSkillDTO(Skill skill) {
        SkillDTO.SkillDTOBuilder builder = SkillDTO.builder()
                .name(skill.getName())
                .category(skill.getCategory())
                .description(skill.getDescription());
        if (skill.getId() > 0) {
            builder.id(skill.getId());
        }
        return builder.build();
    }

    public static Skill convertToSkill(SkillDTO skillDTO) {
        Skill.SkillBuilder builder = Skill.builder();
        if (skillDTO.getId() > 0) {
            builder.id(skillDTO.getId());
        }
        if (skillDTO.getName() != null) {
            builder.name(skillDTO.getName());
        }
        if (skillDTO.getCategory() != null) {
            builder.category(skillDTO.getCategory());
        }
        if (skillDTO.getDescription() != null) {
            builder.description(skillDTO.getDescription());
        }
        return builder.build();
    }

    public static List<SkillDTO> convertToSkillDTOList(List<Skill> Skills) {

        return Skills.stream()
                .map(SkillConverters::convertToSkillDTO)
                .toList();
    }  
}
