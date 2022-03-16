package com.fcastro.statement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class StatementUploadRequest {
    private long clientId;
    private long bankId;
    private MultipartFile file;
}
