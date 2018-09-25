package com.alsharqi.compliance.compliance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ComplianceRepository extends JpaRepository<Compliance,Long> {
    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(Date sDate, Date eDate, String status, Pageable page);
    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(Date sDate,Date eDate,Pageable page);

    //public Page<Compliance> findAllByCustomerIdAndStatus(Long customerId, String status, Pageable page);
    public Page<Compliance> findAllByComplianceRequest_OrganizationNameAndStatus(String organizationName, String status, Pageable page);
    //public Page<Compliance> findAllByCustomerIdOrderByIdDesc(Long customerId, Pageable page);
    public Page<Compliance> findAllByComplianceRequest_OrganizationNameOrderByIdDesc(String organizationName, Pageable page);
    public Page<Compliance> findAllByOrderByIdDesc(Pageable page);

    public Compliance findComplianceByComplianceNumber(String complianceNumber);
}
