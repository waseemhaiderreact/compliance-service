package com.alsharqi.compliance.compliance;

import com.alsharqi.compliance.api.requests.ComplianceListFilterRequest;
import com.alsharqi.compliance.cookedcompliance.CookedCompliance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ComplianceCriteriaApi {
    Page<CookedCompliance> findComplianceList(ComplianceListFilterRequest filterRequest, Pageable pageable, String sortOrder, String sortByField, List<String> fieldnames, String searchQuery);

}
