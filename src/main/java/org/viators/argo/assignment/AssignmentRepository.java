package org.viators.argo.assignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.viators.argo.assignment.dto.projection.ActiveAssignmentInfo;
import org.viators.argo.assignment.dto.response.AssignmentsHistOfVesselResponse;
import org.viators.argo.assignment.dto.response.AssignmentsHistOfSeafarerResponse;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentT, Long> {

    @EntityGraph(attributePaths = {"seafarer", "vessel"})
    Optional<AssignmentT> findByPublicId(String publicId);

    Page<AssignmentT> findByVessel_PublicIdAndAssignmentStateAndActualSignedOffDateIsNull(
        String vesselPublicId, AssignmentStateEnum status, Pageable pageable);

    Set<AssignmentT> findByVessel_PublicIdAndAssignmentStateAndActualSignedOffDateIsNull(
        String vesselPublicId, AssignmentStateEnum status);

    boolean existsBySeafarer_PublicIdAndAssignmentStateAndActualSignedOffDateIsNull(
        String vesselPublicId, AssignmentStateEnum status);

    Optional<AssignmentT> findBySeafarer_PublicIdAndActualSignedOffDateIsNull(String seafarerPublicId);

    @Query("""
           select new org.viators.argo.assignment.dto.projection.ActiveAssignmentInfo(
               v.vesselName,
               a.assignmentRank
           )
           from AssignmentT a
           join a.vessel v
           where a.seafarer.publicId = :seafarerPublicId
             and a.assignmentState = org.viators.argo.assignment.AssignmentStateEnum.ACTIVE
             and a.actualSignedOffDate is null
           """)
    Optional<ActiveAssignmentInfo> findActiveAssignmentInfoForSeafarer(
        @Param("seafarerPublicId") String seafarerPublicId
    );

    @Query("""
        SELECT NEW org.viators.argo.assignment.dto.response.AssignmentsHistOfSeafarerResponse(
            v.publicId,
            v.vesselName,
            a.assignmentRank,
            a.signOnDate,
            a.actualSignedOffDate,
            a.signOnPort,
            a.signOffPort,
            a.assignmentState
        )
        from AssignmentT a
        join a.seafarer s
        join a.vessel v
        where a.seafarer.publicId = :seafarerPublicId
        and a.assignmentState != org.viators.argo.assignment.AssignmentStateEnum.CANCELLED
        order by a.signOnDate desc, a.assignmentState
        """)
    Page<AssignmentsHistOfSeafarerResponse> findAssignmentHistForSeaman(
        @Param("seafarerPublicId") String seafarerPublicId,
        Pageable pageable
    );

    @Query("""
           SELECT NEW org.viators.argo.assignment.dto.response.AssignmentsHistOfVesselResponse(
                s.publicId,
                concat(s.lastName, ' ', s.firstName),
                a.assignmentRank,
                a.signOnDate,
                a.actualSignedOffDate,
                a.signOnPort,
                a.signOffPort,
                a.assignmentState
           )
           from AssignmentT a
           join a.seafarer s
           where a.vessel.publicId = :vesselPublicId
           and a.assignmentState != org.viators.argo.assignment.AssignmentStateEnum.CANCELLED
           order by a.signOnDate desc, a.assignmentState
           """)
    Page<AssignmentsHistOfVesselResponse> findAssignmentsHistForVessel(
        @Param("vesselPublicId") String vesselPublicId,
        Pageable pageable
    );

    @Query("""
        select count(a) from AssignmentT a
        where a.vessel.publicId = :vesselPublicId
        and a.assignmentState = org.viators.argo.assignment.AssignmentStateEnum.ACTIVE
        """)
    long getActiveAssignmentsForVesselCount(@Param("vesselPublicId") String vesselPublicId);
}
