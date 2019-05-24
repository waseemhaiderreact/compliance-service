package com.alsharqi.compliance.attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileAttachmentsRepository extends JpaRepository<FileAttachments,Long> {
}
