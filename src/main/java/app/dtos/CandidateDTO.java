package app.dtos;

import app.entities.Candidate;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDTO {
    private int id;
    private String name;
    private String phonenumber;

    public CandidateDTO(Candidate candidate) {
        this.name = candidate.getName();
        this.phonenumber = candidate.getPhoneNumber();

    }
}
