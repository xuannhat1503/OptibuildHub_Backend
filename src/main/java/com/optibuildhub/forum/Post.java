package com.optibuildhub.forum;

import com.optibuildhub.user.User;
import com.optibuildhub.pcbuild.PcBuild;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    @Column(columnDefinition = "text") private String content;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "build_id")
    private PcBuild build; // optional reference to shared build
    private Instant createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images;
}