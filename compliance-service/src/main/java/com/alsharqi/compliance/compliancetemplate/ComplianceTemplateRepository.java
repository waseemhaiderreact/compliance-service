package com.alsharqi.compliance.compliancetemplate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplianceTemplateRepository extends JpaRepository<ComplianceTemplate,Long> {
    ComplianceTemplate findComplianceTemplateByTypeOfCompliance(String typeOfCompliance);
}
