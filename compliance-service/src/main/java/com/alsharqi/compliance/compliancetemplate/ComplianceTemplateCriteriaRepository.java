package com.alsharqi.compliance.compliancetemplate;

import com.alsharqi.compliance.api.requests.ComplianceListFilterRequest;
import com.alsharqi.compliance.cookedcompliance.CookedComplianceTemplate;
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
public class ComplianceTemplateCriteriaRepository implements ComplianceTemplateCriteriaApi{
    private static final Logger LOGGER = LogManager.getLogger(ComplianceTemplateCriteriaRepository.class);
    @Autowired
    EntityManager entityManager;
    private CharSequence val = null; // to store date for LIKE operator to remove records that get matched in hh:mm:ss
    private Boolean dateEqualFlag = false; // to remove previous day records if any present
    private Date dateCheck; // to store date to compare and remove previous day records

    //bring the data by advance filters using advance predicated
    public Page<CookedComplianceTemplate> findComplianceTemplateList(ComplianceListFilterRequest filterRequest, Pageable pageable, String sortOrder, String sortByField, List<String> fieldnames, String searchQuery) {
        List<CookedComplianceTemplate> shipments;
        Page page = null;
        try{
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<CookedComplianceTemplate> criteriaQuery = criteriaBuilder.createQuery(CookedComplianceTemplate.class);
            Root<ComplianceTemplate> shipmentRoot = criteriaQuery.from(ComplianceTemplate.class);
            List<Predicate> predicatesAnd = getAdvancePredicates(filterRequest, criteriaBuilder, shipmentRoot);
            List<Predicate> predicatesOr = getSearchPredicates(fieldnames,searchQuery, criteriaBuilder, shipmentRoot);

            criteriaQuery.where(criteriaBuilder.and(predicatesAnd.toArray(new Predicate[0])), criteriaBuilder.and(predicatesOr.toArray(new Predicate[0])));
            criteriaQuery.select(criteriaBuilder.construct(CookedComplianceTemplate.class, shipmentRoot.get("id"),shipmentRoot.get("typeOfCompliance"), shipmentRoot.get("statusOfCustomer"),
                    shipmentRoot.get("issuingAuthority"), shipmentRoot.get("issuingAuthorityLocation")));

            if(sortOrder.equalsIgnoreCase("asc")){
                criteriaQuery.orderBy(criteriaBuilder.asc(shipmentRoot.get(sortByField)));
            }
            else if(sortOrder.equalsIgnoreCase("desc")){
                criteriaQuery.orderBy(criteriaBuilder.desc(shipmentRoot.get(sortByField)));
            }
            shipments = entityManager.createQuery(criteriaQuery)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            // this is removing records from result that got matched based on hh:mm:ss in LIKE operation
//            List<CookedDeclaration> extractedShipments = new ArrayList<>();
//            if(val != null){
//                for ( int i=0; i<shipments.size(); i++){
//                    Date date = shipments.get(i).getIssueDate();
//                    if(date != null){
//                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                        String strDate = dateFormat.format(date);
//                        if(strDate.contains(val)){
//                            extractedShipments.add(0,shipments.get(i));
//                        }
//                    }
//                }
//                shipments = extractedShipments;
//            }

            // to remove previous day records if any present in EQUAL Dates
//            List<CookedDeclaration> currentDateShipments = new ArrayList<>();
//            if(dateEqualFlag.equals(true)){
//                for ( int i=0; i<shipments.size(); i++){
//                    Date date = shipments.get(i).getIssueDate();
//                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    String strDate = dateFormat.format(date);
//                    String prevDate = dateFormat.format(dateCheck);
//
//                    if(!strDate.contains(prevDate)){
//                        currentDateShipments.add(0,shipments.get(i));
//                    }
//                }
//                shipments = currentDateShipments;
//            }

//            List<CookedDeclaration> currentDateBookings = new ArrayList<>();
//            if(dateEqualFlag.equals(true))
//            {
//                LOGGER.info("Inside date compare function using advance filters");
//                String strDate;
//                String prevDate;
//
//                String strDate1;
//                String prevDate1;
//                Boolean flag=false;
//                Boolean flag1=false;
//                Boolean flag2=false;
//
//                for ( int i=0; i<shipments.size(); i++){
//                    Date date = shipments.get(i).getIssueDate();
//                    if(date.equals(null))
//                    {
//                        flag=false;
//                    }
//                    else
//                    {
//                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                        strDate = dateFormat.format(date);
//                        prevDate = dateFormat.format(dateCheck);
//                        if(!strDate.contains(prevDate))
//                        {
//                            flag=true;
//                        }
//                    }
//                    Date date1=shipments.get(i).getCreationDate();
//                    if(date1.equals(null))
//                    {
//                        flag1=false;
//                    }
//                    else
//                    {
//                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
//                        strDate1 = dateFormat1.format(date);
//                        prevDate1 = dateFormat1.format(dateCheck);
//                        if(!strDate1.contains(prevDate1))
//                        {
//                            flag1=true;
//                        }
//                    }
//                    Date date2=shipments.get(i).getExpiryDate();
//                    if(date2.equals(null))
//                    {
//                        flag2=false;
//                    }
//                    else
//                    {
//                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
//                        strDate1 = dateFormat1.format(date);
//                        prevDate1 = dateFormat1.format(dateCheck);
//                        if(!strDate1.contains(prevDate1))
//                        {
//                            flag2=true;
//                        }
//                    }
//
//                    if(flag.equals(false) && flag1.equals(false) && flag2.equals(false))
//                    {
//
//                    }
//                    else
//                    {
//                        currentDateBookings.add(0, shipments.get(i));
//                    }
//                    flag=false;
//                    flag1=false;
//                    flag2=false;
//                }
//                shipments=currentDateBookings;
//            }

            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<ComplianceTemplate> booksRootCount = countQuery.from(ComplianceTemplate.class);
            countQuery.select(criteriaBuilder.count(booksRootCount)).where(criteriaBuilder.and(predicatesAnd.toArray(new Predicate[0])));
            Long count = entityManager.createQuery(countQuery).getSingleResult();
            page = new PageImpl<>(shipments, pageable, count);
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
    public Page<CookedComplianceTemplate> findAllByLatest(Pageable pageable, String sortOrder, String sortByField, List<String> fieldNames, String searchQuery)
    {
        Page page = null;
        try {
            List<CookedComplianceTemplate> bundleRequestList;
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<CookedComplianceTemplate> criteriaQuery = criteriaBuilder.createQuery(CookedComplianceTemplate.class);
            Root<ComplianceTemplate> bundleRequestRoot = criteriaQuery.from(ComplianceTemplate.class);
            List<Predicate> predicatesOr = getSearchPredicates(fieldNames,searchQuery, criteriaBuilder, bundleRequestRoot);

//            List<Predicate> predicatesList=null;
//            predicatesList = getPredicatesForRegular(criteriaBuilder, bundleRequestRoot);


            criteriaQuery.where(criteriaBuilder.or(predicatesOr.toArray(new Predicate[0])));
            criteriaQuery.select(criteriaBuilder.construct(CookedComplianceTemplate.class,bundleRequestRoot.get("id"), bundleRequestRoot.get("typeOfCompliance"), bundleRequestRoot.get("country"),
                    bundleRequestRoot.get("issuingAuthority") ,bundleRequestRoot.get("issuingAuthorityLocation")));

            if(sortOrder.equalsIgnoreCase("asc")){
                criteriaQuery.orderBy(criteriaBuilder.asc(bundleRequestRoot.get(sortByField)));
            }
            else if(sortOrder.equalsIgnoreCase("desc")){
                criteriaQuery.orderBy(criteriaBuilder.desc(bundleRequestRoot.get(sortByField)));
            }
            bundleRequestList = entityManager.createQuery(criteriaQuery)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<ComplianceTemplate> booksRootCount = countQuery.from(ComplianceTemplate.class);
            countQuery.select(criteriaBuilder.count(booksRootCount)).where(criteriaBuilder.and(predicatesOr.toArray(new Predicate[0])));
            Long count = entityManager.createQuery(countQuery).getSingleResult();
            page = new PageImpl<>(bundleRequestList, pageable, count);
        }
        catch (Exception e) {
            LOGGER.error("An Error occurred while retrieving data", e);
        }
        return page;
    }

    public List<Predicate> getAdvancePredicates(ComplianceListFilterRequest filterRequest, CriteriaBuilder criteriaBuilder, Root<ComplianceTemplate> shipmentRoot) throws ParseException {
        List<Predicate> predicates = new ArrayList<>();
        for (ComplianceListFilterRequest.FilterField filterField : filterRequest.getFilterFieldList()) {
            if (filterField.getCompType().equalsIgnoreCase("=")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1().trim();

                if (filterField.getFieldType().equals("date"))
                {
                    dateEqualFlag = true;
                    // incrementing date by one
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    Calendar c = Calendar.getInstance();
                    c.setTime(endDate);
                    c.add(Calendar.DATE, 1);
                    endDate = c.getTime();

                    //decrementing date by one
                    Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    int daysToDecrement = -1;
                    cal.add(Calendar.DATE, daysToDecrement);
                    startDate = cal.getTime();
                    dateCheck = startDate;

                    predicates.add(criteriaBuilder.greaterThan(shipmentRoot.get(key), startDate));
                    predicates.add(criteriaBuilder.lessThan(shipmentRoot.get(key), endDate));
                }
                else {
                    predicates.add(criteriaBuilder.equal(shipmentRoot.get(key), value));
                }

            }
            if (filterField.getCompType().equalsIgnoreCase("Like")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                if(filterField.getFieldType().equals("date")){
                    val = value;
                }
                predicates.add(criteriaBuilder.like(shipmentRoot.get(key).as(String.class), "%" + value + "%"));

            }
            if (filterField.getCompType().equalsIgnoreCase("btw")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();
                final String value2  = filterField.getValue2();

                if(filterField.getFieldType().equals("date")){
                    Date date =  new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    Date date2 =  new SimpleDateFormat("yyyy-MM-dd").parse(value2);

                    // temporary fix to include range end date records as well for any year other than 2020
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int year1 = cal.get(Calendar.YEAR);
                    cal.setTime(date2);
                    int year2 = cal.get(Calendar.YEAR);
                    if(!(year1 == 2020 || year2 == 2020)){
                        cal.add(Calendar.DATE, 1);
                        date2 = cal.getTime();
                    }

                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(shipmentRoot.get(key),date));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(shipmentRoot.get(key),date2));
                }
                else{
                    predicates.add(criteriaBuilder.between(shipmentRoot.get(key), Double.parseDouble(value), Double.parseDouble(value2)));
                }

            }
            if (filterField.getCompType().equalsIgnoreCase("!=")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();
                if(filterField.getFieldType().equals("date")){
                    Date date =  new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    predicates.add(criteriaBuilder.notEqual(shipmentRoot.get(key),date));
                }
                else {
                    predicates.add(criteriaBuilder.notEqual(shipmentRoot.get(key), value));
                }
            }
            if (filterField.getCompType().equalsIgnoreCase(">")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();
                if(filterField.getFieldType().equals("date")){

                    Date date =  new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    //removing current records for year other than 2020
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    date = cal.getTime();
                    dateCheck = date;
                    dateEqualFlag = true;

                    predicates.add(criteriaBuilder.greaterThan(shipmentRoot.get(key),date));
                }
                else {
                    predicates.add(criteriaBuilder.greaterThan(shipmentRoot.get(key), value));
                }
            }
            if (filterField.getCompType().equalsIgnoreCase("<")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                if(filterField.getFieldType().equals("date")){
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    predicates.add(criteriaBuilder.lessThan(shipmentRoot.get(key),date));
                }
                else {
                    predicates.add(criteriaBuilder.lessThan(shipmentRoot.get(key), value));
                }
            }
            if (filterField.getCompType().equalsIgnoreCase("<=")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                if(filterField.getFieldType().equals("date")){
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(shipmentRoot.get(key),date));
                }
                else {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(shipmentRoot.get(key), value));
                }
            }
            if (filterField.getCompType().equalsIgnoreCase(">=")) {

                final String key = filterField.getFieldName();
                final String value = filterField.getValue1();

                if(filterField.getFieldType().equals("date")){
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(shipmentRoot.get(key),date));
                }
                else {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(shipmentRoot.get(key), value));
                }
            }
        }
        return predicates;
    }

    public List<Predicate> getSearchPredicates(List<String> fieldNames, String searchQuery, CriteriaBuilder criteriaBuilder, Root<ComplianceTemplate> declarationRoot){
        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(criteriaBuilder.equal(declarationRoot.get("active"), 1));
        for ( int i =0; i<fieldNames.size(); i++){
            if(fieldNames.get(i)!= null){
                predicates.add(criteriaBuilder.like(declarationRoot.get(fieldNames.get(i)).as(String.class),"%" +searchQuery+ "%"));
                LOGGER.info("Field names: "+fieldNames.get(i));
            }
        }
        return predicates;
    }

    public List<Predicate> getPredicatesForRegular(CriteriaBuilder criteriaBuilder, Root<ComplianceTemplate> bookingRoot)
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
}
