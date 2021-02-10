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
//    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(Date sDate, Date eDate, String status, Pageable page);
//    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(Date sDate,Date eDate,Pageable page);
//
//    //public Page<Compliance> findAllByCustomerIdAndStatus(Long customerId, String status, Pageable page);
//    public Page<Compliance> findAllByComplianceRequest_OrganizationNameAndStatus(String organizationName, String status, Pageable page);
//    public Page<Compliance> findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(String organizationName, String status, Pageable page);
//    //public Page<Compliance> findAllByCustomerIdOrderByIdDesc(Long customerId, Pageable page);
//    public Page<Compliance> findAllByComplianceRequest_OrganizationNameOrderByIdDesc(String organizationName, Pageable page);
//    public Page<Compliance> findAllByOrderByIdDesc(Pageable page);
//
//    public Compliance findComplianceByComplianceNumber(String complianceNumber);
//
//    public Page<Compliance> findAllByStatusOrderByIdAsc(String status,Pageable page);
//    public Page<Compliance> findAllByStatusOrderByIdDesc(String status,Pageable page);
//
//    public Page<Compliance> findAllByStatusOrderByTypeAsc(String status,Pageable page);
//    public Page<Compliance> findAllByStatusOrderByTypeDesc(String status,Pageable page);
//
//    public Page<Compliance> findAllByStatusOrderByDueDateAsc(String status,Pageable page);
//    public Page<Compliance> findAllByStatusOrderByDueDateDesc(String status,Pageable page);
//
//    public Page<Compliance> findAllByStatusOrderByUserFirstNameAsc(String status,Pageable page);
//    public Page<Compliance> findAllByStatusOrderByUserFirstNameDesc(String status,Pageable page);
//
//    public Page<Compliance> findAllByStatusOrderByIssuingAuthorityAuthorityAsc(String status,Pageable page);
//    public Page<Compliance> findAllByStatusOrderByIssuingAuthorityAuthorityDesc(String status,Pageable page);
//
//    public Page<Compliance> findAllByStatusOrderByComplianceRequestOrganizationNameAsc(String status,Pageable page);
//    public Page<Compliance> findAllByStatusOrderByComplianceRequestOrganizationNameDesc(String status,Pageable page);
//
//    public Page<Compliance> findAllByOrderByStatusDesc(Pageable page);
//    public Page<Compliance> findAllByOrderByStatusAsc(Pageable page);
//
//    //-- squad pusrpose
//    public Page<Compliance> findAllByComplianceRequest_OrganizationIdInOrderByIdDesc(List<String> stringlist,Pageable page);
//    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeAndComplianceRequest_OrganizationIdOrderByIdDesc(Date sDate,Date eDate,List<String> stringlist,Pageable page);
//    public Page<Compliance> findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(Date sDate, Date eDate, String status,List<String> stringlist, Pageable page);
//
//    @Transactional
//    @Modifying
//    @Query("Delete  from Compliance c where c.complianceNumber = ?1 ")
//    public void deleteComplianceByComplianceNumber(String complianceNumber);
//
//    @Transactional
//    @Modifying
//    @Query("Delete  from Compliance c where c.complianceNumber IN ?1 ")
//    public void deleteAllByComplianceNumbers(List<String> complianceNumbers);
//
//    //Filter Queries --MSA
//    public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIdAsc(
//        String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//    public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIdDesc(
//        String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//        public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByUserFirstNameAsc(
//            String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//        public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByUserFirstNameDesc(
//            String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//            public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIssuingAuthorityAuthorityAsc(
//                String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//            public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIssuingAuthorityAuthorityDesc(
//                String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//                public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByComplianceRequestOrganizationNameAsc(
//                    String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//                public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByComplianceRequestOrganizationNameDesc(
//                    String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//                    public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByTypeAsc(
//                        String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//                    public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByTypeDesc(
//                        String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//                        public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByDueDateAsc(
//                            String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//                        public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByDueDateDesc(
//                            String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//                            public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByStatusAsc(
//                                String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
//
//                            public Page<Compliance> findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByStatusDesc(
//                                String complianceNumber,String firstName,String lastName,String authority,String type,String orgName,String dueDate,String status,Pageable page);
}

