package flycatch.feedback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "feedback_types")
public class FeedbackTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "feedbackType", cascade = CascadeType.ALL)
    private List<Email> emails = new ArrayList<>();

    public void addEmail(Email email) {
        emails.add(email);
        email.setFeedbackType(this);
    }
}
