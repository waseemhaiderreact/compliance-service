package com.alsharqi.compliance.compliance;

import com.alsharqi.compliance.api.requests.ComplianceListFilterRequest;
import com.alsharqi.compliance.cookedcompliance.CookedCompliance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ComplianceCriteriaRepository implements ComplianceCriteriaApi{
    private static final Logger LOGGER = LogManager.getLogger(ComplianceCriteriaRepository.class);
    @Autowired
    EntityManager entityManager;
    private CharSequence val = null; // to store date for LIKE operator to remove records that get matched in hh:mm:ss
    private Boolean dateEqualFlag = false; // to remove previous day records if any present
    private Date dateCheck; // to store date to compare and remove previous day records

    //bring the data by advance filters using advance predicate
    //March 2022, removing unnecessary code and refactoring duplicate code within this class
    public Page<CookedCompliance> findComplianceList(ComplianceListFilterRequest filterRequest, Pageable pageable, String sortOrder, String sortByField, List<String> fieldnames, String searchQuery) {
        List<CookedCompliance> complianceResult;
        Page page = null;
        try{
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<CookedCompliance> criteriaQuery = criteriaBuilder.createQuery(CookedCompliance.class);
            Root<Compliance> complianceRoot = criteriaQuery.from(Compliance.class);
            List<Predicate> predicatesAnd = getAdvancePredicates(filterRequest, criteriaBuilder, complianceRoot);
            List<Predicate> predicatesOr = getSearchPredicates(fieldnames,searchQuery, criteriaBuilder, complianceRoot);

            criteriaQuery.where(criteriaBuilder.and(predicatesAnd.toArray(new Predicate[0])), criteriaBuilder.and(predicatesOr.toArray(new Predicate[0])));
            complianceResult = getCookedCompliances(pageable, sortOrder, sortByField, criteriaBuilder, criteriaQuery, complianceRoot);

            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<CookedCompliance> booksRootCount = countQuery.from(CookedCompliance.class);
            countQuery.select(criteriaBuilder.count(booksRootCount)).where(criteriaBuilder.and(predicatesAnd.toArray(new Predicate[0])));
            Long count = entityManager.createQuery(countQuery).getSingleResult();
            page = new PageImpl<>(complianceResult, pageable, count);
        }
        catch (Exception e){
            LOGGER.error("An Error occurred while retrieving filtered data",e);
        }
        finally {
            val = null;
            dateEqualFlag = false;
            dateCheck = null;
        }

        return page;
    }

    //simply populate the SDT data with criteria api
    public Page<CookedCompliance> findAllByLatest(Pageable pageable, String sortOrder, String sortByField, List<String> fieldNames, String searchQuery)
    {
        Page page = null;
        try {
            List<CookedCompliance> bundleRequestList;
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<CookedCompliance> criteriaQuery = criteriaBuilder.createQuery(CookedCompliance.class);
            Root<Compliance> bundleRequestRoot = criteriaQuery.from(Compliance.class);
            List<Predicate> predicatesOr = getSearchPredicates(fieldNames,searchQuery, criteriaBuilder, bundleRequestRoot);

            List<Predicate> predicatesList=null;
            predicatesList = getPredicatesForRegular(criteriaBuilder, bundleRequestRoot);


            criteriaQuery.where(criteriaBuilder.or(predicatesOr.toArray(new Predicate[0])),criteriaBuilder.and(predicatesList.toArray(new Predicate[0])));
            bundleRequestList = getCookedCompliances(pageable, sortOrder, sortByField, criteriaBuilder, criteriaQuery, bundleRequestRoot);
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<Compliance> booksRootCount = countQuery.from(Compliance.class);
            countQuery.select(criteriaBuilder.count(booksRootCount)).where(criteriaBuilder.and(predicatesOr.toArray(new Predicate[0])));
            Long count = entityManager.createQuery(countQuery).getSingleResult();
            page = new PageImpl<>(bundleRequestList, pageable, count);
        }
        catch (Exception e) {
            LOGGER.error("An Error occurred while retrieving data", e);
        }
        return page;
    }

    public List<Predicate> getPredicatesForRegular(CriteriaBuilder criteriaBuilder, Root<Compliance> bookingRoot)
    {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(bookingRoot.get("active"), 1));
        return predicates;
    }

    public Date getStartOfDay(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    public Date getEndOfDay(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    //March 2022, created this method for code refactoring
    private List<CookedCompliance> getCookedCompliances(Pageable pageable, String sortOrder, String sortByField, CriteriaBuilder criteriaBuilder, CriteriaQuery<CookedCompliance> criteriaQuery, Root<Compliance> complianceRoot) {
        List<CookedCompliance> complianceResult;
        criteriaQuery.select(criteriaBuilder.construct(CookedCompliance.class, complianceRoot.get("id"),complianceRoot.get("typeOfCompliance"), complianceRoot.get("statusOfCustomer"),
                complianceRoot.get("shipmentNumber"), complianceRoot.get("customer")));

        if(sortOrder.equalsIgnoreCase("asc")){
            criteriaQuery.orderBy(criteriaBuilder.asc(complianceRoot.get(sortByField)));
        }
        else if(sortOrder.equalsIgnoreCase("desc")){
            criteriaQuery.orderBy(criteriaBuilder.desc(complianceRoot.get(sortByField)));
        }
        complianceResult = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        return complianceResult;
    }

    //March 2022, removing date check as it was unnecessary for the time being
    //if needed(date check) look for it in the respective classes in Declaration Service for reference
    public List<Predicate> getAdvancePredicates(ComplianceListFilterRequest filterRequest, CriteriaBuilder criteriaBuilder, Root<Compliance> complianceRoot) throws ParseException {
        List<Predicate> predicates = new ArrayList<>();
        for (ComplianceListFilterRequest.FilterField filterField : filterRequest.getFilterFieldList()) {
            if (filterField.getCompType().equalsIgnoreCase("=")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1().trim();

                predicates.add(criteriaBuilder.equal(complianceRoot.get(key), value));

            }
            if (filterField.getCompType().equalsIgnoreCase("Like")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                predicates.add(criteriaBuilder.like(complianceRoot.get(key).as(String.class), "%" + value + "%"));

            }
            if (filterField.getCompType().equalsIgnoreCase("btw")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();
                final String value2  = filterField.getValue2();

                predicates.add(criteriaBuilder.between(complianceRoot.get(key), Double.parseDouble(value), Double.parseDouble(value2)));

            }
            if (filterField.getCompType().equalsIgnoreCase("!=")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                predicates.add(criteriaBuilder.notEqual(complianceRoot.get(key), value));

            }
            if (filterField.getCompType().equalsIgnoreCase(">")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                predicates.add(criteriaBuilder.greaterThan(complianceRoot.get(key), value));

            }
            if (filterField.getCompType().equalsIgnoreCase("<")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                predicates.add(criteriaBuilder.lessThan(complianceRoot.get(key), value));

            }
            if (filterField.getCompType().equalsIgnoreCase("<=")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                predicates.add(criteriaBuilder.lessThanOrEqualTo(complianceRoot.get(key), value));

            }
            if (filterField.getCompType().equalsIgnoreCase(">=")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                predicates.add(criteriaBuilder.greaterThanOrEqualTo(complianceRoot.get(key), value));

            }
        }
        return predicates;
    }

    public List<Predicate> getSearchPredicates(List<String> fieldNames, String searchQuery, CriteriaBuilder criteriaBuilder, Root<Compliance> complianceRoot){
        List<Predicate> predicates = new ArrayList<>();
        for ( int i =0; i<fieldNames.size(); i++){
            if(fieldNames.get(i)!= null){
                predicates.add(criteriaBuilder.like(complianceRoot.get(fieldNames.get(i)).as(String.class),"%" +searchQuery+ "%"));
                LOGGER.info("Field names: "+fieldNames.get(i));
            }
        }
        return predicates;
    }
}
