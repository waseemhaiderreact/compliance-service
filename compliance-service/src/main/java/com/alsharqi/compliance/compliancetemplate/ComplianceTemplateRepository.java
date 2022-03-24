package com.alsharqi.compliance.compliancetemplate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplianceTemplateRepository extends JpaRepository<ComplianceTemplate,Long> {
    ComplianceTemplate findComplianceTemplateByTypeOfCompliance(String typeOfCompliance);
    ComplianceTemplate findComplianceTemplateById(Long id);
    List<ComplianceTemplate> findComplianceListByTypeOfCompliance(String typeOfCompliance);
}
