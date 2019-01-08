package com.alsharqi.compliance.compliance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface ComplianceRepository extends JpaRepository<Compliance,Long> {
    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(Date sDate, Date eDate, String status, Pageable page);
    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(Date sDate,Date eDate,Pageable page);

    //public Page<Compliance> findAllByCustomerIdAndStatus(Long customerId, String status, Pageable page);
    public Page<Compliance> findAllByComplianceRequest_OrganizationNameAndStatus(String organizationName, String status, Pageable page);
    public Page<Compliance> findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(String organizationName, String status, Pageable page);
    //public Page<Compliance> findAllByCustomerIdOrderByIdDesc(Long customerId, Pageable page);
    public Page<Compliance> findAllByComplianceRequest_OrganizationNameOrderByIdDesc(String organizationName, Pageable page);
    public Page<Compliance> findAllByOrderByIdDesc(Pageable page);

    public Compliance findComplianceByComplianceNumber(String complianceNumber);

    public Page<Compliance> findAllByStatusOrderByIdDesc(String status,Pageable page);

    //-- squad pusrpose
    public Page<Compliance> findAllByComplianceRequest_OrganizationIdInOrderByIdDesc(List<String> stringlist,Pageable page);
    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeAndComplianceRequest_OrganizationIdOrderByIdDesc(Date sDate,Date eDate,List<String> stringlist,Pageable page);
    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(Date sDate, Date eDate, String status,List<String> stringlist, Pageable page);

    @Transactional
    @Modifying
    @Query("Delete  from Compliance c where c.complianceNumber = ?1 ")
    public void deleteComplianceByComplianceNumber(String complianceNumber);
}
