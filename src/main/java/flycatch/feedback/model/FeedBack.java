package flycatch.feedback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String feedbackText;

    @ManyToOne
    @JoinColumn(name = "feedback_types_id",nullable = false)
    private FeedbackTypes feedbackType;

    @ManyToOne
    @JoinColumn(name = "aircraft_id",nullable = false)
    private Aircraft aircraft;
}
