package flycatch.feedback.model;

import flycatch.feedback.dto.EmailDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email")
@Builder
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String email;

    @ManyToOne
    @JoinColumn(name = "feedback_type_id",nullable = false)
    private FeedbackTypes feedbackType;

}
