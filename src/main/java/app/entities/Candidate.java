package app.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsExclude;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Setter
@Getter
@EqualsAndHashCode
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "candidate")
public class Candidate {

    @Id
    @Column(name = "candidate_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name =  "phonenumber", nullable = false)
    String phoneNumber;

    @Column(name = "education_background", nullable = false)
    String educationBackground;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "candidate_skill", joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
           @EqualsAndHashCode.Exclude
    @Builder.Default
    Set<Skill> skills = new HashSet();


    public void addSkill(Skill skill) {
        if (skills.add(skill)) {
            skill.getCandidates().add(this);
        }
    }

    public void removeSkill(Skill skill) {
        if (skills.remove(skill)) {
            skill.getCandidates().remove(this);
        }

    }
}
