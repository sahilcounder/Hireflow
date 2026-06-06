package com.hireflow.screening.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ResumeParserService {

    /**
     * Extracts plain text from a resume file (.pdf or .docx).
     *
     * @param filePath absolute path to the resume file
     * @return extracted text content
     */
    public String extractText(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".pdf")) {
            return extractFromPdf(filePath);
        } else if (fileName.endsWith(".docx")) {
            return extractFromDocx(filePath);
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileName);
        }
    }

    private String extractFromPdf(String filePath) throws IOException {
        try (PDDocument document =
                     Loader.loadPDF(Paths.get(filePath).toFile())) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            log.info("Extracted {} characters from PDF {}", text.length(), filePath);

            return text;
        }
    }

    private String extractFromDocx(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String text = extractor.getText();

            log.info("Extracted {} characters from DOCX {}", text.length(), filePath);

            return text;
        }
    }
}