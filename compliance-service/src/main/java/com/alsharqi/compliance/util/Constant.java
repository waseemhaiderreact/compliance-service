package com.alsharqi.compliance.util;

public class Constant {

    public static String CONTENT_TYPE_PDF = "data:application/pdf;base64,";
    public static String CONTENT_TYPE_PNG = "data:image/png;base64,";
    public static String CONTENT_TYPE_JPG = "data:image/jpeg;base64,";
    public static String CONTENT_TYPE_JPEG = "data:image/jpeg;base64,";
    public static String FILE_TYPE_PNG = "png";
    public static String FILE_TYPE_PDF = "pdf";
    public static String FILE_TYPE_JPG = "jpg";
    public static String FILE_TYPE_JPEG = "jpeg";


    public static String EMPTY_STRING = "";


    public static String CUSTOMS_KEYWORD = "Customs";
    public static String IMPORT_KEYWORD = "Import";
    public static String EXPORT_KEYWORD = "Export";

    public static int SHIPMENT_STATUS_CUSTOMS_ORIGIN_CLEARED = 23;
    public static int SHIPMENT_STATUS_CUSTOMS_DESTINATION_CLEARED = 50;

    public static String COMPLIANCE_REQUEST_STATUS_PENDING = "0";
    public static String COMPLIANCE_REQUEST_STATUS_IN_PROGRESS = "1";
    public static String COMPLIANCE_REQUEST_STATUS_COMPLETED = "2";

    public static String QAFILA = "QAF";
    public static String ALSHARQI = "ALS";

    public static String SHIPMENT_MILESTONE_EXPORT_CUSTOMS_CLEARED_RECEIVED_TYPE = "23.1";
    public static String SHIPMENT_MILESTONE_EXPORT_CUSTOMS_CLEARED_RECEIVED_DESCRIPTION = "Export Customs Cleared Received";
    public static String SHIPMENT_MILESTONE_IMPORT_CUSTOMS_CLEARED_RECEIVED_TYPE = "50.1";
    public static String SHIPMENT_MILESTONE_IMPORT_CUSTOMS_CLEARED_RECEIVED_DESCRIPTION = "Import Customs Cleared Received";
    /*
     * ACTION VALUE FOR WHEN CREATE A NEW SHIPMENT SUMMARY LIST IS REQUIRED. ALSO
     * */
    public static final String  SHIPMENT_SUMMARY_LIST_ACTION_CREATE = "CREATE";

    public static String VGM_KEYWORD = "VGM";
    public static String SUBMITTED_KEYWORD = "submitted";

    public static String SHIPMENT_MILESTONE_VGM_SUBMITTED_TYPE = "20.3";
    public static String SHIPMENT_MILESTONE_VGM_SUBMITTED_DESCRIPTION = "VGM Submitted";
}
