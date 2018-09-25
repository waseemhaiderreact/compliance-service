package com.alsharqi.compliance.api;

import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.compliance.ComplianceRepository;
import com.alsharqi.compliance.compliancerequest.ComplianceFilter;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.compliancerequest.ComplianceRequestDocument;
import com.alsharqi.compliance.compliancerequest.ComplianceRequestRepository;
import com.alsharqi.compliance.contact.Contact;
import com.alsharqi.compliance.contact.ContactRepository;
import com.alsharqi.compliance.events.notification.NotificationModel;
import com.alsharqi.compliance.events.notification.NotificationSourceBean;
import com.alsharqi.compliance.notification.Notification;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @Autowired
    private NotificationSourceBean notificationSourceBean;

    private String compliance_request_status_pending="0";
    private String compliance_request_status_progress="1";
    private String compliance_request_status_complete="2";
    private String compliance_status_pending="0";
    private String compliance_status_progress="1";
    private String compliance_status_complete="2";

    /*Font section*/
    Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    Font catContentFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.NORMAL);
    Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10,
            Font.NORMAL, BaseColor.RED);
    Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);
    Font subContentFont = new Font(Font.FontFamily.TIMES_ROMAN, 11,
            Font.NORMAL);


    Font mini = new Font(Font.FontFamily.HELVETICA, 4);
    Font miniBold = new Font(Font.FontFamily.HELVETICA, 4,Font.BOLD);
    Font miniGray = new Font(Font.FontFamily.HELVETICA, 4,Font.NORMAL,BaseColor.GRAY);
    Font small = new Font(Font.FontFamily.HELVETICA, 6);
    Font smallBold = new Font(Font.FontFamily.HELVETICA, 6,Font.BOLD);
    Font smallGray = new Font(Font.FontFamily.HELVETICA, 6,Font.NORMAL,BaseColor.GRAY);
    //smallGray.setColor(BaseColor.GRAY);
    Font smallBoldGray = new Font(Font.FontFamily.HELVETICA, 6,Font.BOLD,BaseColor.GRAY);
    //smallBoldGray.setColor(BaseColor.GRAY);
    Font normal = new Font(Font.FontFamily.HELVETICA, 8);
    Font normalBold = new Font(Font.FontFamily.HELVETICA, 8,Font.BOLD);
    Font normalGray = new Font(Font.FontFamily.HELVETICA, 8);
    //normalGray.setColor(BaseColor.GRAY);
    Font normalBoldGray = new Font(Font.FontFamily.HELVETICA, 8,Font.BOLD,BaseColor.GRAY);
    //normalBoldGray.setColor(BaseColor.GRAY);
    Font large = new Font(Font.FontFamily.HELVETICA, 8);
    Font largeBold = new Font(Font.FontFamily.HELVETICA, 8,Font.BOLD);
    Font largeBoldBlue = new Font(Font.FontFamily.HELVETICA, 8,Font.BOLD,new BaseColor(36,106,180));
    Font heading1 = new Font(Font.FontFamily.HELVETICA, 12);
    Font heading1Bold = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD,new BaseColor(106,206,242));
    Font heading1BoldBlue = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD,new BaseColor(36,106,180));
    BaseColor lightGray = new BaseColor(211,211,211);
    BaseColor lightBlue= new BaseColor(106,206,242);

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
            if(complianceRequest.getCompliances().size()>0) {
                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while(complianceIterator.hasNext()){
                    Compliance currentCompliance = complianceIterator.next();
                    Notification aNotification = new Notification();
                    aNotification.setMessage("Compliance Topic: "+ currentCompliance.getType());
                    aNotification.setUsername("Qafila");
                    aNotification.setReadStatus(false);
                    aNotification.setType("auto");
                    aNotification.setShipmentNumber(complianceRequest.getShipmentNumber());
                    NotificationModel notification = new NotificationModel("CREATE", aNotification, "carrierBooked");
                    notificationSourceBean.publishNewNotification(notification);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //*******  Create Notification to send to the User about carrier booking  **********

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

        ComplianceRequest dbComplianceRequest=complianceRequestRepository.findComplianceRequestByRequestNumber(complianceRequest.getRequestNumber());

        if(dbComplianceRequest!=null) {
            complianceRequest.setContent(dbComplianceRequest.getContent());
            //--- loop through each compliance so that
            if (complianceRequest.getCompliances() != null && complianceRequest.getCompliances().size() > 0) {
                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while (complianceIterator.hasNext()) {
                    Compliance compliance = complianceIterator.next();
                    compliance.setComplianceRequest(complianceRequest);
                }
            }

            Contact user = contactRepository.findContactByFirstNameAndEmail(complianceRequest.getUser().getFirstName(),
                    complianceRequest.getUser().getEmail());
            if (user == null) {
                contactRepository.save(complianceRequest.getUser());
            }


            Contact authority = contactRepository.findContactByFirstNameAndEmail(complianceRequest.getIssuingAuthority().getFirstName(),
                    complianceRequest.getIssuingAuthority().getEmail());
            if (authority == null) {
                contactRepository.save(complianceRequest.getIssuingAuthority());
            }
            //check if the status is complete, create document.
            if (compliance_request_status_complete.equalsIgnoreCase(complianceRequest.getStatus())) {
                try {
                    complianceRequest.setContent(generateDocumentRequestOrder(complianceRequest));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                complianceRequestRepository.save(complianceRequest);
            } catch (Exception e) {
                complianceRequest.setId(null);
                e.printStackTrace();
            }
        }
        else {
            complianceRequest.setId(null);
        }

        return complianceRequest;
    }

    byte[] generateDocumentRequestOrder(ComplianceRequest complianceRequest)  throws IOException {
        Document document = new Document(PageSize.A4);
        document.setMargins(46.0F,36.0F,-6F,36.0F);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);
            document.open();
            Paragraph extraLines = new Paragraph();



            document.add(getdDocumentHeader());

            // add watermark

            document.add(getDocumentSectionCompanyDetails(complianceRequest.getShipmentNumber()));
            document.add(addEmptyLineWithBorder(2));
            document.add(getDocumentSectionCompliancesHeading(complianceRequest));
            document.add(addEmptyLineWithBorder(2));
            document.add(getCompliancesData(complianceRequest));
            document.close();
        }
        catch(DocumentException ex){
            ex.printStackTrace();
        }

        return out.toByteArray();
    }

    PdfPTable getdDocumentHeader() {

        Image img = null;
        try {
            img = Image.getInstance("classpath:images/Qafila-logo-email-signature.png");
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        img.scaleToFit(100f, 40f);
        PdfPTable headerTable = new PdfPTable(4);
        headerTable.getDefaultCell().setBorder(0);
        headerTable.setWidthPercentage(100);

        PdfPCell logo = new PdfPCell(img);
        logo.setPaddingTop(65);
        logo.setPaddingBottom(0);
        logo.setBorder(0);
        headerTable.addCell(logo);
        try {
            img = Image.getInstance("classpath:images/watermark.PNG");
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        img.scaleToFit(140f, 240f);
        PdfPCell watermark = new PdfPCell(img);
        watermark.setPaddingBottom(0);
        watermark.setHorizontalAlignment(Element.ALIGN_RIGHT);
        watermark.setPaddingRight(-40);
        watermark.setBorder(0);
        headerTable.addCell(new Paragraph(" "));
        headerTable.addCell(new Paragraph(" "));
        headerTable.addCell(watermark);
        return headerTable;
    }

    PdfPTable getDocumentSectionCompanyDetails(String shipmentNumber){
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[] { 2, 1 });
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        Paragraph first = new Paragraph("Al Sharqi Shipping Co. LLC",normalBold);
        Paragraph second;// = new Paragraph("Shipment Number: QAF-18001015",normal);
        PdfPCell leftCell = new PdfPCell(first);

        Chunk chunk = new Chunk("Shipment Number: ",normalBoldGray);
        second = new Paragraph();
        second.add(chunk);
        chunk = new Chunk(shipmentNumber,normal);
        second.add(chunk);
        PdfPCell rightCell = new PdfPCell(second);
        leftCell.setPaddingLeft(10);
        leftCell.setBorder(0);
        //set border left color
        leftCell.setUseVariableBorders(true);
        leftCell.setBorderWidthLeft(1);
        leftCell.setBorderColorLeft(lightBlue);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        rightCell.setBorderWidthLeft(1);
        rightCell.setBorderColorLeft(lightBlue);
        rightCell.setPaddingLeft(10);
        table.addCell(leftCell);
        table.addCell(rightCell);

        chunk = new Chunk("Address:",normalBoldGray);
        first = new Paragraph();
        first.add(chunk);

        leftCell = new PdfPCell(first);
        leftCell.setPaddingLeft(10);


        chunk = new Chunk("Trip Date: ",normalBoldGray);
        second = new Paragraph();
        second.add(chunk);
        //TODO: to be revied as what date is needed to be added
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        chunk = new Chunk(formattedDate,normal);
        second.add(chunk);
        rightCell = new PdfPCell(second);
        leftCell.setBorder(0);

        leftCell.setBorderWidthLeft(1);
        leftCell.setBorderColorLeft(lightBlue);
        rightCell.setBorder(0);
        //rightCell.setPaddingBottom(0);
        rightCell.setPaddingLeft(10);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        rightCell.setBorderWidthLeft(1);
        rightCell.setBorderColorLeft(lightBlue);
        //leftCell.setFixedHeight(2);
        table.addCell(leftCell);
        table.addCell(rightCell);
        float height = leftCell.getHeight();

        chunk = new Chunk("151 Khalid Bin Walid Road, Umm Hurrair 1, Dubai,",normal);
        first = new Paragraph();
        first.add(chunk);
        second = new Paragraph(" ",mini);
        leftCell = new PdfPCell(first);
        leftCell.setPaddingTop(10);;

        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        leftCell.setBorder(0);
        //set border left color
        leftCell.setPaddingLeft(10);
        leftCell.setPaddingTop(0);;
        leftCell.setPaddingBottom(0L);
        leftCell.setBorderWidthLeft(1);
        leftCell.setBorderColorLeft(lightBlue);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        rightCell.setBorderWidthLeft(1);
        rightCell.setBorderColorLeft(lightBlue);

        rightCell.setPaddingTop(0);
        table.addCell(leftCell);
        table.addCell(rightCell);


        first = new Paragraph("United Arab Emirates",normal);
        second = new Paragraph(" ",mini);
        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        leftCell.setBorder(0);
        leftCell.setPaddingLeft(10);
        leftCell.setPaddingTop(0);;
        leftCell.setPaddingBottom(0L);
        leftCell.setBorderWidthLeft(1);
        leftCell.setBorderColorLeft(lightBlue);


        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        rightCell.setBorderWidthLeft(1);
        rightCell.setBorderColorLeft(lightBlue);
        rightCell.setPaddingBottom(0);
        rightCell.setPaddingTop(0);
        table.addCell(leftCell);
        table.addCell(rightCell);

        chunk = new Chunk("Phone: ",normalBoldGray);
        first = new Paragraph();
        first.add(chunk);
        chunk = new Chunk("121212 ",normal);
        first.add(chunk);
        second = new Paragraph(" ",mini);
        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        leftCell.setBorder(0);
        //set border left color
        leftCell.setUseVariableBorders(true);
        leftCell.setBorderWidthLeft(1);
        leftCell.setBorderColorLeft(lightBlue);
        leftCell.setPaddingTop(0);;
        leftCell.setPaddingBottom(0L);
        leftCell.setPaddingLeft(10);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        rightCell.setBorderWidthLeft(1);
        rightCell.setBorderColorLeft(lightBlue);
        rightCell.setPaddingBottom(0);
        rightCell.setPaddingTop(0);
        table.addCell(leftCell);
        table.addCell(rightCell);


        /*Email*/
        chunk = new Chunk("Email: ",normalBoldGray);
        first = new Paragraph();
        first.add(chunk);
        chunk = new Chunk("ae.finance@alsharqi.co ",normal);

        first.add(chunk);
        second = new Paragraph(" ",mini);
        leftCell = new PdfPCell(first);
        leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        leftCell.setBorder(0);
        //set border left color
        leftCell.setUseVariableBorders(true);
        leftCell.setBorderWidthLeft(1);
        leftCell.setBorderColorLeft(lightBlue);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        rightCell.setBorderWidthLeft(1);
        rightCell.setBorderColorLeft(lightBlue);
        table.addCell(leftCell);
        table.addCell(rightCell);

        return table;
    }

    PdfPTable getDocumentSectionCompliancesHeading(ComplianceRequest complianceRequest){
        PdfPTable table = new PdfPTable(3);
        table.getDefaultCell().setBorder(0);
        //table.getDefaultCell().setBackgroundColor(lightGray);
        table.setWidthPercentage(100);

        PdfPCell firstCell;
        PdfPCell secondCell = new PdfPCell(new Paragraph());
        PdfPCell thirdCell = new PdfPCell(new Paragraph());
        secondCell.setBackgroundColor(BaseColor.WHITE);
        thirdCell.setBackgroundColor(BaseColor.WHITE);
        //Heading
        Paragraph first = new Paragraph();

        Chunk chunk = new Chunk("Compliance Details", heading1BoldBlue);
        first.add(chunk);
        firstCell = new PdfPCell(first);

        firstCell.setBorder(0);
        //set border left color
        firstCell.setUseVariableBorders(true);
        firstCell.setBorderWidthLeft(1);
        firstCell.setPaddingLeft(10);
        firstCell.setBorderColorLeft(lightBlue);
        Paragraph second = new Paragraph("");// = new Paragraph("Shipment Number: QAF-18001015",normal);
        Paragraph third = new Paragraph("");// = new Paragraph("Shipment Number: QAF-18001015",normal);
        secondCell.setBorder(0);
        thirdCell.setBorder(0);
        //set border left color
        //thirdCell.setUseVariableBorders(true);
        //thirdCell.setBorderWidthLeft(1);

        thirdCell.setPaddingLeft(10);
        table.addCell(firstCell);
        table.addCell(secondCell);
        table.addCell(thirdCell);





        return table;
    }

    PdfPTable getCompliancesData(ComplianceRequest complianceRequest){

        PdfPTable table = new PdfPTable(6);
        //table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);
        Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
        while(complianceIterator.hasNext()) {
            Compliance compliance = complianceIterator.next();
            PdfPCell firstCell;
            PdfPCell secondCell = new PdfPCell(new Paragraph());
            PdfPCell thirdCell = new PdfPCell(new Paragraph());
            PdfPCell  fourthCell = new PdfPCell(new Paragraph());
            PdfPCell fifthCell = new PdfPCell(new Paragraph());
            PdfPCell sixthCell = new PdfPCell(new Paragraph());
            secondCell.setBackgroundColor(BaseColor.WHITE);
            thirdCell.setBackgroundColor(BaseColor.WHITE);

            Paragraph first = new Paragraph();
            Paragraph second = new Paragraph();
            Paragraph third = new Paragraph();
            Paragraph fourth = new Paragraph();
            Paragraph fifth = new Paragraph();
            Paragraph sixth = new Paragraph();
            first = new Paragraph();
            second = new Paragraph();
            Chunk chunk = new Chunk("Number:  \n\t", normalBoldGray);
            first.add(chunk);
            chunk = new Chunk(compliance.getComplianceNumber(), normal);
            first.add(chunk);
            firstCell = new PdfPCell(first);
            firstCell.setBorder(0);
            firstCell.setPaddingLeft(10);
            chunk = new Chunk("Status:  \n\t", normalBoldGray);
            second.add(chunk);
            chunk = new Chunk(compliance.getStatus(), normal);
            second.add(chunk);
            secondCell = new PdfPCell(second);
            secondCell.setBorder(0);

            third = new Paragraph();
            fourth = new Paragraph();
            chunk = new Chunk("Due Date:  \n\t", normalBoldGray);
            third.add(chunk);
            chunk = new Chunk(String.valueOf(compliance.getDueDate()), normal);
            third.add(chunk);
            thirdCell = new PdfPCell(third);
            thirdCell.setBorder(0);
            thirdCell.setPaddingLeft(10);
            chunk = new Chunk("Date Of Completion:  \n\t", normalBoldGray);
            fourth.add(chunk);
            chunk = new Chunk(String.valueOf(compliance.getDateOfCompletion()), normal);
            fourth.add(chunk);
            fourthCell = new PdfPCell(fourth);
            fourthCell.setBorder(0);

            fifth = new Paragraph();
            chunk = new Chunk("Type:  \n\t", normalBoldGray);
            fifth.add(chunk);
            chunk = new Chunk(compliance.getType(), normal);
            fifth.add(chunk);
            fifthCell = new PdfPCell(fifth);
            fifthCell.setBorder(0);
            fifthCell.setPaddingLeft(10);

            //set border left color
            firstCell.setUseVariableBorders(true);
            firstCell.setBorderWidthLeft(1);
            firstCell.setBorderColorLeft(lightBlue);
            table.addCell(firstCell);
            table.addCell(secondCell);
            table.addCell(thirdCell);
            table.addCell(fourthCell);
            table.addCell(fifthCell);
            sixthCell.setBorder(0);
            table.addCell(sixthCell);
        }
        return table;
    }


    public Iterable<ComplianceRequest> getAllComplianceRequests(){
        return complianceRequestRepository.findAllByOrderById();
    }

    public Page<ComplianceRequest> getAllComplianceRequests(int offset, int limit){
        return complianceRequestRepository.findAll(new PageRequest(offset,limit));
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsWithFilter(ComplianceFilter complianceFilter,int offset, int limit){

        if(complianceFilter.getOrganizationName()==null && complianceFilter.getEndDate()==null && complianceFilter.getStartDate()==null){
            return complianceRequestRepository.findAll(new PageRequest(offset,limit));
        }

        if(complianceFilter.getOrganizationName()==null ) {
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
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameOrderByIdDesc(complianceFilter.getOrganizationName(),new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatus(complianceFilter.getOrganizationName(),"0",new PageRequest(offset,limit));
                return x;
            }
            else{
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatus(complianceFilter.getOrganizationName(),"1",new PageRequest(offset,limit));
                return x;
            }

        }
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsByShipmentNumber(String shipmentNumber){
        return complianceRequestRepository.findAllByShipmentNumber(shipmentNumber);
    }

    public Iterable<Compliance> getAllCompliancesWithFilter(ComplianceFilter complianceFilter,int offset, int limit){

        if(complianceFilter.getOrganizationName()==null && complianceFilter.getEndDate()==null && complianceFilter.getStartDate()==null){
            return complianceRepository.findAllByOrderByIdDesc(new PageRequest(offset,limit));
        }

        if(complianceFilter.getOrganizationName()==null ) {
            if (complianceFilter.getStatus().equalsIgnoreCase("all") ) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),"0",new PageRequest(offset,limit));
            }
            else
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),"1",new PageRequest(offset,limit));
        }
        else{
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameOrderByIdDesc(complianceFilter.getOrganizationName(),new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatus(complianceFilter.getOrganizationName(),"0",new PageRequest(offset,limit));
                return x;
            }
            else{
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatus(complianceFilter.getOrganizationName(),"1",new PageRequest(offset,limit));
                return x;
            }

        }
    }

    public Page<Compliance> getAllCompliances(int offset, int limit){
        return complianceRepository.findAll(new PageRequest(offset,limit));
    }

    public ComplianceRequestDocument getCompliaceRequestDocument(String requestNumber){
        ComplianceRequest complianceRequest = complianceRequestRepository.findComplianceRequestByRequestNumber(requestNumber);

        try {
            ComplianceRequestDocument complianceRequestDocument = new ComplianceRequestDocument();
            complianceRequestDocument.setContent(generateDocumentRequestOrder(complianceRequest));
            if(complianceRequest!=null){

                return complianceRequestDocument;
            }
            else
                return null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        //complianceRequestRepository.save(complianceRequest);
        return null;

    }

    /*For now this table has 2 columns. When converted to generic , pdf table will also be passed, aong with column numbers*/
    private PdfPTable addEmptyLineWithBorder(int number) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 2, 1 });

        for (int i = 0; i < number; i++) {
            Paragraph paragraph1 = new Paragraph(" ");
            PdfPCell pdfPCell = new PdfPCell(paragraph1);
            //set border left color
            pdfPCell.setUseVariableBorders(true);
            pdfPCell.setBorder(0);
            pdfPCell.setBorderWidthLeft(1);
            pdfPCell.setBorderColorLeft(lightBlue);
            PdfPCell pdfPCell1 = new PdfPCell(new Paragraph(" "));
            pdfPCell1.setBorder(0);
            //pdfPCell1.setBorderWidthLeft(1);
            //pdfPCell1.setBorderColorLeft(lightBlue);

            table.addCell(pdfPCell);
            table.addCell(pdfPCell1);
        }
        return table;
    }

    @Transactional
    public Compliance updateCompliance(Compliance compliance){

        Compliance dbCompliance=complianceRepository.findComplianceByComplianceNumber(compliance.getComplianceNumber());

        if(dbCompliance!=null) {
            compliance.setComplianceRequest(dbCompliance.getComplianceRequest());
            //--- loop through each compliance so that
            dbCompliance.copyComplianceValues(compliance);
            try {
                complianceRepository.save(dbCompliance);
            } catch (Exception e) {
                compliance.setId(null);
                e.printStackTrace();
            }
        }
        else {
            compliance.setId(null);
        }

        return compliance;
    }
}
