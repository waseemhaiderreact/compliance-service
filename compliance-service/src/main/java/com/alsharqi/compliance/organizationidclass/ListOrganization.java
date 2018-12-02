package com.alsharqi.compliance.organizationidclass;

import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.compliancerequest.ComplianceFilter;

import java.util.List;

public class ListOrganization {
    List<OrganizationIdCLass> organizationIdCLasses;
    ComplianceFilter filterObject;
    public ListOrganization() {
    }

    public List<OrganizationIdCLass> getOrganizationIdCLasses() {
        return organizationIdCLasses;
    }

    public void setOrganizationIdCLasses(List<OrganizationIdCLass> organizationIdCLasses) {
        this.organizationIdCLasses = organizationIdCLasses;
    }

    public ComplianceFilter getFilterObject() {
        return filterObject;
    }

    public void setFilterObject(ComplianceFilter filterObject) {
        this.filterObject = filterObject;
    }
}
