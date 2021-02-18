package com.alsharqi.compliance.api;

import com.alsharqi.compliance.compliance.ComplianceCriteriaRepository;
import com.alsharqi.compliance.compliancetemplate.ComplianceTemplateCriteriaRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class CookedComplianceResponseService {
    private static final Logger LOGGER = LogManager.getLogger(CookedComplianceResponseService.class);

    @Autowired
    ComplianceCriteriaRepository complianceCriteriaRepository;

    @Autowired
    ComplianceTemplateCriteriaRepository complianceTemplateCriteriaRepository;

    public Pageable getPageObject(String sortOrder, String sortByField, int size, int page){
        Sort sort = null;
        try {
            if (sortOrder.equalsIgnoreCase("Asc")) {
                sort = new Sort(new Sort.Order(Sort.Direction.ASC, sortByField));
            }
            if (sortOrder.equalsIgnoreCase("Desc")) {
                sort = new Sort(new Sort.Order(Sort.Direction.DESC, sortByField));
            }
            return new PageRequest(page, size, sort);
        }
        catch (Exception e){
            LOGGER.error("Failed to create a page for pageable object",e);
            return null;
        }
    }

    /**SDT GET CONTROLLER FOR Compliance**/
    public Page findComplianceByCondition(String searchQuery, String sortByField, String sortOrder, int page, int size)
    {
        String inputParam = searchQuery + ": " + sortByField + ": " + sortOrder + ": " + page + ": " + size;
        LOGGER.info(inputParam);
        Pageable pageable  = new PageRequest(page, size, null);
        List<String> fieldNames = new ArrayList<>();
        try {
            fieldNames.add("typeOfCompliance");
            fieldNames.add("statusOfCustomer");
            fieldNames.add("shipmentNumber");
            fieldNames.add("customer");

        }
        catch (Exception e){
            LOGGER.error("Exception occurred while fetching Compliances List (get table) Input Parameters= "+inputParam, e);
        }
        return complianceCriteriaRepository.findAllByLatest(pageable, sortOrder, sortByField, fieldNames, searchQuery);
    }

    /**SDT GET CONTROLLER FOR Compliance template**/
    public Page findComplianceTemplateByCondition(String searchQuery, String sortByField, String sortOrder, int page, int size)
    {
        String inputParam = searchQuery + ": " + sortByField + ": " + sortOrder + ": " + page + ": " + size;
        LOGGER.info(inputParam);
        Pageable pageable  = new PageRequest(page, size, null);
        List<String> fieldNames = new ArrayList<>();
        try {
            fieldNames.add("templateName");
            fieldNames.add("typeOfCompliance");
            fieldNames.add("complianceName");
            fieldNames.add("complianceShortCode");
            fieldNames.add("visibleToCustomer");
        }
        catch (Exception e){
            LOGGER.error("Exception occurred while fetching Compliances template List (get table) Input Parameters= "+inputParam, e);
        }
        return complianceTemplateCriteriaRepository.findAllByLatest(pageable, sortOrder, sortByField, fieldNames, searchQuery);
    }

}
