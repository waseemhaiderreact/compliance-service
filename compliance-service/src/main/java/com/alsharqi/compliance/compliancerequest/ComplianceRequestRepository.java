package com.alsharqi.compliance.compliancerequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import scala.collection.immutable.List;

import javax.transaction.Transactional;
import java.util.Date;

@CrossOrigin
@Repository
public interface ComplianceRequestRepository extends JpaRepository<ComplianceRequest,Long> {

    public Iterable<ComplianceRequest> findAllByOrderById();
    public Iterable<ComplianceRequest> findAllByShipmentNumber(String shipmentNumber);

    public Page<ComplianceRequest> findAllByStatusOrderByIdDesc(String status,Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusAndDueDateBefore(String status, Date date, Pageable page);


    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(Date sDate,Date eDate,String status,Pageable page);
    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(Date sDate,Date eDate,Pageable page);

    public Page<ComplianceRequest> findAllByOrganizationNameAndStatus(String organizationName, String status, Pageable page);
    public Page<ComplianceRequest> findAllByOrganizationNameAndStatusOrderByIdDesc(String organizationName, String status, Pageable page);
    public Page<ComplianceRequest> findAllByOrganizationNameOrderByIdDesc(String organizationName, Pageable page);

    public ComplianceRequest findComplianceRequestByRequestNumber(String requestNumber);

    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByIdDesc(String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeAndOrganizationIdInOrderByIdDesc(Date sDate,Date eDate,java.util.List<String> organizationIds,Pageable page);
    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeAndStatusAndOrganizationIdInOrderByIdDesc(Date sDate,Date eDate,String status,java.util.List<String> organizationIds,Pageable page);
    public Page<ComplianceRequest> findAllByOrganizationNameAndOrganizationIdInOrderByIdDesc(String organizationName,java.util.List<String> organizationIds ,Pageable page);
    public Page<ComplianceRequest> findAllByOrganizationNameAndStatusAndOrganizationIdInOrderByIdDesc(String organizationName, String status,java.util.List<String> organizationIds ,Pageable page);

    @Transactional
    @Modifying
    @Query("Delete  from ComplianceRequest c where c.requestNumber = ?1 ")
    public void deleteComplianceRequestByRequestNumber(String complianceRequestNumber);

    public Page<ComplianceRequest> findAllByOrderById(Pageable pageable);


}
