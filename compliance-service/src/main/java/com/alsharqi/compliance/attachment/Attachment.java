package com.alsharqi.compliance.attachment;

import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.response.ComplianceFileUploadResponse;

import javax.persistence.*;

//
//@Entity
//@Table(name="t_attachment")
public class Attachment {

//    @Id
//    @GeneratedValue
    private Long id;

    private String fileName;
//    private String fileType;
//    private String fileURL;
//    private String storageMethod;
//    private String fileExt;
//    private String fileSize;
//    private String contentType;
//    @JsonIgnore
//    private byte[] content;




    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

//    public String getFileType() {
//        return fileType;
//    }
//
//    public void setFileType(String fileType) {
//        this.fileType = fileType;
//    }
//
//    public String getFileURL() {
//        return fileURL;
//    }
//
//    public void setFileURL(String fileURL) {
//        this.fileURL = fileURL;
//    }
//
//    public String getStorageMethod() {
//        return storageMethod;
//    }
//
//    public void setStorageMethod(String storageMethod) {
//        this.storageMethod = storageMethod;
//    }
//
//    public String getFileExt() {
//        return fileExt;
//    }
//
//    public void setFileExt(String fileExt) {
//        this.fileExt = fileExt;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getFileSize() {
//        return fileSize;
//    }
//
//    public void setFileSize(String fileSize) {
//        this.fileSize = fileSize;
//    }
//
//    public String getContentType() {
//        return contentType;
//    }
//
//    public void setContentType(String contentType) {
//        this.contentType = contentType;
//    }
//
//    public byte[] getContent() {
//        return content;
//    }
//
//    public void setContent(byte[] content) {
//        this.content = content;
//    }


    public void copyValues(ComplianceFileUploadResponse fileResponse){
        this.fileName = fileResponse.getFileName();
//        this.fileURL = fileResponse.getFileLink();
    }
}
