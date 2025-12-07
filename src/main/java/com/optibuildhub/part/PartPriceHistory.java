package com.optibuildhub.part;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "part_price_history")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PartPriceHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(precision = 15, scale = 2, nullable = false) private BigDecimal price;
    private String source;
    private Instant crawledAt;
}