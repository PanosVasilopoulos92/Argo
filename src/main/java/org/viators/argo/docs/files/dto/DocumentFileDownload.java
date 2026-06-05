package org.viators.argo.docs.files.dto;

import org.springframework.core.io.Resource;

public record DocumentFileDownload(
    Resource resource,
    String contentType,
    String originalFilename,
    long fileSize
) {
}
