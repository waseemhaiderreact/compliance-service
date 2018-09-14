package com.alsharqi.compliance.api;

import com.alsharqi.compliance.compliancerequest.ComplianceFilter;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.exception.EmptyEntityTableException;
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

    @RequestMapping(method = RequestMethod.GET,value="/requests", params = {"offset","limit"})
    public @ResponseBody
    ResponseEntity getAllComplianceRequestssWithPagination(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit) throws EmptyEntityTableException {
        if(offset>=0 && limit>0){
            return Optional.ofNullable(complianceService.getAllComplianceRequests(offset,limit))
                    .map(resp -> new ResponseEntity<Page<ComplianceRequest>>(resp, HttpStatus.OK))
                    .orElseThrow(() -> new EmptyEntityTableException("No request Exists",0L));
        }
        else {
            return Optional.ofNullable(complianceService.getAllComplianceRequests(0,paginationLimit))
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

    @RequestMapping(method = RequestMethod.GET,value="", params = {"offset","limit"})
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
    }

    @RequestMapping(value="/requests",method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity findAllRequestsByShipmentNumber(@RequestParam("shipmentNumber") String shipmentNumber) throws EmptyEntityTableException {
        return Optional.ofNullable(complianceService.getAllComplianceRequestsByShipmentNumber(shipmentNumber))
                .map(resp -> new ResponseEntity<Iterable<ComplianceRequest>>(resp, HttpStatus.OK))
                .orElseThrow(() -> new EmptyEntityTableException("No request exists.",0L));

    }
}
