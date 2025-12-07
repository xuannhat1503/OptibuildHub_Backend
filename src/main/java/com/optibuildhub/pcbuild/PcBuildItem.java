package com.optibuildhub.pcbuild;

import com.optibuildhub.part.Part;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pc_build_items")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PcBuildItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "build_id", nullable = false)
    private PcBuild build;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    private Integer quantity;
}