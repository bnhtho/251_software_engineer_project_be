package HCMUT.TutorSytem.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "related_session_id")
    private Session relatedSession;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "message")
    private String message;

    @ColumnDefault("0")
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "sent_at")
    private Instant sentAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Instant createdDate;

}