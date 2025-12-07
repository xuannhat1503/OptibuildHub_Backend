package com.optibuildhub.part;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "parts")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Part {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String name;
    @Column(nullable = false) private String category; // CPU, GPU, RAM, PSU, MAIN, CASE, STORAGE...
    private String brand;
    @Column(columnDefinition = "json") private String specJson; // socket, form factor...
    private Integer wattage;
    @Column(precision = 15, scale = 2, nullable = false) private BigDecimal price;
    private String imageUrl;
    private Instant createdAt;
}