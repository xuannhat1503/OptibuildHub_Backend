package com.optibuildhub.pcbuild;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PcBuildRepository extends JpaRepository<PcBuild, Long> {
    List<PcBuild> findByUserId(Long userId);
}