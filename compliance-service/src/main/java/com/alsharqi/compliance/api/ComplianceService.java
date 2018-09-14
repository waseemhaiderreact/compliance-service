package com.alsharqi.compliance.api;

import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.compliance.ComplianceRepository;
import com.alsharqi.compliance.compliancerequest.ComplianceFilter;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.compliancerequest.ComplianceRequestRepository;
import com.alsharqi.compliance.contact.Contact;
import com.alsharqi.compliance.contact.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

@Service
public class ComplianceService {

    @Autowired
    private ComplianceRequestRepository complianceRequestRepository;

    @Autowired
    private ComplianceRepository complianceRepository;

    @Autowired
    private ContactRepository contactRepository;

    private String compliance_request_status_pending="0";
    private String compliance_request_status_progress="1";
    private String compliance_request_status_complete="2";
    private String compliance_status_pending="0";
    private String compliance_status_progress="1";
    private String compliance_status_complete="2";

    @Transactional
    public ComplianceRequest addRequest(ComplianceRequest complianceRequest){

        //--- loop through each compliance so that
        if(complianceRequest.getCompliances()!=null && complianceRequest.getCompliances().size()>0){
            Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
            while(complianceIterator.hasNext()){
                Compliance compliance = complianceIterator.next();
                compliance.setStatus(compliance_status_pending);
                compliance.setComplianceRequest(complianceRequest);
            }
        }
        //--- saving user and authorities

        try{
            if(complianceRequest.getUser()!=null) {
                Contact contact = contactRepository.findContactByFirstNameAndEmail(complianceRequest.getUser().getFirstName(),
                        complianceRequest.getUser().getEmail());

                if(contact==null)
                    contactRepository.save(complianceRequest.getUser());
            }

            if(complianceRequest.getIssuingAuthority()!=null) {
                Contact contact = contactRepository.findContactByFirstNameAndEmail(complianceRequest.getIssuingAuthority().getFirstName(),
                        complianceRequest.getIssuingAuthority().getEmail());

                if(contact==null)
                    contactRepository.save(complianceRequest.getIssuingAuthority());

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        complianceRequest.setRequestDate(new Date());
        complianceRequest.setStatus(compliance_request_status_pending);
        try {

            complianceRequestRepository.save(complianceRequest);
            complianceRequest.setRequestNumber(getComplianceRequestNumber(complianceRequest.getId()));

            //loop through compliane to set the compliance number
            if(complianceRequest.getCompliances()!=null && complianceRequest.getCompliances().size()>0){
                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while(complianceIterator.hasNext()){
                    Compliance compliance = complianceIterator.next();
                    compliance.setComplianceNumber(getComplianceNumber(compliance.getId()));
                }
            }

            complianceRequestRepository.save(complianceRequest);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return complianceRequest;
    }

    String getComplianceRequestNumber(Long number){
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String formattedDate = df.format(Calendar.getInstance().getTime());
        return "CR-"+formattedDate+String.format("%04d", number) ;
    }

    String getComplianceNumber(Long number){
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String formattedDate = df.format(Calendar.getInstance().getTime());
        return "CL-"+formattedDate+String.format("%04d", number) ;
    }

    @Transactional
    public ComplianceRequest updateRequest(ComplianceRequest complianceRequest){

        //--- loop through each compliance so that
        if(complianceRequest.getCompliances()!=null && complianceRequest.getCompliances().size()>0){
            Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
            while(complianceIterator.hasNext()){
                Compliance compliance = complianceIterator.next();
                compliance.setComplianceRequest(complianceRequest);
            }
        }

        try {
            complianceRequestRepository.save(complianceRequest);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return complianceRequest;
    }

    public Iterable<ComplianceRequest> getAllComplianceRequests(){
        return complianceRequestRepository.findAllByOrderById();
    }

    public Page<ComplianceRequest> getAllComplianceRequests(int offset, int limit){
        return complianceRequestRepository.findAll(new PageRequest(offset,limit));
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsWithFilter(ComplianceFilter complianceFilter,int offset, int limit){

        if(complianceFilter.getCustomerId()==null && complianceFilter.getEndDate()==null && complianceFilter.getStartDate()==null){
            return complianceRequestRepository.findAll(new PageRequest(offset,limit));
        }

        if(complianceFilter.getCustomerId()==null ) {
            if (complianceFilter.getStatus().equalsIgnoreCase("all") ) {
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),"0",new PageRequest(offset,limit));
            }
            else
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),"1",new PageRequest(offset,limit));
        }
        else{
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByCustomerIdOrderByIdDesc(complianceFilter.getCustomerId(),new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByCustomerIdAndStatus(complianceFilter.getCustomerId(),"0",new PageRequest(offset,limit));
                return x;
            }
            else{
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByCustomerIdAndStatus(complianceFilter.getCustomerId(),"1",new PageRequest(offset,limit));
                return x;
            }

        }
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsByShipmentNumber(String shipmentNumber){
        return complianceRequestRepository.findAllByShipmentNumber(shipmentNumber);
    }
}
