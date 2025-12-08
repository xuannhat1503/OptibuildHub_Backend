package com.optibuildhub.forum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_images")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PostImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(columnDefinition = "LONGTEXT")
    private String url;
}