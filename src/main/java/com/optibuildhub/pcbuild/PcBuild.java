package com.optibuildhub.pcbuild;

import com.optibuildhub.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "pc_builds")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PcBuild {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    @Column(precision = 15, scale = 2) private BigDecimal totalPrice;
    private Integer wattageTotal;
    private Boolean isShared;
    private Instant createdAt;

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PcBuildItem> items;
}