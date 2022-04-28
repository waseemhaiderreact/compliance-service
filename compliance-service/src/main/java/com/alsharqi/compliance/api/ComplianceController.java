package com.alsharqi.compliance.api;

import com.alsharqi.compliance.api.requests.ComplianceListFilterRequest;
import com.alsharqi.compliance.audittrail.AuditTrail;
import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.compliancerequest.ComplianceFilter;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.compliancerequest.ComplianceRequestDocument;
import com.alsharqi.compliance.compliancetemplate.ComplianceTemplate;
import com.alsharqi.compliance.cookedcompliance.CookedCompliance;
import com.alsharqi.compliance.exception.EmptyEntityTableException;
import com.alsharqi.compliance.organizationidclass.ListOrganization;
import com.alsharqi.compliance.response.DefaultResponse;
import com.alsharqi.compliance.util.AccessDeniedException;
import com.alsharqi.compliance.util.ApplicationException;
import com.alsharqi.compliance.util.Util;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@CrossOrigin
@RequestMapping(path="/compliances")
public class ComplianceController {
    private static final Logger LOGGER = LogManager.getLogger(ComplianceController.class);
    private int paginationLimit=10;

    @Autowired
    private ComplianceService complianceService;

    @Autowired
    private CookedComplianceResponseService cookedComplianceResponseService;

    //saving a compliance
    @RequestMapping(value = "/record/info", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Compliance> saveCompliance(@RequestBody Compliance compliance){
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;
        try{
            LOGGER.info("Adding a new compliance");
            responseEntity = new ResponseEntity<>(complianceService.saveCompliance(compliance),HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error("Cannot Add a new compliance",e);
            responseEntity = new ResponseEntity<>("Cannot add a new compliance",HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return responseEntity;
    }

    //saving a compliance template
    @RequestMapping(value = "/template/record/info", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Compliance> saveComplianceTemplate(@RequestBody ComplianceTemplate complianceTemplate){
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;
        try{
            LOGGER.info("Adding a new compliance template");
            responseEntity = new ResponseEntity<>(complianceService.saveComplianceTemplate(complianceTemplate),HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error("Cannot Add a new compliance template",e);
            responseEntity = new ResponseEntity<>("Cannot add a new compliance template",HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return responseEntity;
    }

    //get all the compliances in the SDT using criteria api
    @RequestMapping(value = "/info", params = {"searchQuery", "sortByField", "sortOrder", "page",
            "size"}, method = RequestMethod.GET)
    @ResponseBody
    public Page<Compliance> findFilterComplianceByCondition(@RequestParam("searchQuery") String searchQuery,
                                                               @RequestParam("sortByField") String sortByField,
                                                               @RequestParam("sortOrder") String sortOrder,
                                                               @RequestParam("page") int page,
                                                               @RequestParam("size") int size){
        Util util = new Util();
        util.setThreadContextForLogging();
        Page<Compliance> data = null;
        String inputParam = searchQuery+": "+sortByField+": "+sortOrder+": "+page+": "+size;
        LOGGER.info("Received parameters For Simple/Search (No Filters) are "+inputParam);
        try{
            data = cookedComplianceResponseService.findComplianceByCondition(searchQuery,sortByField,sortOrder,page,size);
            LOGGER.info("Compliance Table is empty.....");
        }
        catch (Exception e) {
            LOGGER.error("Exception occured while fetching filtered/sorted/paginated Compliance List Input Parameters= "+inputParam, e);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return data;
    }

    //getting all the compliance templates
    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ComplianceTemplate>> getAllComplainceTemplates(){
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;
        List<ComplianceTemplate> complianceTemplates = new ArrayList<ComplianceTemplate>();
        try{
            LOGGER.info("Fetching all compliance templates ");
            complianceTemplates = complianceService.getAllComplianceTemplates();
            responseEntity = new ResponseEntity<>
                    (complianceTemplates,HttpStatus.OK);
        }catch (Exception e) {
            LOGGER.error("Cannot fetch all compliance templates", e);
            responseEntity = new ResponseEntity<>("Cannot get all the compliance", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return  responseEntity;
    }

    //getting audit trail against a compliance number
    @RequestMapping(value = "/audit/{complianceNumber}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAuditByComplianceNumber(@PathVariable String complianceNumber){
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity;
        List<AuditTrail> auditTrails = new ArrayList<AuditTrail>();
        try{
            LOGGER.info("Getting all the audit trail against compliance number ",complianceNumber);
            auditTrails = complianceService.getAuditByComplianceNumber(complianceNumber);
            responseEntity = new ResponseEntity<>(auditTrails,HttpStatus.OK);
        }catch (Exception e){
            LOGGER.info("Cannot get the audit trail against compliance number ",complianceNumber);
            responseEntity = new ResponseEntity<>("cannot get audit history based on compliance number"+complianceNumber,HttpStatus.OK);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return responseEntity;
    }

    //getting a template based on compliance type
    @RequestMapping(value = "/template/{typeOfCompliance}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getComplainceTemplateByTypeOfCompliance(@PathVariable String typeOfCompliance){
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;
        try{
            LOGGER.info("Fetching a template based on type ",typeOfCompliance);
            responseEntity = new ResponseEntity<>
                    (complianceService.getComplianceTemplateByTypeOfCompliance(typeOfCompliance),HttpStatus.OK);
        }catch (Exception e) {
            LOGGER.error("Cannot fetch a template based on its type " + typeOfCompliance, e);
            responseEntity = new ResponseEntity<>("Cannot get a compliance template based on type", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return  responseEntity;
    }

    //fetching a compliance template based on its id
    @RequestMapping(value = "/template/record/{templateId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getComplainceTemplateById(@PathVariable Long templateId){
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;

        try{
            LOGGER.info("Fetching a template based on its id ",templateId);
            responseEntity = new ResponseEntity<>(complianceService.getComplianceTemplateById(templateId),HttpStatus.OK);
        }catch (Exception e) {
            LOGGER.error("Cannot fetch a template with id " + templateId, e);
            responseEntity = new ResponseEntity<>("Cannot get a compliance template", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return  responseEntity;
    }

    //fetching a compliance based on its id
    @RequestMapping(value = "/record/{complianceId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getComplaincById(@PathVariable Long complianceId) throws AccessDeniedException{
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;
        try{
            LOGGER.info("Fetching a compliance based on its id ",complianceId);
            responseEntity = new ResponseEntity<>(complianceService.getComplianceById(complianceId),HttpStatus.OK);
        }
        catch (AccessDeniedException aed){
            LOGGER.error("Access denied due to privilege not available");
            throw aed;
        }
        catch (Exception e) {
            LOGGER.error("Cannot fetch a compliance with id " + complianceId, e);
            responseEntity = new ResponseEntity<>("Cannot get a compliance ", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return  responseEntity;
    }

    //getting all compliances template in the SDT using criteriaApi
    @RequestMapping(value = "/template/info", params = {"searchQuery", "sortByField", "sortOrder", "page",
            "size"}, method = RequestMethod.GET)
    @ResponseBody
    public Page<Compliance> findFilterComplianceTemplateByCondition(@RequestParam("searchQuery") String searchQuery,
                                                            @RequestParam("sortByField") String sortByField,
                                                            @RequestParam("sortOrder") String sortOrder,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("size") int size){
        Util util = new Util();
        util.setThreadContextForLogging();
        Page<Compliance> data = null;
        String inputParam = searchQuery+": "+sortByField+": "+sortOrder+": "+page+": "+size;
        LOGGER.info("Received parameters For Simple/Search (No Filters) are "+inputParam);
        try{
            data = cookedComplianceResponseService.findComplianceTemplateByCondition(searchQuery,sortByField,sortOrder,page,size);
            LOGGER.info("compliance template Table is empty.....");
        }
        catch (Exception e) {
            LOGGER.error("Exception occured while fetching filtered/sorted/paginated Compliance template List Input Parameters= "+inputParam, e);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return data;
    }

    /** Add Compliance with Multiple Documents **/

    /**
     * The Data will be Received as Form Data and then the object will be taken as form data takes string
     **/
    @RequestMapping(value = "/add" , method = RequestMethod.POST,consumes = {"multipart/form-data"})
    @ResponseBody
    ResponseEntity addCompliance(@RequestParam("complianceDocument") List<MultipartFile> complianceDocument,
                                            @RequestParam("compliances") String request,
                                            @RequestParam("userName") String userName) throws AccessDeniedException {
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;
        try {
            LOGGER.info("adding a new compliance using object mapper");
            ObjectMapper objectMapper=new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            Compliance mapper=objectMapper.readValue(request,Compliance.class);
            LOGGER.info("Adding Compliance with compliance number : "+mapper.getComplianceNumber());
            ResponseEntity responseEntity1 = complianceService.saveComplianceMapper(mapper,complianceDocument,userName);
            if (responseEntity1.getStatusCode() == HttpStatus.OK && complianceDocument.size() > 0){
                responseEntity = new ResponseEntity<>("Document Saved and Compliance added with compliance number : "+mapper.getComplianceNumber(),HttpStatus.OK);
            }
            else if (responseEntity1.getStatusCode() == HttpStatus.CONFLICT){
                responseEntity = new ResponseEntity<>("Document Not Saved but Compliance added with compliance number : "+mapper.getComplianceNumber(),HttpStatus.OK);
            }
            else if (responseEntity1.getStatusCode() == HttpStatus.OK) {
                responseEntity = new ResponseEntity<>("Compliance Successfully addded with compliance number : " + mapper.getComplianceNumber(), HttpStatus.OK);
            }
            return responseEntity;
        }
        catch (AccessDeniedException aed){
            LOGGER.error("Access denied due to privilege not available");
            throw aed;
        }
        catch (Exception e){
            LOGGER.error("cannot save a mapper object",e);
            responseEntity = new ResponseEntity<>("Cannot save compliance with mapper",HttpStatus.OK);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return responseEntity;
    }

    //editing a compliance template
    @RequestMapping(value = "/template/record/update", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> updateComplianceTemplate(@RequestBody ComplianceTemplate complianceTemplate){
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;
        try {
            LOGGER.info("updating compliance template with id",complianceTemplate.getId());
            responseEntity = new ResponseEntity(complianceService.saveComplianceTemplate(complianceTemplate),HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error("Cannot update compliance template with id ",complianceTemplate.getId());
            responseEntity = new ResponseEntity("Could not update compliance template",HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
        return responseEntity;
    }

    //editing a compliance and creating a n audit trail
    @RequestMapping(value = "/record/update", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> updateCompliance(@RequestBody Compliance compliance) throws AccessDeniedException{
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;
        try {
            LOGGER.info("updating compliance with id",compliance.getId());
            responseEntity = new ResponseEntity(complianceService.updateCompliance(compliance),HttpStatus.OK);
        }
        catch (AccessDeniedException aed){
            LOGGER.error("Access denied due to privilege not available");
            throw aed;
        }
        catch (Exception e){
            LOGGER.error("Cannot update compliance template with id ",compliance.getId());
            responseEntity = new ResponseEntity("Could not update compliance template",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    //getting relative date for due date
    @RequestMapping(value = "/template/duedate", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getRelativeDate(){
        Util util = new Util();
        util.setThreadContextForLogging();
        ResponseEntity responseEntity = null;
        try {
            LOGGER.info("Getting relative date for due date");
            responseEntity = new ResponseEntity<>(complianceService.getRelativeTimeUsingDate(),HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error("Could not get the converted date",e);
            responseEntity = new ResponseEntity<>("Failed to get relative date",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }





    //getting compliance data using advance filters


//    @RequestMapping(value="/requests",method= RequestMethod.POST)
//    @ResponseBody
//    public ComplianceRequest addNewRequest(@RequestBody ComplianceRequest complianceRequest){
//        return complianceService.addRequest(complianceRequest);
//    }
//
//    @RequestMapping(value="/requests",method= RequestMethod.PUT)
//    @ResponseBody
//    public ComplianceRequest updateRequest(@RequestBody ComplianceRequest complianceRequest){
//        return complianceService.updateRequest(complianceRequest);
//    }

    // edit compliance
//    @RequestMapping(value="",method= RequestMethod.PUT)
//    @ResponseBody
//    public Compliance updateCompliance(@RequestBody Compliance compliance){
//        return complianceService.updateCompliance(compliance);
//    }

    @RequestMapping(method = RequestMethod.GET,value="/requests", params = {"offset","limit","sort","orderBy"})
    public @ResponseBody
    ResponseEntity getAllComplianceRequestssWithPagination(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,
            @RequestParam("sort") String sort,
            @RequestParam("orderBy") String orderBy) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllComplianceRequestsPending(offset,limit,sort,orderBy))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllComplianceRequestsPending(0,paginationLimit,sort,orderBy))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
        }
    }

    @RequestMapping(method = RequestMethod.GET,value="/conditionalRequests", params = {"searchQuery","offset","limit","sort","orderBy"})
    public @ResponseBody
    ResponseEntity getConditionalComplianceRequestssWithPagination(
            @RequestParam("searchQuery") String searchQuery,
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,
            @RequestParam("sort") String sort,
            @RequestParam("orderBy") String orderBy) throws EmptyEntityTableException,ParseException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getConditionalComplianceRequests(searchQuery,offset,limit,sort,orderBy))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));

        }
        else {
            return Optional.ofNullable(complianceService.getConditionalComplianceRequests(searchQuery,0,paginationLimit,sort,orderBy))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
        }
    }

    @RequestMapping(method = RequestMethod.PUT,value="/requests/filter", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllComplianceRequestssWithPaginationAndFilter(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit, @RequestBody ComplianceFilter complianceFilter) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllComplianceRequestsWithFilter(complianceFilter,offset,limit))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllComplianceRequestsWithFilter(complianceFilter,0,paginationLimit))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
        }
    }


    /*@RequestMapping(method = RequestMethod.GET,value="", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllCompliancePagination(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllComplianceRequests(offset,limit))
                    .map(resp -> new ResponseEntity<Page<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No Organization Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllComplianceRequests())
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No template exists.",0L));
        }
    }*/

    @RequestMapping(value="/requests",method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity findAllRequestsByShipmentNumber(@RequestParam("shipmentNumber") String shipmentNumber) throws EmptyEntityTableException {
        return Optional.ofNullable(complianceService.getAllComplianceRequestsByShipmentNumber(shipmentNumber))
                .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
    }

//    @RequestMapping(method = RequestMethod.PUT,value="/filter", params = {"offset","limit"})
//    public @ResponseBody
//    ResponseEntity getAllComplianceWithPaginationAndFilter(
//            @RequestParam("offset") int offset,
//            @RequestParam("limit") int limit, @RequestBody ComplianceFilter complianceFilter) throws EmptyEntityTableException {
//        if(offset>=0 && limit>0){
//            return Optional.ofNullable(complianceService.getAllCompliancesWithFilter(complianceFilter,offset,limit))
//                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
//        }
//        else {
//            return Optional.ofNullable(complianceService.getAllCompliancesWithFilter(complianceFilter,0,paginationLimit))
//                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
//        }
//    }

//    @RequestMapping(method = RequestMethod.GET,value="", params = {"sort","sortBy","offset","limit"})
//    public @ResponseBody
//    ResponseEntity getAllCompliancesWithPagination(
//            @RequestParam("sort") String sort,
//            @RequestParam("sortBy") String sortBy,
//            @RequestParam("offset") int offset,
//            @RequestParam("limit") int limit) throws EmptyEntityTableException {
//        if(offset>=0 && limit>0){
//            return Optional.ofNullable(complianceService.getAllCompliancesPending(sort,sortBy,offset,limit))
//                    .map(resp -> new ResponseEntity<Page<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request exists",0L));
//        }
//        else {
//            return Optional.ofNullable(complianceService.getAllCompliancesPending(sort,sortBy,0,paginationLimit))
//                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
//        }
//    }

//    @RequestMapping(method = RequestMethod.GET,value="/compliancesSearch", params = {"searchQuery","sort","sortBy","offset","limit"})
//    public @ResponseBody
//    ResponseEntity getAllCompliancesWithPagination(
//            @RequestParam("searchQuery") String searchQuery,
//            @RequestParam("sort") String sort,
//            @RequestParam("sortBy") String sortBy,
//            @RequestParam("offset") int offset,
//            @RequestParam("limit") int limit) throws EmptyEntityTableException {
//        if(offset>=0 && limit>0){
//            return Optional.ofNullable(complianceService.getAllCompliancesByCondition(searchQuery,sort,sortBy,offset,limit))
//                    .map(resp -> new ResponseEntity<Page<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request exists",0L));
//        }
//        else {
//            return Optional.ofNullable(complianceService.getAllCompliancesByCondition(searchQuery,sort,sortBy,0,paginationLimit))
//                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
//        }
//    }

    /*DFF-986*/
//    @RequestMapping(method = RequestMethod.GET,value="/requests/documents")
//    public @ResponseBody
//    ResponseEntity fetchComplianceRequestDocument(@RequestParam("requestNumber") String requestNumber) throws EmptyEntityTableException {
//
//        return Optional.ofNullable(complianceService.getCompliaceRequestDocument(requestNumber))
//                .map(resp -> new ResponseEntity<ComplianceRequestDocument>(resp, HttpStatus.OK))
//                .orElseThrow(() -> new EmptyEntityTableException("No request exists",0L));
//    }
//
//    //Task:432 Re-Index Compliance Request -Ammar
//    @CrossOrigin
//    @PostMapping(path="/re-index")
//    public @ResponseBody
//    ResponseEntity ReIndexComplianceRequests() throws EmptyEntityTableException {
//        return Optional.ofNullable(complianceService.indexComplianceRequests())
//                .map(resp -> new ResponseEntity<DefaultResponse>(resp, HttpStatus.OK))
//                .orElseThrow(() -> new EmptyEntityTableException("No Compliance Request exists",0L));
//
//    }

    /*
    * Added by SB
    * Created for squad purpose
    * */
//    @RequestMapping(method = RequestMethod.POST,value="", params = {"searchQuery","sort","sortBy","offset","limit"})
//    public @ResponseBody
//    ResponseEntity getAllCompliancesWithPaginationAndSquads(
//            @RequestParam("searchQuery") String searchQuery,
//            @RequestParam("sort") String sort,
//            @RequestParam("sortBy") String sortBy,
//            @RequestParam("offset") int offset,
//            @RequestParam("limit") int limit,@RequestBody ListOrganization listOrganization) throws EmptyEntityTableException {
//        if(offset>=0 && limit>0 && searchQuery.isEmpty()){
//            return Optional.ofNullable(complianceService.getAllCompliancesPending(sort,sortBy,offset,limit))
//                    .map(resp -> new ResponseEntity<Page<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request exists",0L));
//        }
//        else if(offset>=0 && limit>0 && !searchQuery.isEmpty()){
//            return Optional.ofNullable(complianceService.getAllCompliancesByCondition(searchQuery,sort,sortBy,offset,limit))
//                    .map(resp -> new ResponseEntity<Page<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request exists",0L));
//        }
//        else {
//            return Optional.ofNullable(complianceService.getAllCompliancesPending(sort,sortBy,0,paginationLimit))
//                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
//        }
//    }


    /*
     * Added by SB
     * Created for squad purpose
     * */
//    @RequestMapping(method = RequestMethod.POST,value="/filter", params = {"offset","limit"})
//    public @ResponseBody
//    ResponseEntity getAllComplianceWithPaginationAndFilterAndSquad(
//            @RequestParam("offset") int offset,
//            @RequestParam("limit") int limit, @RequestBody ListOrganization listOrganization) throws EmptyEntityTableException {
//        if(offset>=0 && limit>0){
//            return Optional.ofNullable(complianceService.getAllCompliancesWithFilterBySquad(listOrganization,offset,limit))
//                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
//        }
//        else {
//            return Optional.ofNullable(complianceService.getAllCompliancesWithFilterBySquad(listOrganization,0,paginationLimit))
//                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
//                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
//        }
//    }

    //get compliance requests by squads
    @RequestMapping(method = RequestMethod.POST,value="/requests", params = {"searchQuery","sort","sortBy","offset","limit"})
    public @ResponseBody
    ResponseEntity getAllComplianceRequestssWithPaginationAndSquad(
            @RequestParam("searchQuery") String searchQuery,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("sort") String sort,
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,@RequestBody ListOrganization listOrganization) throws EmptyEntityTableException {
        if(offset>=0 && limit>0 && searchQuery.isEmpty()){
            return Optional.ofNullable(complianceService.getAllComplianceRequestsPendingBySquad(sortBy,sort,offset,limit,listOrganization))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else if(offset>=0 && limit>0 && !searchQuery.isEmpty()){
            return Optional.ofNullable(complianceService.getAllComplianceRequestsBySquad(searchQuery,sortBy,sort,offset,limit,listOrganization))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllComplianceRequestsPendingBySquad(sortBy,sort,0,paginationLimit,listOrganization))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
        }
    }

    @RequestMapping(method = RequestMethod.POST,value="/requests/filter", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllComplianceRequestssWithPaginationAndFilterAndSquad(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit, @RequestBody ListOrganization listOrganization) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllComplianceRequestsWithFilterAndSquad(listOrganization,offset,limit))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllComplianceRequestsWithFilterAndSquad(listOrganization,0,paginationLimit))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
        }
    }

    // edit compliance
//    @RequestMapping(value="",method= RequestMethod.DELETE,params = {"complianceNumber"})
//    @ResponseBody
//    public ResponseEntity deleteCompliance(@RequestParam("complianceNumber") String complianceNumber){
//        DefaultResponse defaultResponse = complianceService.deleteComplianceByComplianceNumber(complianceNumber);
//        return new ResponseEntity(defaultResponse,HttpStatus.OK);
//    }

//    @RequestMapping(value="/requests",method= RequestMethod.DELETE,params = {"complianceRequestNumber"})
//    @ResponseBody
//    public ResponseEntity deleteComplianceRequest(@RequestParam("complianceRequestNumber") String complianceRequestNumber){
//        DefaultResponse defaultResponse = complianceService.deleteComplianceRequestByComplianceRequestNumber(complianceRequestNumber);
//        return new ResponseEntity(defaultResponse,HttpStatus.OK);
//    }

    @GetMapping(value = "/requests/all")
    public @ResponseBody
    ResponseEntity<Iterable<ComplianceRequest>> getAllComplianceRequestss() throws EmptyEntityTableException {

            return Optional.ofNullable(complianceService.getAllComplianceRequests())
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));

    }

//    @RequestMapping(value="/files/upload",method= RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity uploadFile(@RequestParam("document") List<MultipartFile> multipartFileList,@RequestParam("complianceNumber") String complianceNumber){
//        return new ResponseEntity(complianceService.uploadComplianceFileService(multipartFileList,complianceNumber),HttpStatus.OK);
//    }

    @RequestMapping(value="/files/s3",method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity fetchFileFromS3Controller(@RequestParam("url") String url){
        return new ResponseEntity(complianceService.getFile(url),HttpStatus.OK);
    }

    //March 2022, creating endpoint for advance filters
    //getting compliance based on advance filters
    @PostMapping(value = "/filteredData", params = {"searchQuery","sortByField", "sortOrder", "page", "size" })
    @ResponseBody
    public Page<CookedCompliance> findComplianceListByCriteria(@RequestParam("searchQuery") String searchQuery,
                                                               @RequestParam("sortByField") String sortByField,
                                                               @RequestParam("sortOrder") String sortOrder,
                                                               @RequestParam("page") int page,
                                                               @RequestParam("size") int size,
                                                               @RequestBody ComplianceListFilterRequest filterRequest) {

        Util util = new Util();
        util.setThreadContextForLogging();
        Page<CookedCompliance> complianceList = null;
        LOGGER.info("Parameters of request (Compliance Advance Filters): "+ searchQuery);
        try{
            complianceList = cookedComplianceResponseService.getComplianceByAdvanceCriteria("", sortByField, sortOrder, page, size, filterRequest);
            return  complianceList;
        }
        catch(Exception e){
            LOGGER.info("Exception occurred while retrieving data "+ e);
            return complianceList; //emptyList
        }finally{
            util.clearThreadContextForLogging();
            util = null;
        }
    }
}
