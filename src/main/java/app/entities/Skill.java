package app.entities;

import app.enums.Category;
import app.security.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
@Table(name = "skill")
public class Skill {


    @Id
    @Column(name = "skill_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;

    public Skill(int id, String name, Category category, String description) {
        this.id = id;
        this.name = name;    //toUpperCase();
        this.category = category;
        this.description = description;
    }

    @ManyToMany(mappedBy = "skills", fetch = FetchType.EAGER)
    private Set<Candidate> candidates = new HashSet<>();

    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
    }

    public void removeCandidate(Candidate candidate) {
        candidates.remove(candidate);
    }
}