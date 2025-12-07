package com.optibuildhub.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByPartId(Long partId);

    Optional<Rating> findByPartIdAndUserId(Long partId, Long userId);

    Long countByPartId(Long partId);

    @Query("select avg(r.score) from Rating r where r.part.id = :partId")
    Double averageScore(Long partId);
}