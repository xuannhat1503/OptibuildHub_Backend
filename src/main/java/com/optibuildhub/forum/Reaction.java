package com.optibuildhub.forum;

import com.optibuildhub.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "reactions", uniqueConstraints = {
        @UniqueConstraint(name = "uq_reaction_user_post", columnNames = {"post_id", "user_id"})
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Reaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING) private ReactionType type; // LIKE / DISLIKE
    private Instant createdAt;
}