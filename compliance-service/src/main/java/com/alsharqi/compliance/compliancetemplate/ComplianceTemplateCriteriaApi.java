package com.alsharqi.compliance.compliancetemplate;

import com.alsharqi.compliance.api.requests.ComplianceListFilterRequest;
import com.alsharqi.compliance.cookedcompliance.CookedComplianceTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ComplianceTemplateCriteriaApi {
    Page<CookedComplianceTemplate> findComplianceTemplateList(ComplianceListFilterRequest filterRequest, Pageable pageable, String sortOrder, String sortByField, List<String> fieldnames, String searchQuery);

}
