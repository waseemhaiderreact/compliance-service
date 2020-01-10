package com.alsharqi.compliance.api;

import com.alsharqi.compliance.attachment.FileAttachments;
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

import com.alsharqi.compliance.events.shipmentevent.DestinationCustomsClearedBean;
import com.alsharqi.compliance.events.shipmentevent.OriginCustomsClearedEventBean;
import com.alsharqi.compliance.events.shipmentevent.ShipmentEventModel;
import com.alsharqi.compliance.events.shipmentsummary.SummaryListModel;
import com.alsharqi.compliance.events.shipmentsummary.SummaryListSourceBean;

import com.alsharqi.compliance.events.shipment.ShipmentModel;
import com.alsharqi.compliance.events.shipment.ShipmentSourceBean;
import com.alsharqi.compliance.events.shipment.ShipmentStatus;

import com.alsharqi.compliance.location.Location;
import com.alsharqi.compliance.notification.Notification;
import com.alsharqi.compliance.organizationidclass.ListOrganization;
import com.alsharqi.compliance.organizationidclass.OrganizationIdCLass;
import com.alsharqi.compliance.request.EditComplianceRecordRequest;
import com.alsharqi.compliance.response.ComplianceFileUploadResponse;
import com.alsharqi.compliance.response.DefaultResponse;
import com.alsharqi.compliance.util.Constant;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;


import static com.alsharqi.compliance.util.Constant.ALSHARQI;
import static com.alsharqi.compliance.util.Constant.QAFILA;
import org.apache.logging.log4j.Logger;

@Service
public class ComplianceService {

    private static final Logger LOGGER = LogManager.getLogger(ComplianceService.class);

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

    @Autowired
    private SummaryListSourceBean summaryListSourceBean;

    @Autowired
    private DestinationCustomsClearedBean destinationCustomsClearedBean;

    @Autowired
    private OriginCustomsClearedEventBean originCustomsClearedEventBean;


    @Autowired
    Environment environment;

    private final String compliance_request_status_pending = "0";
    private final String compliance_request_status_progress = "1";
    private final String compliance_request_status_complete = "2";
    private final String compliance_status_pending = "1";
    private final String compliance_status_progress = "2";
    private final String compliance_status_complete = "3";

    //For S3 Integration
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region}")
    private String region;

    @Value("${cloud.aws.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.custom.upload.file.url}")
    private String s3EnpointCustomUploadedFileUrl;

    @Value("${clound.aws.s3.compliance.folder}")
    private String complianceFolderName;

    private AmazonS3 s3Client;

    @Autowired
    private ShipmentSourceBean shipmentSourceBean;

    @PostConstruct
    private void initializeAmazon() {

        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            this.s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(region)
                    .build();

            if (!s3Client.doesBucketExist(bucketName)) {
                s3Client.createBucket(new CreateBucketRequest(bucketName));

                // Verify that the bucket was created by retrieving it and checking its location.
                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));

            }
        } catch (Exception e) {
            LOGGER.error("Amazon initialization / Bucket creation,detection issues ", e);
        }
    }

    //DFF-1086
    private final String compliance_status_unassigned = "0";
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
    Font miniBold = new Font(Font.FontFamily.HELVETICA, 4, Font.BOLD);
    Font miniGray = new Font(Font.FontFamily.HELVETICA, 4, Font.NORMAL, BaseColor.GRAY);
    Font small = new Font(Font.FontFamily.HELVETICA, 6);
    Font smallBold = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
    Font smallGray = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.GRAY);
    //smallGray.setColor(BaseColor.GRAY);
    Font smallBoldGray = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.GRAY);
    //smallBoldGray.setColor(BaseColor.GRAY);
    Font normal = new Font(Font.FontFamily.HELVETICA, 8);
    Font normalBold = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
    Font normalGray = new Font(Font.FontFamily.HELVETICA, 8);
    //normalGray.setColor(BaseColor.GRAY);
    Font normalBoldGray = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.GRAY);
    //normalBoldGray.setColor(BaseColor.GRAY);
    Font large = new Font(Font.FontFamily.HELVETICA, 8);
    Font largeBold = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
    Font largeBoldBlue = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, new BaseColor(36, 106, 180));
    Font heading1 = new Font(Font.FontFamily.HELVETICA, 12);
    Font heading1Bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(106, 206, 242));
    Font heading1BoldBlue = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(36, 106, 180));
    BaseColor lightGray = new BaseColor(211, 211, 211);
    BaseColor lightBlue = new BaseColor(106, 206, 242);

    @Transactional
    public ComplianceRequest addRequest(ComplianceRequest complianceRequest) {
        String companyName;
        //--- loop through each compliance so that
        if (complianceRequest.getCompliances() != null && complianceRequest.getCompliances().size() > 0) {
            Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
            while (complianceIterator.hasNext()) {
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
            if (complianceRequest.getCompliances() != null && complianceRequest.getCompliances().size() > 0) {
                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while (complianceIterator.hasNext()) {
                    Compliance compliance = complianceIterator.next();
                    compliance.setComplianceNumber(getComplianceNumber(compliance.getId()));
                }
            }

            complianceRequestRepository.save(complianceRequest);

            //Send complianceRequest to Search Service-Ammar
            //kafkaAsynService.sendCompliance(complianceRequest);
            LOGGER.info("Here in Compliance Service and Shipment #"+complianceRequest.getShipmentNumber());
            companyName = getCompanyName(complianceRequest.getShipmentNumber());
            LOGGER.info("Company/Username: "+companyName);
            if (complianceRequest.getCompliances().size() > 0) {
                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while (complianceIterator.hasNext()) {
                    Compliance currentCompliance = complianceIterator.next();
                    Notification aNotification = new Notification();
                    aNotification.setMessage("Compliance Created: " + currentCompliance.getType());
                    aNotification.setUsername(companyName);
                    aNotification.setReadStatus(false);
                    aNotification.setType("auto");
                    aNotification.setShipmentNumber(complianceRequest.getShipmentNumber());
                    NotificationModel notification = new NotificationModel("CREATE", aNotification, "carrierBooked");
                    notificationSourceBean.publishNewNotification(notification);
                    LOGGER.info("Notification has published from Compliance Service");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //*******  Create Notification to send to the User about carrier booking  **********

        return complianceRequest;
    }

    String getComplianceRequestNumber(Long number) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String formattedDate = df.format(Calendar.getInstance().getTime());
        return "CR-" + formattedDate + String.format("%04d", number);
    }

    String getComplianceNumber(Long number) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String formattedDate = df.format(Calendar.getInstance().getTime());
        return "CL-" + formattedDate + String.format("%04d", number);
    }

    @Transactional
    public ComplianceRequest updateRequest(ComplianceRequest complianceRequest) {

        ComplianceRequest dbComplianceRequest = complianceRequestRepository.findComplianceRequestByRequestNumber(complianceRequest.getRequestNumber());

        String s3Key = dbComplianceRequest.getShipmentNumber() + "/" + complianceRequest.getRequestNumber();

        if (dbComplianceRequest != null) {

            Set<Compliance> complianceSet = new HashSet<Compliance>();


            //--- loop through each compliance so that
            if (complianceRequest.getCompliances() != null && complianceRequest.getCompliances().size() > 0) {

                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while (complianceIterator.hasNext()) {

                    Compliance compliance = complianceIterator.next();
                    Compliance newCompliance = new Compliance();
                    newCompliance.copyComplianceValues(compliance);
                    complianceSet.add(newCompliance);
                }
                //complianceRequest.setCompliances(complianceSet);
            }

            //-- check for status if complete, add date of completion
            String requestStatus = complianceRequest.getStatus();

            //-- if during updating the request, the status is updated to completed then it will be true
            Boolean updatedToCompletedFlag = false;
            if (compliance_request_status_complete.equals(requestStatus) && compliance_status_complete.equals(dbComplianceRequest.getStatus()) == false) {
                complianceRequest.setDateOfCompletion(new Date());
                try {

                    File doc = new File(complianceRequest.getRequestNumber());
                    try {
                        FileOutputStream fos = new FileOutputStream(doc);
                        fos.write(generateDocumentRequestOrder(complianceRequest));
                        fos.close();
                        complianceRequest.setS3Key(s3Key);
                    } catch (Exception e) {
                        LOGGER.error("Building File Content Error", e);
                    } finally {

                        try {

                            PutObjectRequest putRequest = new PutObjectRequest(bucketName, s3Key, doc);
                            List<Tag> tags = new ArrayList<Tag>();
                            tags.add(new Tag("Type", dbComplianceRequest.getType()));
                            tags.add(new Tag("CustomerId", dbComplianceRequest.getOrganizationId()));
                            tags.add(new Tag("Source", "Compliance"));
                            putRequest.setTagging(new ObjectTagging(tags));
                            s3Client.putObject(putRequest);
                        } catch (Exception e) {
                            LOGGER.error("S3 File Save Error", e);
                        } finally {
                            //Delete locally created document.
                            doc.delete();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                updatedToCompletedFlag = true;
            }


            try {

                if (complianceSet.size() > 0) {

                    complianceSet = addComplianceSet(complianceSet);
                    Iterator<Compliance> iterator = complianceSet.iterator();
                    while (iterator.hasNext())
                        iterator.next().setComplianceRequest(complianceRequest);
                }

                complianceRequest.setCompliances(complianceSet);
                complianceRequestRepository.save(complianceRequest);
                complianceRequestRepository.flush();

                //-- send kafka calls for status change of shipment iin case of completion of last compliance type
                if(updatedToCompletedFlag && complianceRequest.getType()!=null && complianceRequest.getType().contains(Constant.CUSTOMS_KEYWORD)){

                    java.util.List<String> statusList = new ArrayList<String>();
                    statusList.add(Constant.COMPLIANCE_REQUEST_STATUS_PENDING);
                    statusList.add(Constant.COMPLIANCE_REQUEST_STATUS_IN_PROGRESS);
                    if(complianceRequest.getType()!=null && complianceRequest.getType().contains(Constant.IMPORT_KEYWORD)){

                        int complianceRequestLeft = complianceRequestRepository.countAllByShipmentNumberAndTypeContainsAndTypeContainsAndStatusIn(complianceRequest.getShipmentNumber(),Constant.CUSTOMS_KEYWORD,Constant.IMPORT_KEYWORD,statusList);
                        if(complianceRequestLeft==0) {
                            updateShipmentStatus(complianceRequest.getShipmentNumber(), Constant.SHIPMENT_STATUS_CUSTOMS_DESTINATION_CLEARED);
                            sendShipmentSummaryEvent(complianceRequest.getShipmentNumber(),Constant.SHIPMENT_MILESTONE_IMPORT_CUSTOMS_CLEARED_RECEIVED_TYPE,Constant.SHIPMENT_MILESTONE_IMPORT_CUSTOMS_CLEARED_RECEIVED_DESCRIPTION);
                            this.sendDestinationCustomsClearedMessage(complianceRequest.getShipmentNumber(), Constant.SHIPMENT_MILESTONE_IMPORT_CUSTOMS_CLEARED_RECEIVED_TYPE);
                        }
                    }
                    else if(complianceRequest.getType()!=null && complianceRequest.getType().contains(Constant.EXPORT_KEYWORD)){

                        int complianceRequestLeft = complianceRequestRepository.countAllByShipmentNumberAndTypeContainsAndTypeContainsAndStatusIn(complianceRequest.getShipmentNumber(),Constant.CUSTOMS_KEYWORD,Constant.EXPORT_KEYWORD,statusList);
                        if(complianceRequestLeft==0) {
                            updateShipmentStatus(complianceRequest.getShipmentNumber(), Constant.SHIPMENT_STATUS_CUSTOMS_ORIGIN_CLEARED);
                            sendShipmentSummaryEvent(complianceRequest.getShipmentNumber(),Constant.SHIPMENT_MILESTONE_EXPORT_CUSTOMS_CLEARED_RECEIVED_TYPE,Constant.SHIPMENT_MILESTONE_EXPORT_CUSTOMS_CLEARED_RECEIVED_DESCRIPTION);
                            this.sendOriginCustomsClearedMessage(complianceRequest.getShipmentNumber(), Constant.SHIPMENT_MILESTONE_EXPORT_CUSTOMS_CLEARED_RECEIVED_TYPE);
                        }
                    }
                    else if(complianceRequest.getType()!=null && complianceRequest.getType().contains(Constant.VGM_KEYWORD)){

                        int complianceRequestLeft = complianceRequestRepository.countAllByShipmentNumberAndTypeContainsAndTypeContainsAndStatusIn(complianceRequest.getShipmentNumber(),Constant.VGM_KEYWORD,Constant.SUBMITTED_KEYWORD,statusList);

                        if(complianceRequestLeft==0) {
                            sendShipmentSummaryEvent(complianceRequest.getShipmentNumber(),Constant.SHIPMENT_MILESTONE_VGM_SUBMITTED_TYPE,Constant.SHIPMENT_MILESTONE_VGM_SUBMITTED_DESCRIPTION);
                        }
                    }
                }
                //send compliance request to search-service -Ammar
                kafkaAsynService.sendCompliance(complianceRequest);
            } catch (Exception e) {
                complianceRequest.setId(null);
                e.printStackTrace();
            }
        } else {
            complianceRequest.setId(null);
        }

        return (complianceRequest);
    }

    byte[] generateDocumentRequestOrder(ComplianceRequest complianceRequest) throws IOException {
        Document document = new Document(PageSize.A4);
        document.setMargins(46.0F, 36.0F, -6F, 36.0F);
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

            document.add(getDocumentSectionCompanyDetails(complianceRequest.getShipmentNumber(), complianceRequest.getOrganizationName(), complianceRequest.getHeadOffice()));
            document.add(addEmptyLineWithBorder(2));
            document.add(getDocumentSectionCompliancesHeading(complianceRequest));
            document.add(addEmptyLineWithBorder(2));
            document.add(getCompliancesData(complianceRequest));

            document.close();
        } catch (DocumentException ex) {
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

    PdfPTable getDocumentSectionCompanyDetails(String shipmentNumber, String organizationName, Location headOffice) {

        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{2, 1});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        Paragraph first = new Paragraph(organizationName, normalBold);
        Paragraph second;// = new Paragraph("Shipment Number: QAF-18001015",normal);
        PdfPCell leftCell = new PdfPCell(first);

        Chunk chunk = new Chunk("Shipment Number: ", normalBoldGray);
        second = new Paragraph();
        second.add(chunk);
        chunk = new Chunk(shipmentNumber, normal);
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

        chunk = new Chunk("Address:", normalBoldGray);
        first = new Paragraph();
        first.add(chunk);

        leftCell = new PdfPCell(first);
        leftCell.setPaddingLeft(10);


        chunk = new Chunk("Issue Date: ", normalBoldGray);
        second = new Paragraph();
        second.add(chunk);
        //TODO: to be revied as what date is needed to be added
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        chunk = new Chunk(formattedDate, normal);
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

        chunk = new Chunk(headOffice.getAddress1() + ", " + headOffice.getCity() + ",", normal);
        first = new Paragraph();
        first.add(chunk);
        second = new Paragraph(" ", mini);
        leftCell = new PdfPCell(first);
        leftCell.setPaddingTop(10);
        ;

        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        leftCell.setBorder(0);
        //set border left color
        leftCell.setPaddingLeft(10);
        leftCell.setPaddingTop(0);
        ;
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


        first = new Paragraph(headOffice.getCountry(), normal);
        second = new Paragraph(" ", mini);
        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        leftCell.setBorder(0);
        leftCell.setPaddingLeft(10);
        leftCell.setPaddingTop(0);
        ;
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

        chunk = new Chunk("Phone: ", normalBoldGray);
        first = new Paragraph();
        first.add(chunk);

        Contact headOfficeContact = headOffice.getContacts().iterator().next();

        chunk = new Chunk(headOfficeContact.getPhone(), normal);
        first.add(chunk);
        second = new Paragraph(" ", mini);
        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        leftCell.setBorder(0);
        //set border left color
        leftCell.setUseVariableBorders(true);
        leftCell.setBorderWidthLeft(1);
        leftCell.setBorderColorLeft(lightBlue);
        leftCell.setPaddingTop(0);
        ;
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
        chunk = new Chunk("Email: ", normalBoldGray);
        first = new Paragraph();
        first.add(chunk);
        chunk = new Chunk(headOfficeContact.getEmail(), normal);

        first.add(chunk);
        second = new Paragraph(" ", mini);
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

    PdfPTable getDocumentSectionCompliancesHeading(ComplianceRequest complianceRequest) {
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

    PdfPTable getCompliancesData(ComplianceRequest complianceRequest) {

        PdfPTable table = new PdfPTable(7);
        //table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);
        Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
        while (complianceIterator.hasNext()) {
            Compliance compliance = complianceIterator.next();
            PdfPCell firstCell;
            PdfPCell secondCell = new PdfPCell(new Paragraph());
            PdfPCell thirdCell = new PdfPCell(new Paragraph());
            PdfPCell fourthCell = new PdfPCell(new Paragraph());
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (compliance.getRequestDate() != null)
                chunk = new Chunk(String.valueOf(sdf.format(compliance.getRequestDate())), normal);
            else
                chunk = new Chunk("N/A", normal);
            third.add(chunk);
            thirdCell = new PdfPCell(third);
            thirdCell.setBorder(0);

            chunk = new Chunk("Start Date:  \n\t", normalBoldGray);
            fourth.add(chunk);
            if (compliance.getDateStarted() != null) {
                sdf = new SimpleDateFormat("dd/MM/yyyy");
                chunk = new Chunk(String.valueOf(sdf.format(compliance.getDateStarted())), normal);
                fourth.add(chunk);
            } else {
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
            sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (compliance.getDueDate() != null) {
                chunk = new Chunk(String.valueOf(sdf.format(compliance.getDueDate())), normal);
                sixth.add(chunk);
            } else {
                chunk = new Chunk("N/A", normal);
                sixth.add(chunk);
            }


            sixthCell = new PdfPCell(sixth);
            sixthCell.setBorder(0);

            chunk = new Chunk("Completion Date:  \n\t", normalBoldGray);
            seventh.add(chunk);
            sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (compliance.getDateOfCompletion() != null) {
                chunk = new Chunk(String.valueOf(sdf.format(compliance.getDateOfCompletion())), normal);
                seventh.add(chunk);
            } else {
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


    public Iterable<ComplianceRequest> getAllComplianceRequests() {
        return complianceRequestRepository.findAll();
    }

    public Page<ComplianceRequest> getAllComplianceRequests(int offset, int limit) {
        return complianceRequestRepository.findAll(new PageRequest(offset, limit));
    }

    public Page<ComplianceRequest> getAllComplianceRequestsPending(int offset, int limit, String sort, String orderBy) {
        if (orderBy.equalsIgnoreCase("customer")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByStatusOrderByOrganizationNameAsc(compliance_request_status_pending, new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByStatusOrderByOrganizationNameDesc(compliance_request_status_pending, new PageRequest(offset, limit));
        } else if (orderBy.equalsIgnoreCase("status")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByOrderByStatusAsc(new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByOrderByStatusDesc(new PageRequest(offset, limit));
        } else if (orderBy.equalsIgnoreCase("type")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByStatusOrderByTypeAsc(compliance_request_status_pending, new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByStatusOrderByTypeDesc(compliance_request_status_pending, new PageRequest(offset, limit));
        } else if (orderBy.equalsIgnoreCase("due-date")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByStatusOrderByDueDateAsc(compliance_request_status_pending, new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByStatusOrderByDueDateDesc(compliance_request_status_pending, new PageRequest(offset, limit));
        } else {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByStatusOrderByIdAsc(compliance_request_status_pending, new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_pending, new PageRequest(offset, limit));
        }
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsWithFilter(ComplianceFilter complianceFilter, int offset, int limit) {

        if (complianceFilter.getOrganizationName() == null && complianceFilter.getEndDate() == null && complianceFilter.getStartDate() == null && complianceFilter.getStatus() != null) {

            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                return complianceRequestRepository.findAllByOrderById(new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_pending, new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("progress")) {
                return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_progress, new PageRequest(offset, limit));
            } else
                return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_complete, new PageRequest(offset, limit));
        }

        if (complianceFilter.getOrganizationName() == null && complianceFilter.getEndDate() == null && complianceFilter.getStartDate() == null) {
            return complianceRequestRepository.findAllByStatusOrderByIdDesc(compliance_request_status_pending, new PageRequest(offset, limit));

        }

        if (complianceFilter.getOrganizationName() == null) {
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), "0", new PageRequest(offset, limit));
            } else
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), "1", new PageRequest(offset, limit));
        } else {
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameOrderByIdDesc(complianceFilter.getOrganizationName(), new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), "0", new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("progress")) {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_request_status_progress, new PageRequest(offset, limit));
                return x;
            } else {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_request_status_complete, new PageRequest(offset, limit));
                return x;
            }

        }
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsByShipmentNumber(String shipmentNumber) {
        return complianceRequestRepository.findAllByShipmentNumber(shipmentNumber);
    }

    public Iterable<Compliance> getAllCompliancesWithFilter(ComplianceFilter complianceFilter, int offset, int limit) {

        if (complianceFilter.getOrganizationName() == null && complianceFilter.getEndDate() == null && complianceFilter.getStartDate() == null) {

            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                return complianceRepository.findAllByOrderByIdDesc(new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_pending, new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("progress")) {
                return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_progress, new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("unassigned")) {
                return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_unassigned, new PageRequest(offset, limit));
            } else
                return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_complete, new PageRequest(offset, limit));
        }

        if (complianceFilter.getOrganizationName() == null && complianceFilter.getEndDate() == null && complianceFilter.getStartDate() == null) {
        }

        if (complianceFilter.getOrganizationName() == null) {
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), compliance_status_pending, new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("progress")) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), compliance_status_progress, new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("unassigned")) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), compliance_status_unassigned, new PageRequest(offset, limit));
            } else
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), compliance_status_complete, new PageRequest(offset, limit));
        } else {
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameOrderByIdDesc(complianceFilter.getOrganizationName(), new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_status_pending, new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("progress")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_status_progress, new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("unassigned")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_status_unassigned, new PageRequest(offset, limit));
                return x;
            } else {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_status_complete, new PageRequest(offset, limit));
                return x;
            }

        }
    }

    public Page<Compliance> getAllCompliances(int offset, int limit) {
        return complianceRepository.findAll(new PageRequest(offset, limit));
    }

    public Page<Compliance> getAllCompliancesPending(String sort, String sortBy, int offset, int limit) {

        if (sortBy.equalsIgnoreCase("type")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByStatusOrderByTypeAsc(compliance_status_unassigned, new PageRequest(offset, limit));
            else
                return complianceRepository.findAllByStatusOrderByTypeDesc(compliance_status_unassigned, new PageRequest(offset, limit));
        } else if (sortBy.equalsIgnoreCase("user")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByStatusOrderByUserFirstNameAsc(compliance_status_unassigned, new PageRequest(offset, limit));
            else
                return complianceRepository.findAllByStatusOrderByUserFirstNameDesc(compliance_status_unassigned, new PageRequest(offset, limit));
        } else if (sortBy.equalsIgnoreCase("customer")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByStatusOrderByComplianceRequestOrganizationNameAsc(compliance_status_unassigned, new PageRequest(offset, limit));
            else
                return complianceRepository.findAllByStatusOrderByComplianceRequestOrganizationNameDesc(compliance_status_unassigned, new PageRequest(offset, limit));
        } else if (sortBy.equalsIgnoreCase("issuing-authority")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByStatusOrderByIssuingAuthorityAuthorityAsc(compliance_status_unassigned, new PageRequest(offset, limit));
            else
                return complianceRepository.findAllByStatusOrderByIssuingAuthorityAuthorityDesc(compliance_status_unassigned, new PageRequest(offset, limit));
        } else if (sortBy.equalsIgnoreCase("due-date")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByStatusOrderByDueDateAsc(compliance_status_unassigned, new PageRequest(offset, limit));
            else
                return complianceRepository.findAllByStatusOrderByDueDateDesc(compliance_status_unassigned, new PageRequest(offset, limit));
        } else if (sortBy.equalsIgnoreCase("status")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByOrderByStatusAsc(new PageRequest(offset, limit));
            else
                return complianceRepository.findAllByOrderByStatusDesc(new PageRequest(offset, limit));
        } else {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByStatusOrderByIdAsc(compliance_status_unassigned, new PageRequest(offset, limit));
            else
                return complianceRepository.findAllByStatusOrderByIdDesc(compliance_status_unassigned, new PageRequest(offset, limit));
        }
    }

    public ComplianceRequestDocument getCompliaceRequestDocument(String requestNumber) {
        ComplianceRequest complianceRequest = complianceRequestRepository.findComplianceRequestByRequestNumber(requestNumber);

        ComplianceRequestDocument complianceRequestDocument = new ComplianceRequestDocument();
        //complianceRequestDocument.setContent(generateDocumentRequestOrder(complianceRequest));
        if (complianceRequest != null) {

            try {
                S3ObjectInputStream s3Str = s3Client.getObject(bucketName, complianceRequest.getS3Key()).getObjectContent();

                complianceRequestDocument.setContent(IOUtils.toByteArray(s3Str));
            } catch (Exception e) {
                LOGGER.error("Error Obtaining File Content", e);
            }/*finally{}*/


            return complianceRequestDocument;
        } else
            return null;
    }

    /*For now this table has 2 columns. When converted to generic , pdf table will also be passed, aong with column numbers*/
    private PdfPTable addEmptyLineWithBorder(int number) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 1});

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
    public Compliance updateCompliance(Compliance compliance) {
        String companyName;
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

//            System.out.println(compliance_status_progress.indexOf(dbCompliance.getStatus()));
            if( compliance_status_progress.equals(v1) && compliance_status_progress.equals(v2)==false) {
                dbCompliance.setDateStarted(new Date());
            }

            if( compliance_status_complete.equals(v1) && compliance_status_complete.equals(v2)==false) {
                compliance.setDateOfCompletion(new Date());

                LOGGER.info("Here in Compliance Service and Shipment#: "+dbCompliance.getComplianceRequest().getShipmentNumber());
                companyName = getCompanyName(dbCompliance.getComplianceRequest().getShipmentNumber());
                LOGGER.info("Company/Username: "+companyName);

                Notification aNotification = new Notification();
                aNotification.setMessage("Compliance Completed: "+ compliance.getType());
                aNotification.setUsername(companyName);
                aNotification.setReadStatus(false);
                aNotification.setType("auto");
                aNotification.setShipmentNumber(dbCompliance.getComplianceRequest().getShipmentNumber());
                NotificationModel notification = new NotificationModel("CREATE", aNotification, "carrierBooked");
                notificationSourceBean.publishNewNotification(notification);
                LOGGER.info("Updating Shipments in compliance service ");
            }
            List<Contact> contactList = new ArrayList<>();
            if(compliance.getIssuingAuthority()!=null)
                contactList.add(compliance.getIssuingAuthority());
            if(compliance.getUser()!=null)
                contactList.add(compliance.getUser());
            if(compliance.getVendor()!=null)
                contactList.add(compliance.getVendor());


            persistContacts(contactList);
            dbCompliance.copyComplianceValues(compliance);
            try {
                //-- save contact first


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


    //This function gets a contact list and if any if it has an id null, an id is generated
    @Transactional
    public void persistContacts(List<Contact> contacts){

        for(Contact eachContact:contacts){

            if(eachContact.getId()==null){
                eachContact.setId(generateLongNumber());
            }
        }

        contactRepository.save(contacts);
        contactRepository.flush();

    }

    public EditComplianceRecordRequest updateCompliance(EditComplianceRecordRequest compliance, List<MultipartFile> multipartFile) {

        System.out.println("tested");
        return compliance;
    }


    String getComplianceRequestStatusLabel(String status) {
        if (compliance_request_status_pending.equalsIgnoreCase(status))
            return "Pending";
        else if (compliance_request_status_pending.equalsIgnoreCase(status))
            return "In Progress";
        else if (compliance_request_status_pending.equalsIgnoreCase(status))
            return "Completed";
        else
            return "N/A";
    }

    String getComplianceStatusLabel(String status) {
        if (compliance_status_pending.equals(status))
            return "Pending";
        else if (compliance_status_pending.equals(status))
            return "In Progress";
        else if (compliance_status_complete.equals(status))
            return "Completed";
        else if (compliance_status_unassigned.equals(status))
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


    PdfPTable getFooterPdf() {
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(0);
        table.setTotalWidth(570F);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{2, 1});
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        Paragraph first = new Paragraph("Al Sharqi Shipping Co. LLC", normalBold);
        Paragraph second;// = new Paragraph("Shipment Number: QAF-18001015",normal);
        PdfPCell leftCell = new PdfPCell(first);

        Chunk chunk;
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

        chunk = new Chunk("Address:", normalBoldGray);
        first = new Paragraph();
        first.add(chunk);

        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);


        chunk = new Chunk("Phone: ", normalBoldGray);
        second = new Paragraph();
        second.add(chunk);
        //TODO: to be revied as what date is needed to be added

        chunk = new Chunk("1111-111-5599", normal);
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

        chunk = new Chunk("151 Khalid Bin Walid Road, Umm Hurrair 1, Dubai,", normal);
        first = new Paragraph();
        first.add(chunk);
        second = new Paragraph(" ", mini);
        leftCell = new PdfPCell(first);
        leftCell.setPaddingTop(10);
        ;

        //leftCell.setPaddingLeft(10);
        chunk = new Chunk("Email: ", normalBoldGray);
        second = new Paragraph();
        //todo: to be removed
        second.add(chunk);
        chunk = new Chunk("ae.finance@alsharqi.co", normal);
        second.add(chunk);
        rightCell = new PdfPCell(second);
        leftCell.setBorder(0);
        //set border left color
        //leftCell.setPaddingLeft(10);
        leftCell.setPaddingTop(0);
        ;
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


        first = new Paragraph("United Arab Emirates", normal);
        second = new Paragraph(" ", mini);
        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        leftCell.setBorder(0);
        //leftCell.setPaddingLeft(10);
        leftCell.setPaddingTop(0);
        ;
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
        second = new Paragraph(" ", mini);
        leftCell = new PdfPCell(first);
        //leftCell.setPaddingLeft(10);
        rightCell = new PdfPCell(second);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        leftCell.setBorder(0);
        //set border left color
        leftCell.setUseVariableBorders(true);
        //leftCell.setBorderWidthLeft(1);
        //leftCell.setBorderColorLeft(lightBlue);
        leftCell.setPaddingTop(0);
        ;
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
        second = new Paragraph(" ", mini);
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
    public DefaultResponse indexComplianceRequests() {
        Authentication principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal.getAuthorities().toString().contains("ROLE_ADMIN")) {
            Iterable<ComplianceRequest> complianceRequests = complianceRequestRepository.findAll();
            for (ComplianceRequest complianceRequest : complianceRequests) {
                kafkaAsynService.sendCompliance(complianceRequest);
            }
            return new DefaultResponse("N/A", "Compliance Requests sent to search-service successfully.", "F001");
        }
        return new DefaultResponse("N/A", "You need admin authentication to do this operation.", "F001");
    }

    public Iterable<Compliance> getAllCompliancesWithFilterBySquad(ListOrganization listOrganization, int offset, int limit) {

        List<String> stringList = new ArrayList<>();

        for (Iterator<OrganizationIdCLass> organizationIdCLassIterator = listOrganization.getOrganizationIdCLasses().iterator();

             organizationIdCLassIterator.hasNext(); ) {

            OrganizationIdCLass organizationIdCLass = organizationIdCLassIterator.next();

            stringList.add(organizationIdCLass.getOrganizationid());

        }


        ComplianceFilter complianceFilter = listOrganization.getFilterObject();

        if (complianceFilter.getOrganizationName() == null && complianceFilter.getEndDate() == null && complianceFilter.getStartDate() == null) {
            return complianceRepository.findAllByComplianceRequest_OrganizationIdInOrderByIdDesc(stringList, new PageRequest(offset, limit));
        }

        if (complianceFilter.getOrganizationName() == null) {
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), stringList, new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), compliance_status_pending, stringList, new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("progress")) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), compliance_status_progress, stringList, new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("unassigned")) {
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), compliance_status_unassigned, stringList, new PageRequest(offset, limit));
            } else
                return complianceRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndComplianceRequest_OrganizationIdOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), compliance_status_complete, stringList, new PageRequest(offset, limit));
        } else {
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameOrderByIdDesc(complianceFilter.getOrganizationName(), new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_status_pending, new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("progress")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_status_progress, new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("unassigned")) {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_status_unassigned, new PageRequest(offset, limit));
                return x;
            } else {
                Iterable<Compliance> x = complianceRepository.findAllByComplianceRequest_OrganizationNameAndStatusOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_status_complete, new PageRequest(offset, limit));
                return x;
            }

        }
    }

    public Page<ComplianceRequest> getAllComplianceRequestsPendingBySquad(String sortBy, String sort, int offset, int limit, ListOrganization listOrganization) {

        List<String> stringList = new ArrayList<>();

        for (Iterator<OrganizationIdCLass> organizationIdCLassIterator = listOrganization.getOrganizationIdCLasses().iterator(); organizationIdCLassIterator.hasNext(); ) {

            OrganizationIdCLass organizationIdCLass = organizationIdCLassIterator.next();

            stringList.add(organizationIdCLass.getOrganizationid());

        }


        if (sortBy.equalsIgnoreCase("type")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByTypeAsc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByTypeDesc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
        } else if (sortBy.equalsIgnoreCase("customer")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByOrganizationNameAsc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByOrganizationNameDesc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
        } else if (sortBy.equalsIgnoreCase("due-date")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByDueDateAsc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByDueDateDesc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
        } else if (sortBy.equalsIgnoreCase("status")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByStatusAsc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByStatusDesc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
        } else {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByIdAsc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
            else
                return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByIdDesc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
        }
    }

    public Iterable<ComplianceRequest> getAllComplianceRequestsWithFilterAndSquad(ListOrganization listOrganization, int offset, int limit) {

        List<String> stringList = new ArrayList<>();

        for (Iterator<OrganizationIdCLass> organizationIdCLassIterator = listOrganization.getOrganizationIdCLasses().iterator();

             organizationIdCLassIterator.hasNext(); ) {

            OrganizationIdCLass organizationIdCLass = organizationIdCLassIterator.next();

            stringList.add(organizationIdCLass.getOrganizationid());

        }

        ComplianceFilter complianceFilter = listOrganization.getFilterObject();
        if (complianceFilter.getOrganizationName() == null && complianceFilter.getEndDate() == null && complianceFilter.getStartDate() == null) {
            return complianceRequestRepository.findAllByStatusAndOrganizationIdInOrderByIdDesc(compliance_request_status_pending, stringList, new PageRequest(offset, limit));
        }

        if (complianceFilter.getOrganizationName() == null) {
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndOrganizationIdInOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), stringList, new PageRequest(offset, limit));
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), "0", stringList, new PageRequest(offset, limit));
            } else
                return complianceRequestRepository.findAllByDueDateAfterAndDueDateBeforeAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getStartDate(), complianceFilter.getEndDate(), "1", stringList, new PageRequest(offset, limit));
        } else {
            if (complianceFilter.getStatus().equalsIgnoreCase("all")) {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndOrganizationIdInOrderByIdDesc(complianceFilter.getOrganizationName(), stringList, new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("pending")) {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getOrganizationName(), "0", stringList, new PageRequest(offset, limit));
                return x;
            } else if (complianceFilter.getStatus().equalsIgnoreCase("progress")) {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_request_status_progress, stringList, new PageRequest(offset, limit));
                return x;
            } else {
                Iterable<ComplianceRequest> x = complianceRequestRepository.findAllByOrganizationNameAndStatusAndOrganizationIdInOrderByIdDesc(complianceFilter.getOrganizationName(), compliance_request_status_complete, stringList, new PageRequest(offset, limit));
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
    public DefaultResponse deleteComplianceByComplianceNumber(String complianceNumber) {
        try {

            complianceRepository.deleteComplianceByComplianceNumber(complianceNumber);
            return new DefaultResponse(complianceNumber, "Deleted Successfully", "D001");
        } catch (Exception e) {
            return new DefaultResponse(complianceNumber, "Could not delete" + e.getMessage(), "D001");
        }
    }

    public Set<Compliance> addComplianceSet(Set<Compliance> complianceSet) {
        try {
            complianceRepository.save(complianceSet);

            Iterator<Compliance> complianceIterator = complianceSet.iterator();
            while (complianceIterator.hasNext()) {

                Compliance compliance = complianceIterator.next();
                if (compliance.getComplianceNumber() == null) {
                    compliance.setComplianceNumber(getComplianceNumber(compliance.getId()));
                }

            }
            complianceRepository.save(complianceSet);

            return complianceSet;
        } catch (Exception e) {
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
    public DefaultResponse deleteComplianceRequestByComplianceRequestNumber(String complianceRequestNumber) {
        try {

            ComplianceRequest complianceRequest = complianceRequestRepository.findComplianceRequestByRequestNumber(complianceRequestNumber);

            if (complianceRequest != null && complianceRequest.getCompliances().size() > 0) {
                //- create list of complaince numbers and call function to delete them
                List<String> complainceNumbers = new ArrayList<String>();
                Iterator<Compliance> complianceIterator = complianceRequest.getCompliances().iterator();
                while (complianceIterator.hasNext()) {
                    complainceNumbers.add(complianceIterator.next().getComplianceNumber());
                }

                deleteMultipleComplainceByComplianceNumbers(complainceNumbers);
            }

            complianceRequestRepository.deleteComplianceRequestByRequestNumber(complianceRequestNumber);
            return new DefaultResponse(complianceRequestNumber, "Deleted Successfully", "D001");
        } catch (Exception e) {
            return new DefaultResponse(complianceRequestNumber, "Could not delete" + e.getMessage(), "D001");
        }
    }


    /*
     * Deletes list of compliances by using list of compliance numbers
     * */
    @Transactional
    public DefaultResponse deleteMultipleComplainceByComplianceNumbers(List<String> complainceNumbers) {
        try {
            complianceRepository.deleteAllByComplianceNumbers(complainceNumbers);
            return new DefaultResponse(complainceNumbers.get(0), "Deleted Successfully", "D001");
        } catch (Exception e) {
            return new DefaultResponse(complainceNumbers.get(0), "Could not delete" + e.getMessage(), "D001");
        }
    }

    public Iterable<ComplianceRequest> getConditionalComplianceRequests(String searchQuery, Integer offset, Integer limit, String sort, String orderBy) throws ParseException {
        if (searchQuery.contains(".")) {
            searchQuery = searchQuery.replace(".", "-");
            String[] split = searchQuery.split("-");
            if (split.length == 2) {
                searchQuery = split[1] + "-" + split[0];
            }
            if (split.length == 3) {
                searchQuery = split[2] + "-" + split[1] + "-" + split[0];

            }
        }

        if (searchQuery.equalsIgnoreCase("completed"))
            searchQuery = "2";

        if (searchQuery.equalsIgnoreCase("pending"))
            searchQuery = "0";

        if (searchQuery.equalsIgnoreCase("in progress"))
            searchQuery = "1";


        if (orderBy.equalsIgnoreCase("customer")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByOrganizationNameAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(offset, limit)
                );
            else
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByOrganizationNameDesc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(offset, limit)
                );
        } else if (orderBy.equalsIgnoreCase("type")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByTypeAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(offset, limit)
                );
            else
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByTypeDesc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(offset, limit)
                );
        } else if (orderBy.equalsIgnoreCase("due-date")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByDueDateAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(offset, limit)
                );
            else
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByDueDateDesc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(offset, limit)
                );
        } else {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIdAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(offset, limit)
                );
            else
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIdDesc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(offset, limit)
                );
        }
    }

    public Page<Compliance> getAllCompliancesByCondition(String searchQuery, String sort, String sortBy, Integer page, Integer limit) {
        if (searchQuery.contains(".")) {
            searchQuery = searchQuery.replace(".", "-");
            String[] split = searchQuery.split("-");
            if (split.length == 2) {
                searchQuery = split[1] + "-" + split[0];
            }
            if (split.length == 3) {
                searchQuery = split[2] + "-" + split[1] + "-" + split[0];

            }
        }

        if (searchQuery.equalsIgnoreCase("completed"))
            searchQuery = "3";

        if (searchQuery.equalsIgnoreCase("unassign"))
            searchQuery = "0";

        if (searchQuery.equalsIgnoreCase("pending"))
            searchQuery = "1";

        if (searchQuery.equalsIgnoreCase("in progress"))
            searchQuery = "2";

        if (sortBy.equalsIgnoreCase("compliance-request-number")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIdAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
            else
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIdDesc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
        } else if (sortBy.equalsIgnoreCase("user")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByUserFirstNameAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
            else
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByUserFirstNameDesc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
        } else if (sortBy.equalsIgnoreCase("issuing-authority")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIssuingAuthorityAuthorityAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
            else
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByIssuingAuthorityAuthorityDesc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
        } else if (sortBy.equalsIgnoreCase("customer")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByComplianceRequestOrganizationNameAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
            else
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByComplianceRequestOrganizationNameDesc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
        } else if (sortBy.equalsIgnoreCase("type")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByTypeAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
            else
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByTypeDesc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
        } else if (sortBy.equalsIgnoreCase("due-date")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByDueDateAsc(
                        searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
            else
                return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByDueDateDesc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
        } else if (sort.equalsIgnoreCase("asc"))
            return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByStatusAsc(
                    searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));
        else
            return complianceRepository.findAllByComplianceNumberContainingOrUserFirstNameContainingOrUserLastNameContainingOrIssuingAuthorityAuthorityContainingOrComplianceRequestOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseOrderByStatusDesc(
                    searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, new PageRequest(page, limit));

    }

    public Page<ComplianceRequest> getAllComplianceRequestsBySquad(String searchQuery, String sortBy, String sort, int page, int limit, ListOrganization listOrganization) {

        List<String> stringList = new ArrayList<>();

        for (Iterator<OrganizationIdCLass> organizationIdCLassIterator = listOrganization.getOrganizationIdCLasses().iterator(); organizationIdCLassIterator.hasNext(); ) {

            OrganizationIdCLass organizationIdCLass = organizationIdCLassIterator.next();

            stringList.add(organizationIdCLass.getOrganizationid());

        }

        if (searchQuery.contains(".")) {
            searchQuery = searchQuery.replace(".", "-");
            String[] split = searchQuery.split("-");
            if (split.length == 2) {
                searchQuery = split[1] + "-" + split[0];
            }
            if (split.length == 3) {
                searchQuery = split[2] + "-" + split[1] + "-" + split[0];

            }
        }

        if (searchQuery.equalsIgnoreCase("completed"))
            searchQuery = "2";

        if (searchQuery.equalsIgnoreCase("pending"))
            searchQuery = "0";

        if (searchQuery.equalsIgnoreCase("in progress"))
            searchQuery = "1";


        if (sortBy.equalsIgnoreCase("type")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByTypeAsc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
            else
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByTypeDesc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
        } else if (sortBy.equalsIgnoreCase("customer")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByOrganizationNameAsc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
            else
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByOrganizationNameDesc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
        } else if (sortBy.equalsIgnoreCase("due-date")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByDueDateAsc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
            else
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByDueDateDesc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
        } else if (sortBy.equalsIgnoreCase("status")) {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByStatusAsc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
            else
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByStatusDesc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
        } else {
            if (sort.equalsIgnoreCase("asc"))
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByIdAsc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
            else
                return complianceRequestRepository.findAllByRequestNumberContainingOrShipmentNumberContainingOrOrganizationNameContainingOrTypeContainingOrDueDateContainingOrStatusContainingAllIgnoreCaseAndOrganizationIdInOrderByIdDesc(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, stringList, new PageRequest(page, limit));
        }
    }

    public Set<ComplianceFileUploadResponse> uploadComplianceFileService(List<MultipartFile> multipartFileList, String complianceNumber) {

        Set<ComplianceFileUploadResponse> uploadedFiles = null;
        try {
            Compliance compliance = complianceRepository.findComplianceByComplianceNumber(complianceNumber);
            if (compliance != null) {
                uploadedFiles = uploadComplianceFile(multipartFileList);
                Set<FileAttachments> attachments = new HashSet<FileAttachments>();
                for (ComplianceFileUploadResponse eachResponse : uploadedFiles) {

                    FileAttachments attachment = new FileAttachments();
                    attachment.setCompliance(compliance);
                    attachment.copyValues(eachResponse);
                    attachments.add(attachment);
                }
                compliance.setAttachments(attachments);
            }
            complianceRepository.save(compliance);
        } catch (Exception e) {
            LOGGER.error("Error while storing file for compliance" + complianceNumber + e);
        }
        //--return newly added files
        return uploadedFiles;
    }


    /*
     * The uploads file list and for each response create attachment
     * */
    Set<ComplianceFileUploadResponse> uploadComplianceFile(List<MultipartFile> multipartFileList) {

        Set<ComplianceFileUploadResponse> fileResponses = new HashSet<ComplianceFileUploadResponse>();
        for (MultipartFile file : multipartFileList) {
            ComplianceFileUploadResponse response = uploadFile(file);
            if (response != null) {
                fileResponses.add(response);
            }
        }

        return fileResponses;
    }

    //this function uploads a file to s3 bucket and returns url
    public ComplianceFileUploadResponse uploadFile(MultipartFile file) {
        ComplianceFileUploadResponse response = new ComplianceFileUploadResponse();
        LOGGER.debug("inside service function of uploading file to s3");
        try {
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            String fileName = generateFileName(file.getOriginalFilename());
            String fileUrl = s3EnpointCustomUploadedFileUrl + "/" + complianceFolderName + "/" + fileName;
            s3Client.putObject(new PutObjectRequest(bucketName + "/" + complianceFolderName, fileName, convFile));
            response.setFileLink(fileUrl);
            response.setFileName(file.getOriginalFilename());
            LOGGER.info("File uploaded Successfully");
            convFile.delete();
            return response;
        } catch (Exception e) {
            LOGGER.error("Error while uploading file to s3", e);
//            e.printStackTrace();
            response.setFileIdentifier("Fail");
            return response;
        }
    }

    private String generateFileName(String filename) {
        return new Date().getTime() + "-" + filename;
    }


    public ComplianceFileUploadResponse getFile(String url) {
        LOGGER.debug("Inside service function of getting file from s3");
        ComplianceFileUploadResponse response = new ComplianceFileUploadResponse();
        try {
            String[] parts = url.split("/");
            String key = "";
            for (int i = 3; i < parts.length; i++) {
                key += parts[i];
                if (i != parts.length - 1) {
                    key += "/";
                }
            }
//            String key=parts[parts.length-1];
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            S3Object s3Object = s3Client.getObject(getObjectRequest);

            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

            byte[] bytes = IOUtils.toByteArray(objectInputStream);

            String fileName = URLEncoder.encode(key, "UTF-8").replaceAll("\\+", "%20");

            response.setContent(bytes);
            response.setContentLength(bytes.length);
            response.setFileName(fileName);
            response.setContentType(getFileContentType(fileName));
            response.setResponseIdentifier("Success");
            LOGGER.info("File got successfully. Returning to controller");
            return response;
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.error("Error while getting file from s3", e);
            response.setResponseIdentifier("Failure");
            return response;
        }
    }


    String getFileContentType(String extention) {

        String name = Constant.EMPTY_STRING;
        if (extention != null) {
            if (Constant.EMPTY_STRING.equals(extention) == false) {
                extention.contains(Constant.FILE_TYPE_PNG);
                return Constant.CONTENT_TYPE_PNG;
            }
            else if(Constant.EMPTY_STRING.equals(extention) == false) {
                extention.contains(Constant.FILE_TYPE_PDF);
                return Constant.CONTENT_TYPE_PDF;
            }

            else if(Constant.EMPTY_STRING.equals(extention) == false) {
                extention.contains(Constant.FILE_TYPE_JPG);
                return Constant.CONTENT_TYPE_JPG;
            }
            else if(Constant.EMPTY_STRING.equals(extention) == false) {
                extention.contains(Constant.FILE_TYPE_JPEG);
                return Constant.CONTENT_TYPE_JPEG;
            }
        }

        {
            return name;
        }
    }

    Long generateLongNumber(){
        return  Math.abs(new Random().nextLong());
    }


    public void updateShipmentStatus(String shipmentNumber,int status) {

        LOGGER.info("sending information to shipment service " + shipmentNumber);
        ShipmentStatus aShipment = new ShipmentStatus();
        aShipment.setShipmentNumber(shipmentNumber);
        aShipment.setStatus(status);
        ShipmentModel shipment = new ShipmentModel("UPDATE", aShipment);
        shipmentSourceBean.updateShipment(shipment);
        LOGGER.info("sent information to shipment service " + shipmentNumber);
    }


    private String getCompanyName(String shipmentNumber) {
        String companyName ="";
        if(shipmentNumber.startsWith(ALSHARQI))
        {
            companyName = environment.getProperty(ALSHARQI+".company.name");
        }
        else if(shipmentNumber.startsWith(QAFILA))
        {
            companyName = environment.getProperty(QAFILA+".company.name");
        }
        return companyName;
    }
    //TODO: update after adding constants

    public void sendShipmentSummaryEvent(String shipmentNumber,String summaryListType,String summaryListDescription){

        try {
            SummaryListModel summaryListModel = new SummaryListModel();
            summaryListModel.setShipmentNumber(shipmentNumber);
            summaryListModel.setDate(new Date());
            summaryListModel.setEventAction("");
            summaryListModel.setType(summaryListType);
            summaryListModel.setDescription(summaryListDescription);
//
            summaryListModel.setEventAction(Constant.SHIPMENT_SUMMARY_LIST_ACTION_CREATE);
            summaryListSourceBean.sendShipmentSummaryKafkaEvent(summaryListModel);
        }catch (Exception e){
            LOGGER.error("Error while sending milesone. " + e);
        }
    }

    private void sendOriginCustomsClearedMessage(String shipmentNumber, String eventCode){


        try {
            ShipmentEventModel model = new ShipmentEventModel();
            model.setShipmentNumber(shipmentNumber);
            model.setEventCode(eventCode);
            model.setAction("CREATE");
            model.setNotificationType("auto");
            originCustomsClearedEventBean.sendNotificationToAudit(model);
        } catch (Exception e) {
            LOGGER.error("Failed to send notification to audit service", e);
        }
    }

    private void sendDestinationCustomsClearedMessage(String shipmentNumber, String eventCode){
        try {
            ShipmentEventModel model = new ShipmentEventModel();
            model.setShipmentNumber(shipmentNumber);
            model.setEventCode(eventCode);
            model.setAction("CREATE");
            model.setNotificationType("auto");
            destinationCustomsClearedBean.sendNotificationToAudit(model);
        } catch (Exception e) {
            LOGGER.error("Failed to send notification to audit service", e);
        }
    }


}