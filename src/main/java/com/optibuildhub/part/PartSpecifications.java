package com.optibuildhub.part;

import com.optibuildhub.part.dto.PartFilter;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PartSpecifications {

    public static Specification<Part> filter(PartFilter f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (f.getCategory() != null && !f.getCategory().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), f.getCategory().toLowerCase()));
            }
            if (f.getBrand() != null && !f.getBrand().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), f.getBrand().toLowerCase()));
            }
            if (f.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), f.getMinPrice()));
            }
            if (f.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), f.getMaxPrice()));
            }
            if (f.getKeyword() != null && !f.getKeyword().isBlank()) {
                String kw = "%" + f.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), kw),
                        cb.like(cb.lower(root.get("brand")), kw)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}