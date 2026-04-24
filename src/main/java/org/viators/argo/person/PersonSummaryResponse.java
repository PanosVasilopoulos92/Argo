package org.viators.argo.person;

public record PersonSummaryResponse(
    String personPublicId,
    String personFullName,
    String typeOfPerson
) {

    public static PersonSummaryResponse from(PersonT entity) {
        return new PersonSummaryResponse(
            entity.getPublicId(),
            entity.getLastName().concat(" ").concat(entity.getFirstName()),
            entity.getPersonType()
        );
    }
}
