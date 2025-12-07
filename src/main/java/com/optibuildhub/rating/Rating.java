package com.optibuildhub.rating;

import com.optibuildhub.part.Part;
import com.optibuildhub.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(name = "uq_rating_user_part", columnNames = {"part_id", "user_id"})
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Rating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer score; // 1-5
    @Column(columnDefinition = "text") private String content;
    private Instant createdAt;
}