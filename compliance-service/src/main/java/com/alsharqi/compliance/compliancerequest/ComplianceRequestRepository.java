package com.alsharqi.compliance.compliancerequest;

import com.alsharqi.compliance.compliance.Compliance;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Date;

@CrossOrigin
@Repository
public interface ComplianceRequestRepository extends JpaRepository<ComplianceRequest,Long> {

    public Iterable<ComplianceRequest> findAllByOrderById();
    public Iterable<ComplianceRequest> findAllByShipmentNumber(String shipmentNumber);

    public Page<ComplianceRequest> findAllByStatus(String status,Pageable pageable);
    public Page<ComplianceRequest> findAllByStatusAndDueDateBefore(String status, Date date, Pageable page);


    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(Date sDate,Date eDate,String status,Pageable page);
    public Page<ComplianceRequest> findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(Date sDate,Date eDate,Pageable page);

    public Page<ComplianceRequest> findAllByCustomerIdAndStatus(Long customerId, String status, Pageable page);
    public Page<ComplianceRequest> findAllByCustomerIdOrderByIdDesc(Long customerId, Pageable page);
}
