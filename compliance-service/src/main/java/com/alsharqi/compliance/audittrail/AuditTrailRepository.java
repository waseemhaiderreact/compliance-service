package com.alsharqi.compliance.audittrail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditTrailRepository extends JpaRepository<AuditTrail,Long> {
    List<AuditTrail> getAuditTrailByComplianceNumber(String complianceNumber);
}
