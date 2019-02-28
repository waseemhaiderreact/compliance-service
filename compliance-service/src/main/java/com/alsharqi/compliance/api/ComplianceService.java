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
import com.alsharqi.compliance.location.Location;
import com.alsharqi.compliance.notification.Notification;
import com.alsharqi.compliance.organizationidclass.ListOrganization;
import com.alsharqi.compliance.organizationidclass.OrganizationIdCLass;
import com.alsharqi.compliance.response.DefaultResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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

    @Autowired
    KafkaAsynService kafkaAsynService;

    private final String compliance_request_status_pending="0";
    private final String compliance_request_status_progress="1";
    private final String compliance_request_status_complete="2";
    private final String compliance_status_pending="1";
    private final String compliance_status_progress="2";
    private final String compliance_status_complete="3";

    //DFF-1086
    private final String compliance_status_unassigned="0";
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
                compliance.setStatus(compliance_status_unassigned);
                compliance.setComplianceRequest(complianceRequest);
            }
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

            //Send complianceRequest to Search Service-Ammar
            kafkaAsynService.sendCompliance(complianceRequest);

            if(complianceRequest.getCompliances().size()>0) {
                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while(complianceIterator.hasNext()){
                    Compliance currentCompliance = complianceIterator.next();
                    Notification aNotification = new Notification();
                    aNotification.setMessage("Compliance Created: "+ currentCompliance.getType());
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
            Set<Compliance> complianceSet = new HashSet<Compliance>() ;
            complianceRequest.setContent(dbComplianceRequest.getContent());
            //--- loop through each compliance so that
            if (complianceRequest.getCompliances() != null && complianceRequest.getCompliances().size() > 0) {

                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while (complianceIterator.hasNext()) {


                    Compliance compliance = complianceIterator.next();
                        complianceSet.add(compliance);
                }
                //complianceRequest.setCompliances(complianceSet);
            }

            //-- check for status if complete, add date of completion
            String requestStatus = complianceRequest.getStatus();
            if(compliance_request_status_complete.equals(requestStatus) && compliance_status_complete.equals(dbComplianceRequest.getStatus())==false){
                complianceRequest.setDateOfCompletion(new Date());
                try {
                    complianceRequest.setContent(generateDocumentRequestOrder(complianceRequest));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            try {

                if(complianceSet.size()>0){
                    complianceSet = addComplianceSet(complianceSet);
                    Iterator<Compliance> iterator = complianceSet.iterator();
                    while(iterator.hasNext())
                    iterator.next().setComplianceRequest(complianceRequest);
                }

                complianceRequest.setCompliances(complianceSet);
                complianceRequestRepository.save(complianceRequest);

                //send compliance request to search-service -Ammar
                kafkaAsynService.sendCompliance(complianceRequest);
            } catch (Exception e) {
                complianceRequest.setId(null);
                e.printStackTrace();
            }
        }
        else {
            complianceRequest.setId(null);
        }

        return (complianceRequest);
    }

    byte[] generateDocumentRequestOrder(ComplianceRequest complianceRequest)  throws IOException {
        Document document = new Document(PageSize.A4);
        document.setMargins(46.0F,36.0F,-6F,36.0F);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter writer = PdfWriter.getInstance(document, out);

            //QAF-1.0.3    - SB
            //add footer
            FooterTable event = new FooterTable(getFooterPdf());
            writer.setPageEvent(event);
            document.open();
            Paragraph extraLines = new Paragraph();



            document.add(getdDocumentHeader());

            // add watermark

            document.add(getDocumentSectionCompanyDetails(complianceRequest.getShipmentNumber(),complianceRequest.getOrganizationName(),complianceRequest.getHeadOffice()));
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

    PdfPTable getDocumentSectionCompanyDetails(String shipmentNumber,String organizationName,Location headOffice){

        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[] { 2, 1 });
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        Paragraph first = new Paragraph(organizationName,normalBold);
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


        chunk = new Chunk("Issue Date: ",normalBoldGray);
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

        chunk = new Chunk(headOffice.getAddress1()+", "+headOffice.getCity()+",",normal);
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


        first = new Paragraph(headOffice.getCountry(),normal);
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

        Contact headOfficeContact = headOffice.getContacts().iterator().next();

        chunk = new Chunk(headOfficeContact.getPhone(),normal);
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
        chunk = new Chunk(headOfficeContact.getEmail(),normal);

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

        PdfPTable table = new PdfPTable(7);
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
            PdfPCell seventhCell = new PdfPCell(new Paragraph());
            secondCell.setBackgroundColor(BaseColor.WHITE);
            thirdCell.setBackgroundColor(BaseColor.WHITE);

            Paragraph first = new Paragraph();
            Paragraph second = new Paragraph();
            Paragraph third = new Paragraph();
            Paragraph fourth = new Paragraph();
            Paragraph fifth = new Paragraph();
            Paragraph sixth = new Paragraph();
            Paragraph seventh = new Paragraph();
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
            chunk = new Chunk(getComplianceStatusLabel(compliance.getStatus()), normal);
            second.add(chunk);
            secondCell = new PdfPCell(second);
            secondCell.setBorder(0);
            fourth = new Paragraph();
            chunk = new Chunk("Request Date:  \n\t", normalBoldGray);
            third.add(chunk);
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
            if(compliance.getRequestDate()!=null)
            chunk = new Chunk(String.valueOf(sdf.format(compliance.getRequestDate())), normal);
            else
                chunk = new Chunk("N/A", normal);
            third.add(chunk);
            thirdCell = new PdfPCell(third);
            thirdCell.setBorder(0);

            chunk = new Chunk("Start Date:  \n\t", normalBoldGray);
            fourth.add(chunk);
            if(compliance.getDateStarted()!=null){
                sdf=new SimpleDateFormat("dd/MM/yyyy");
            chunk = new Chunk(String.valueOf(sdf.format(compliance.getDateStarted())), normal);
                fourth.add(chunk);
            }
            else{
                chunk = new Chunk("N/A", normal);
                fourth.add(chunk);
            }


            fourthCell = new PdfPCell(fourth);
            fourthCell.setBorder(0);

            fifth = new Paragraph();
            chunk = new Chunk("Type:  \n\t", normalBoldGray);
            fifth.add(chunk);
            chunk = new Chunk(compliance.getType(), normal);
            fifth.add(chunk);
            fifthCell = new PdfPCell(fifth);
            fifthCell.setBorder(0);
            //fifthCell.setPaddingLeft(10);
            //-- set col span of cell to 2
            //fifthCell.setColspan(2);

            chunk = new Chunk("Due Date:  \n\t", normalBoldGray);
            sixth.add(chunk);
            sdf=new SimpleDateFormat("dd/MM/yyyy");
            if(compliance.getDueDate()!=null){
                chunk = new Chunk(String.valueOf(sdf.format(compliance.getDueDate())), normal);
                sixth.add(chunk);
            }
            else{
                chunk = new Chunk("N/A", normal);
                sixth.add(chunk);
            }


            sixthCell = new PdfPCell(sixth);
            sixthCell.setBorder(0);

            chunk = new Chunk("Completion Date:  \n\t", normalBoldGray);
            seventh.add(chunk);
            sdf=new SimpleDateFormat("dd/MM/yyyy");
            if(compliance.getDateOfCompletion()!=null){
                chunk = new Chunk(String.valueOf(sdf.format(compliance.getDateOfCompletion())), normal);
                seventh.add(chunk);
            }
            else{
                chunk = new Chunk("N/A", normal);
                seventh.add(chunk);
            }


            seventhCell = new PdfPCell(seventh);
            seventhCell.setBorder(0);


            //set border left color
            firstCell.setUseVariableBorders(true);
            firstCell.setBorderWidthLeft(1);
            firstCell.setBorderColorLeft(lightBlue);
            table.addCell(firstCell);
            table.addCell(secondCell);

            //-- fifth is added before third as all dates must be together
            table.addCell(fifthCell);
            table.addCell(thirdCell);
            table.addCell(fourthCell);

            table.addCell(sixthCell);
            table.addCell(seventhCell);
            //sixthCell.setBorder(0);
            //table.addCell(sixthCell);
        }
        return table;
    }


    public Iterable<ComplianceRequest> getAllComplianceRequests(){
        return complianceRequestRepository.findAllByOrderById();
    }

    public Page<ComplianceRequest> getAllComplianceRequests(int offset, int limit){
        return complianceRequestRepository.findAll(new PageRequest(offset,limit));
    }

    public Page<ComplianceRequest> getAllComplianceRequestsPending(int offset, int limit){
        return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_pending,new PageRequest(offset,limit));
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsWithFilter(ComplianceFilter complianceFilter,int offset, int limit){

        if(complianceFilter.getOrganizationName()==null && complianceFilter.getEndDate()==null && complianceFilter.getStartDate()==null && complianceFilter.getStatus()!=null){

            if (complianceFilter.getStatus().equalsIgnoreCase("all") ) {
                return complianceRequestRepository.findAllByOrderById(new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_pending,new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("progress")){
                return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_progress,new PageRequest(offset,limit));
            }
            else
                return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_complete,new PageRequest(offset,limit));
        }

        if(complianceFilter.getOrganizationName()==null && complianceFilter.getEndDate()==null && complianceFilter.getStartDate()==null){
            return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_pending,new PageRequest(offset,limit));

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
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),"0",new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("progress")){
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_request_status_progress,new PageRequest(offset,limit));
                return x;
            }
            else{
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_request_status_complete,new PageRequest(offset,limit));
                return x;
            }

        }
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsByShipmentNumber(String shipmentNumber){
        return complianceRequestRepository.findAllByShipmentNumber(shipmentNumber);
    }

    public Iterable<Compliance> getAllCompliancesWithFilter(ComplianceFilter complianceFilter,int offset, int limit){

        if(complianceFilter.getOrganizationName()==null && complianceFilter.getEndDate()==null && complianceFilter.getStartDate()==null){

            if (complianceFilter.getStatus().equalsIgnoreCase("all") ) {
                return complianceRepository.findAllByOrderByIdDesc(new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_pending,new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("progress")){
                return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_progress,new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("unassigned")){
                return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_unassigned,new PageRequest(offset,limit));
            }
            else
                return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_complete,new PageRequest(offset,limit));
        }

        if(complianceFilter.getOrganizationName()==null && complianceFilter.getEndDate()==null && complianceFilter.getStartDate()==null){
        }

        if(complianceFilter.getOrganizationName()==null ) {
            if (complianceFilter.getStatus().equalsIgnoreCase("all") ) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),compliance_status_pending,new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("progress")){
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),compliance_status_progress,new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("unassigned")){
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),compliance_status_unassigned,new PageRequest(offset,limit));
            }
            else
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),compliance_status_complete,new PageRequest(offset,limit));
        }
        else{
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameOrderByIdDesc(complianceFilter.getOrganizationName(),new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_status_pending,new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("progress")){
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_status_progress,new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("unassigned")){
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_status_unassigned,new PageRequest(offset,limit));
                return x;
            }
            else{
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_status_complete,new PageRequest(offset,limit));
                return x;
            }

        }
    }

    public Page<Compliance> getAllCompliances(int offset, int limit){
        return complianceRepository.findAll(new PageRequest(offset,limit));
    }

    public Page<Compliance> getAllCompliancesPending(int offset, int limit){
        return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_unassigned ,new PageRequest(offset,limit));
    }

    public ComplianceRequestDocument getCompliaceRequestDocument(String requestNumber){
        ComplianceRequest complianceRequest = complianceRequestRepository.findComplianceRequestByRequestNumber(requestNumber);


            ComplianceRequestDocument complianceRequestDocument = new ComplianceRequestDocument();
            //complianceRequestDocument.setContent(generateDocumentRequestOrder(complianceRequest));
            if(complianceRequest!=null){
                complianceRequestDocument.setContent(complianceRequest.getContent());
                return complianceRequestDocument;
            }
            else
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


    public Compliance updateCompliance(Compliance compliance){

        Compliance dbCompliance=complianceRepository.findComplianceByComplianceNumber(compliance.getComplianceNumber());

        if(dbCompliance!=null) {
            //compliance.setComplianceRequest(dbCompliance.getComplianceRequest());
            //--- loop through each compliance so that

    String v1=compliance.getStatus();
    String v2 = dbCompliance.getStatus();

            //--- set the started date if the status is changed to in progress
            if(compliance_status_unassigned.equals(v1) && compliance.getUser()!=null && compliance_status_unassigned.equals(v2)) {
                compliance.setStatus(compliance_status_pending);
            }

            System.out.println(compliance_status_progress.indexOf(dbCompliance.getStatus()));
            if( compliance_status_progress.equals(v1) && compliance_status_progress.equals(v2)==false) {
                dbCompliance.setDateStarted(new Date());
            }

            if( compliance_status_complete.equals(v1) && compliance_status_complete.equals(v2)==false) {
                compliance.setDateOfCompletion(new Date());
            }
            dbCompliance.copyComplianceValues(compliance);
            try {
                //-- save contact first
                contactRepository.save(dbCompliance.getIssuingAuthority());
                contactRepository.save(dbCompliance.getUser());


                complianceRepository.save(dbCompliance);
                //send compliance to search-service -Ammar
                kafkaAsynService.sendCompliance(dbCompliance.getComplianceRequest());
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

    String getComplianceRequestStatusLabel(String status){
        if(compliance_request_status_pending.equalsIgnoreCase( status))
            return "Pending";
        else if(compliance_request_status_pending.equalsIgnoreCase( status))
            return "In Progress";
        else if(compliance_request_status_pending.equalsIgnoreCase( status))
            return "Completed";
        else
            return "N/A";
    }

    String getComplianceStatusLabel(String status){
        if(compliance_status_pending.equals( status))
            return "Pending";
        else if(compliance_status_pending.equals( status))
            return "In Progress";
        else if(compliance_status_complete.equals( status))
            return "Completed";
        else if(compliance_status_unassigned.equals( status))
            return "Unassigned";
        else
            return "N/A";
    }


    //QAF-1.0.3  #111    - SB
    //---class created to add footer
    public class FooterTable extends PdfPageEventHelper {
        protected PdfPTable footer;
        public FooterTable(PdfPTable footer) {
            this.footer = footer;
        }
        public void onEndPage(PdfWriter writer, Document document) {
            footer.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());
        }
    }


    PdfPTable getFooterPdf(){
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(0);
        table.setTotalWidth(570F);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[] { 2, 1 });
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        Paragraph first = new Paragraph("Al Sharqi Shipping Co. LLC",normalBold);
        Paragraph second;// = new Paragraph("Shipment Number: QAF-18001015",normal);
        PdfPCell leftCell = new PdfPCell(first);

        Chunk chunk ;
        second = new Paragraph();
        //todo: to be removed
        PdfPCell rightCell = new PdfPCell(second);
        //leftCell.setPaddingLeft(10);
        leftCell.setBorder(0);
        //set border left color
        leftCell.setUseVariableBorders(true);
        //leftCell.setBorderWidthLeft(1);
       // leftCell.setBorderColorLeft(lightBlue);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        //rightCell.setBorderWidthLeft(1);
        //rightCell.setBorderColorLeft(lightBlue);
        //rightCell.setPaddingLeft(10);
        table.addCell(leftCell);
        table.addCell(rightCell);

        chunk = new Chunk("Address:",normalBoldGray);
        first = new Paragraph();
        first.add(chunk);

        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);


        chunk = new Chunk("Phone: ",normalBoldGray);
        second = new Paragraph();
        second.add(chunk);
        //TODO: to be revied as what date is needed to be added

        chunk = new Chunk("1111-111-5599",normal);
        second.add(chunk);
        rightCell = new PdfPCell(second);
        leftCell.setBorder(0);

        //leftCell.setBorderWidthLeft(1);
        //leftCell.setBorderColorLeft(lightBlue);
        rightCell.setBorder(0);
        //rightCell.setPaddingBottom(0);
//        rightCell.setPaddingLeft(10);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        //rightCell.setBorderWidthLeft(1);
        //rightCell.setBorderColorLeft(lightBlue);
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
        chunk = new Chunk("Email: ",normalBoldGray);
        second = new Paragraph();
        //todo: to be removed
        second.add(chunk);
        chunk = new Chunk("ae.finance@alsharqi.co",normal);
        second.add(chunk);
        rightCell = new PdfPCell(second);
        leftCell.setBorder(0);
        //set border left color
        //leftCell.setPaddingLeft(10);
        leftCell.setPaddingTop(0);;
        leftCell.setPaddingBottom(0L);
        //leftCell.setBorderWidthLeft(1);
        //leftCell.setBorderColorLeft(lightBlue);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        //rightCell.setBorderWidthLeft(1);
        //rightCell.setBorderColorLeft(lightBlue);

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
        //leftCell.setPaddingLeft(10);
        leftCell.setPaddingTop(0);;
        leftCell.setPaddingBottom(0L);
        //leftCell.setBorderWidthLeft(1);
        //leftCell.setBorderColorLeft(lightBlue);


        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        //rightCell.setBorderWidthLeft(1);
        //rightCell.setBorderColorLeft(lightBlue);
        rightCell.setPaddingBottom(0);
        rightCell.setPaddingTop(0);
        table.addCell(leftCell);
        table.addCell(rightCell);

        //chunk = new Chunk("Phone: ",normalBoldGray);
        first = new Paragraph();
        //first.add(chunk);
        //chunk = new Chunk("121212 ",normal);
        // first.add(chunk);
        second = new Paragraph(" ",mini);
        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        leftCell.setBorder(0);
        //set border left color
        leftCell.setUseVariableBorders(true);
        //leftCell.setBorderWidthLeft(1);
        //leftCell.setBorderColorLeft(lightBlue);
        leftCell.setPaddingTop(0);;
        leftCell.setPaddingBottom(0L);
        //leftCell.setPaddingLeft(10);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        //rightCell.setBorderWidthLeft(1);
        //rightCell.setBorderColorLeft(lightBlue);
        rightCell.setPaddingBottom(0);
        rightCell.setPaddingTop(0);
        table.addCell(leftCell);
        table.addCell(rightCell);


        /*Email*/
        //chunk = new Chunk("Email: ",normalBoldGray);
        first = new Paragraph();
        //first.add(chunk);
        //chunk = new Chunk("ae.finance@alsharqi.co ",normal);

        //first.add(chunk);
        second = new Paragraph(" ",mini);
        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        leftCell.setBorder(0);
        //set border left color
        leftCell.setUseVariableBorders(true);
        //leftCell.setBorderWidthLeft(1);
        //leftCell.setBorderColorLeft(lightBlue);
        rightCell.setBorder(0);
        //set border left color
        rightCell.setUseVariableBorders(true);
        //rightCell.setBorderWidthLeft(1);
        //rightCell.setBorderColorLeft(lightBlue);
        table.addCell(leftCell);
        table.addCell(rightCell);

        return table;
    }

    //Task 432 Re-index Compliance Request -Ammar
    public DefaultResponse indexComplianceRequests(){
        Authentication principal = SecurityContextHolder.getContext().getAuthentication();
        if(principal.getAuthorities().toString().contains("ROLE_ADMIN")){
            Iterable<ComplianceRequest> complianceRequests=complianceRequestRepository.findAll();
            for(ComplianceRequest complianceRequest:complianceRequests){
                kafkaAsynService.sendCompliance(complianceRequest);
            }
            return new DefaultResponse("N/A", "Compliance Requests sent to search-service successfully.", "F001");
        }
        return new DefaultResponse("N/A", "You need admin authentication to do this operation.", "F001");
    }

    public Iterable<Compliance> getAllCompliancesWithFilterBySquad(ListOrganization listOrganization, int offset, int limit){

        List<String> stringList = new ArrayList<>();

        for (Iterator<OrganizationIdCLass> organizationIdCLassIterator = listOrganization.getOrganizationIdCLasses().iterator();

             organizationIdCLassIterator.hasNext();){

            OrganizationIdCLass organizationIdCLass = organizationIdCLassIterator.next();

            stringList.add(organizationIdCLass.getOrganizationid());

        }


        ComplianceFilter complianceFilter = listOrganization.getFilterObject();

        if(complianceFilter.getOrganizationName()==null && complianceFilter.getEndDate()==null && complianceFilter.getStartDate()==null){
            return complianceRepository.findAllByComplianceRequest_OrganizationIdInOrderByIdDesc(stringList,new PageRequest(offset,limit));
        }

        if(complianceFilter.getOrganizationName()==null ) {
            if (complianceFilter.getStatus().equalsIgnoreCase("all") ) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),stringList,new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),compliance_status_pending,stringList,new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("progress")){
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),compliance_status_progress,stringList,new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("unassigned")){
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),compliance_status_unassigned,stringList,new PageRequest(offset,limit));
            }
            else
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),compliance_status_complete,stringList,new PageRequest(offset,limit));
        }
        else{
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameOrderByIdDesc(complianceFilter.getOrganizationName(),new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_status_pending,new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("progress")){
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_status_progress,new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("unassigned")){
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_status_unassigned,new PageRequest(offset,limit));
                return x;
            }
            else{
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_status_complete,new PageRequest(offset,limit));
                return x;
            }

        }
    }

    public Page<ComplianceRequest> getAllComplianceRequestsPendingBySquad(int offset, int limit,ListOrganization listOrganization){

        List<String> stringList = new ArrayList<>();

        for (Iterator<OrganizationIdCLass> organizationIdCLassIterator = listOrganization.getOrganizationIdCLasses().iterator();

             organizationIdCLassIterator.hasNext();){

            OrganizationIdCLass organizationIdCLass = organizationIdCLassIterator.next();

            stringList.add(organizationIdCLass.getOrganizationid());

        }
        return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByIdDesc(compliance_request_status_pending,stringList,new PageRequest(offset,limit));
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsWithFilterAndSquad(ListOrganization listOrganization,int offset, int limit){

        List<String> stringList = new ArrayList<>();

        for (Iterator<OrganizationIdCLass> organizationIdCLassIterator = listOrganization.getOrganizationIdCLasses().iterator();

             organizationIdCLassIterator.hasNext();){

            OrganizationIdCLass organizationIdCLass = organizationIdCLassIterator.next();

            stringList.add(organizationIdCLass.getOrganizationid());

        }

        ComplianceFilter complianceFilter = listOrganization.getFilterObject();
        if(complianceFilter.getOrganizationName()==null && complianceFilter.getEndDate()==null && complianceFilter.getStartDate()==null){
            return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByIdDesc(compliance_request_status_pending,stringList,new PageRequest(offset,limit));
        }

        if(complianceFilter.getOrganizationName()==null ) {
            if (complianceFilter.getStatus().equalsIgnoreCase("all") ) {
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndOrganizationIdInOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),stringList,new PageRequest(offset,limit));
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),"0",stringList,new PageRequest(offset,limit));
            }
            else
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(),"1",stringList,new PageRequest(offset,limit));
        }
        else{
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndOrganizationIdInOrderByIdDesc(complianceFilter.getOrganizationName(),stringList,new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("pending")){
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getOrganizationName(),"0",stringList,new PageRequest(offset,limit));
                return x;
            }
            else if(complianceFilter.getStatus().equalsIgnoreCase("progress")){
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_request_status_progress,stringList,new PageRequest(offset,limit));
                return x;
            }
            else{
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getOrganizationName(),compliance_request_status_complete,stringList,new PageRequest(offset,limit));
                return x;
            }

        }
    }

    /*
    * Q418-1.2 #757
    * implement delete conpliance functionality
    *
    * */
    @Transactional
    public DefaultResponse deleteComplianceByComplianceNumber(String complianceNumber){
        try{

            complianceRepository.deleteComplianceByComplianceNumber(complianceNumber);
            return new DefaultResponse(complianceNumber,"Deleted Successfully","D001");
        }catch(Exception e){
            return new DefaultResponse(complianceNumber,"Could not delete"+e.getMessage(),"D001");
        }
    }

    public Set<Compliance>  addComplianceSet(Set<Compliance> complianceSet){
        try{
            complianceRepository.save(complianceSet);

            Iterator<Compliance> complianceIterator = complianceSet.iterator();
            while (complianceIterator.hasNext()) {

                Compliance compliance = complianceIterator.next();
                if(compliance.getComplianceNumber()==null){
                    compliance.setComplianceNumber(getComplianceNumber(compliance.getId()));
                }

            }
            complianceRepository.save(complianceSet);

            return complianceSet;
        }catch(Exception e){
            return null;
        }
    }

    /*
     * Q418-1.2 #757
     * implement delete conpliance functionality
     * For tets library purpose
     *
     * This function first creates a list of compliance numbers under the given compliance request and then deletes the compliances
     * then it deletes the compliance request
     * */
    @Transactional
    public DefaultResponse deleteComplianceRequestByComplianceRequestNumber(String complianceRequestNumber){
        try{

            ComplianceRequest complianceRequest = complianceRequestRepository.findComplianceRequestByRequestNumber(complianceRequestNumber);

            if(complianceRequest!=null && complianceRequest.getCompliances().size()>0) {
                //- create list of complaince numbers and call function to delete them
                List<String> complainceNumbers = new ArrayList<String>();
                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while (complianceIterator.hasNext()) {
                    complainceNumbers.add(complianceIterator.next().getComplianceNumber());
                }

                deleteMultipleComplainceByComplianceNumbers(complainceNumbers);
            }

            complianceRequestRepository.deleteComplianceRequestByRequestNumber(complianceRequestNumber);
            return new DefaultResponse(complianceRequestNumber,"Deleted Successfully","D001");
        }catch(Exception e){
            return new DefaultResponse(complianceRequestNumber,"Could not delete"+e.getMessage(),"D001");
        }
    }


    /*
    * Deletes list of compliances by using list of compliance numbers
    * */
    @Transactional
    public DefaultResponse deleteMultipleComplainceByComplianceNumbers(List<String> complainceNumbers){
        try{
            complianceRepository.deleteAllByComplianceNumbers(complainceNumbers);
            return new DefaultResponse(complainceNumbers.get(0),"Deleted Successfully","D001");
        }catch(Exception e){
            return new DefaultResponse(complainceNumbers.get(0),"Could not delete"+e.getMessage(),"D001");
        }
    }
}

