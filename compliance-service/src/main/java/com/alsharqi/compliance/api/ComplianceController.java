package com.alsharqi.compliance.api;

import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.compliancerequest.ComplianceFilter;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.compliancerequest.ComplianceRequestDocument;
import com.alsharqi.compliance.exception.EmptyEntityTableException;
import com.alsharqi.compliance.organizationidclass.ListOrganization;
import com.alsharqi.compliance.response.DefaultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@CrossOrigin
@RequestMapping(path="/compliances")
public class ComplianceController {

    private int paginationLimit=10;

    @Autowired
    private ComplianceService complianceService;

    @RequestMapping(value="/requests",method= RequestMethod.POST)
    @ResponseBody
    public ComplianceRequest addNewRequest(@RequestBody ComplianceRequest complianceRequest){
        return complianceService.addRequest(complianceRequest);
    }

    @RequestMapping(value="/requests",method= RequestMethod.PUT)
    @ResponseBody
    public ComplianceRequest updateRequest(@RequestBody ComplianceRequest complianceRequest){
        return complianceService.updateRequest(complianceRequest);
    }

    // edit compliance
    @RequestMapping(value="",method= RequestMethod.PUT)
    @ResponseBody
    public Compliance updateCompliance(@RequestBody Compliance compliance){
        return complianceService.updateCompliance(compliance);
    }

    @RequestMapping(method = RequestMethod.GET,value="/requests", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllComplianceRequestssWithPagination(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllComplianceRequestsPending(offset,limit))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllComplianceRequestsPending(0,paginationLimit))
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

    @RequestMapping(method = RequestMethod.PUT,value="/filter", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllComplianceWithPaginationAndFilter(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit, @RequestBody ComplianceFilter complianceFilter) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllCompliancesWithFilter(complianceFilter,offset,limit))
                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllCompliancesWithFilter(complianceFilter,0,paginationLimit))
                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
        }
    }

    @RequestMapping(method = RequestMethod.GET,value="", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllCompliancesWithPagination(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllCompliancesPending(offset,limit))
                    .map(resp -> new ResponseEntity<Page<Compliance>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllCompliancesPending(0,paginationLimit))
                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
        }
    }

    /*DFF-986*/
    @RequestMapping(method = RequestMethod.GET,value="/requests/documents")
    public @ResponseBody
    ResponseEntity fetchComplianceRequestDocument(@RequestParam("requestNumber") String requestNumber) throws EmptyEntityTableException {

        return Optional.ofNullable(complianceService.getCompliaceRequestDocument(requestNumber))
                .map(resp -> new ResponseEntity<ComplianceRequestDocument>(resp, HttpStatus.OK))
                .orElseThrow(() -> new EmptyEntityTableException("No request exists",0L));
    }

    //Task:432 Re-Index Compliance Request -Ammar
    @CrossOrigin
    @PostMapping(path="/re-index")
    public @ResponseBody
    ResponseEntity ReIndexComplianceRequests() throws EmptyEntityTableException {
        return Optional.ofNullable(complianceService.indexComplianceRequests())
                .map(resp -> new ResponseEntity<DefaultResponse>(resp, HttpStatus.OK))
                .orElseThrow(() -> new EmptyEntityTableException("No Compliance Request exists",0L));

    }

    /*
    * Added by SB
    * Created for squad purpose
    * */
    @RequestMapping(method = RequestMethod.POST,value="", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllCompliancesWithPaginationAndSquads(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,@RequestBody ListOrganization listOrganization) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllCompliancesPending(offset,limit))
                    .map(resp -> new ResponseEntity<Page<Compliance>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllCompliancesPending(0,paginationLimit))
                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
        }
    }


    /*
     * Added by SB
     * Created for squad purpose
     * */
    @RequestMapping(method = RequestMethod.POST,value="/filter", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllComplianceWithPaginationAndFilterAndSquad(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit, @RequestBody ListOrganization listOrganization) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllCompliancesWithFilterBySquad(listOrganization,offset,limit))
                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllCompliancesWithFilterBySquad(listOrganization,0,paginationLimit))
                    .map(resp -> new ResponseEntity<Iterable<Compliance>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));
        }
    }

    //get compliance requests by squads
    @RequestMapping(method = RequestMethod.POST,value="/requests", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllComplianceRequestssWithPaginationAndSquad(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,@RequestBody ListOrganization listOrganization) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllComplianceRequestsPendingBySquad(offset,limit,listOrganization))
                    .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllComplianceRequestsPendingBySquad(0,paginationLimit,listOrganization))
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
    @RequestMapping(value="",method= RequestMethod.DELETE,params = {"complianceNumber"})
    @ResponseBody
    public ResponseEntity deleteCompliance(@RequestParam("complianceNumber") String complianceNumber){
        DefaultResponse defaultResponse = complianceService.deleteComplianceByComplianceNumber(complianceNumber);
        return new ResponseEntity(defaultResponse,HttpStatus.OK);
    }

    @RequestMapping(value="/requests",method= RequestMethod.DELETE,params = {"complianceRequestNumber"})
    @ResponseBody
    public ResponseEntity deleteComplianceRequest(@RequestParam("complianceRequestNumber") String complianceRequestNumber){
        DefaultResponse defaultResponse = complianceService.deleteComplianceRequestByComplianceRequestNumber(complianceRequestNumber);
        return new ResponseEntity(defaultResponse,HttpStatus.OK);
    }
}
