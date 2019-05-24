package com.alsharqi.compliance.attachment;


import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.response.ComplianceFileUploadResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name="t_file_attachements")
public class FileAttachments {

    @Id
    @GeneratedValue
    private Long id;

    private String fileName;
    private String fileURL;
    private String contentType;
    private byte[] content;
    private String fileSize;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="compliance_id")
    private Compliance compliance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Compliance getCompliance() {
        return compliance;
    }

    public void setCompliance(Compliance compliance) {
        this.compliance = compliance;
    }

    public void copyValues(ComplianceFileUploadResponse response){

        this.fileName = response.getFileName();
        this.fileURL = response.getFileLink();
    }
}
