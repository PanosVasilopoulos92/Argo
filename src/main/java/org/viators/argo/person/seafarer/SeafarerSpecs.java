package org.viators.argo.person.seafarer;

import org.springframework.data.jpa.domain.Specification;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

public class SeafarerSpecs {

    public static Specification<SeafarerT> isActive() {
        return (root, query, cb) ->
            cb.equal(root.get("status"), ResourceStatusEnum.ACTIVE);
    }

    public static Specification<SeafarerT> hasLastName(String lastNameContaining) {
        return (root, query, cb) ->
            cb.like(
                cb.lower(root.get("lastName")),
                "%".concat(lastNameContaining.toLowerCase()).concat("%")
            );
    }

    public static Specification<SeafarerT> hasRank(SeafarerRankEnum rank) {
        return (root, query, cb) ->
            cb.equal(root.get("rank"), rank);
    }

    public static Specification<SeafarerT> hasNationality(String nationality) {
        return (root, query, cb) ->
            cb.equal(root.get("nationality"), nationality);
    }

}
