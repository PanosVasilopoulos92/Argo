package org.viators.argo.common.validation;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.viators.argo.common.exceptions.InvalidPdfException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.StorageException;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfValidator {

    private static final String PDF_MIME_TYPE = "application/pdf";
    private final Tika tika = new Tika();

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidPdfException("File is empty");
        }

        try (InputStream in = file.getInputStream()) {
            String detected = tika.detect(in);
            if (!PDF_MIME_TYPE.equals(detected)) {
                throw new InvalidPdfException(
                    "File is not a PDF. Detected type: " + detected
                );
            }
        } catch (IOException e) {
            throw new InvalidPdfException("Could not read uploaded file: " + e.getMessage());
        }
    }
}
