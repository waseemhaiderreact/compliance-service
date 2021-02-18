package com.alsharqi.compliance.compliancerequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
//import scala.collection.immutable.List;

import javax.transaction.Transactional;
import java.util.Date;

@CrossOrigin
@Repository
public interface ComplianceRequestRepository extends JpaRepository<ComplianceRequest,Long> {

    public Iterable<ComplianceRequest> findAllByOrderById();
    public Iterable<ComplianceRequest> findAllByShipmentNumber(String shipmentNumber);

    public Page<ComplianceRequest> findAllByStatusOrderByIdDesc(String status,Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusOrderByIdAsc(String status,Pageable pageable);

    public Page<ComplianceRequest> findAllByOrderByStatusDesc(Pageable pageable);
    public Page<ComplianceRequest> findAllByOrderByStatusAsc(Pageable pageable);

    public Page<ComplianceRequest> findAllByStatusOrderByOrganizationNameAsc(String status,Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusOrderByOrganizationNameDesc(String status,Pageable pageable);

    public Page<ComplianceRequest> findAllByStatusOrderByTypeAsc(String status,Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusOrderByTypeDesc(String status,Pageable pageable);

    public Page<ComplianceRequest> findAllByStatusOrderByDueDateAsc(String status,Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusOrderByDueDateDesc(String status,Pageable pageable);

    public Page<ComplianceRequest> findAllByStatusAndDueDateBefore(String status, Date date, Pageable page);


    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(Date sDate,Date eDate,String status,Pageable page);
    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(Date sDate,Date eDate,Pageable page);

    public Page<ComplianceRequest> findAllByOrganizationNameAndStatus(String organizationName, String status, Pageable page);
    public Page<ComplianceRequest> findAllByOrganizationNameAndStatusOrderByIdDesc(String organizationName, String status, Pageable page);
    public Page<ComplianceRequest> findAllByOrganizationNameOrderByIdDesc(String organizationName, Pageable page);

    public ComplianceRequest findComplianceRequestByRequestNumber(String requestNumber);

    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByIdDesc(String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByIdAsc(String status, java.util.List<String> organizationIds, Pageable pageable);

        public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByOrganizationNameDesc(String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByOrganizationNameAsc(String status, java.util.List<String> organizationIds, Pageable pageable);

    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByDueDateDesc(String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByDueDateAsc(String status, java.util.List<String> organizationIds, Pageable pageable);

    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByStatusDesc(String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByStatusAsc(String status, java.util.List<String> organizationIds, Pageable pageable);

    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByTypeDesc(String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusAndOrganizationIdInOrderByTypeAsc(String status, java.util.List<String> organizationIds, Pageable pageable);

    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByIdDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByIdAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status, java.util.List<String> organizationIds, Pageable pageable);

    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByOrganizationNameDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByOrganizationNameAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status, java.util.List<String> organizationIds, Pageable pageable);

    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByDueDateDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByDueDateAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status, java.util.List<String> organizationIds, Pageable pageable);

    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByStatusDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByStatusAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status, java.util.List<String> organizationIds, Pageable pageable);

    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByTypeDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status, java.util.List<String> organizationIds, Pageable pageable);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByTypeAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status, java.util.List<String> organizationIds, Pageable pageable);

    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeAndOrganizationIdInOrderByIdDesc(Date sDate,Date eDate,java.util.List<String> organizationIds,Pageable page);
    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeAndStatusAndOrganizationIdInOrderByIdDesc(Date sDate,Date eDate,String status,java.util.List<String> organizationIds,Pageable page);
    public Page<ComplianceRequest> findAllByOrganizationNameAndOrganizationIdInOrderByIdDesc(String organizationName,java.util.List<String> organizationIds ,Pageable page);
    public Page<ComplianceRequest> findAllByOrganizationNameAndStatusAndOrganizationIdInOrderByIdDesc(String organizationName, String status,java.util.List<String> organizationIds ,Pageable page);

    @Transactional
    @Modifying
    @Query("Delete  from ComplianceRequest c where c.requestNumber = ?1 ")
    public void deleteComplianceRequestByRequestNumber(String complianceRequestNumber);

    public Page<ComplianceRequest> findAllByOrderById(Pageable pageable);

    //Filter Query w/ Sorting
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIdAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIdDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);

    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByOrganizationNameAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByOrganizationNameDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);

    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByTypeAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByTypeDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);

    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByDueDateAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByDueDateDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);

    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByStatusAsc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);
    public Page<ComplianceRequest> findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByStatusDesc(String requestNumber,String shipmentNumber,String organizationName,String type,String dueDate,String status,Pageable page);


    /*
    * two type contains as one is for import and one is for customs for example
    * */
    public int countAllByShipmentNumberAndTypeContainsAndTypeContainsAndStatusIn(String shipmentNumber,String typeKeyword ,String secondTypeKeyword,java.util.List<String> statusList);
}
