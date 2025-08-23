package br.com.breno_barbosa1.data.dto.v1;

import java.io.Serial;
import java.io.Serializable;

public class UploadFileResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private Long fileSize;

    public UploadFileResponseDTO() {}

    public UploadFileResponseDTO(String fileName, String fileDownloadUri, String fileType, Long fileSize) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
