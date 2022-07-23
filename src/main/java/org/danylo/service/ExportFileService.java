package org.danylo.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.danylo.model.Trainer;
import org.danylo.model.WorkingTime;
import org.danylo.repository.WorkingTimeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class ExportFileService {
    private static final Logger logger = LoggerFactory.getLogger(WorkingTimeRepository.class);
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public void exportToPdf(List<Trainer> clubTrainers, HttpServletResponse response) {
        setResponseHeader(response, "application/pdf", ".pdf");
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
        } catch (IOException e) {
            logger.info("File upload error");
        }
        document.open();

        document.add(createTitle());
        document.add(createTable(clubTrainers));

        document.close();
    }

    private void setResponseHeader(HttpServletResponse response, String contentType, String extension) {
        response.setContentType(contentType);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        String fileName = timestamp + extension;
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=trainers_" + fileName;
        response.setHeader(headerKey, headerValue);
    }

    private Paragraph createTitle() {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(24);
        Paragraph paragraph = new Paragraph("My trainers", font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return paragraph;
    }

    private PdfPTable createTable(List<Trainer> clubTrainers) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        setTableHeader(table);
        setTableBody(table, clubTrainers);
        return table;
    }

    private void setTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.CYAN);
        cell.setPadding(15);
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(12);
        font.setStyle(Font.BOLD);
        cell.setPhrase(new Phrase("№", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("first_name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("last_name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("experience (years)", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("price (UAH)", font));
        table.addCell(cell);
    }

    private void setTableBody(PdfPTable table, List<Trainer> clubTrainers) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.YELLOW);
        cell.setPadding(15);
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(10);
        for (int i = 0; i < clubTrainers.size(); i++) {
            Trainer trainer = clubTrainers.get(i);
            cell.setPhrase(new Phrase(String.valueOf(i + 1), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(trainer.getFirstName(), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(trainer.getLastName(), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(LocalDate.now().getYear() - trainer.getExperience()), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(trainer.getPrice()), font));
            table.addCell(cell);
        }
    }

    public void exportToExcel(List<WorkingTime> workingDays, HttpServletResponse response) {
        setResponseHeader(response, "application/octet-stream", ".xlsx");
        workbook = new XSSFWorkbook();
        setTitle();
        setData(workingDays);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            logger.info("File upload error");
        }
    }


    private void setTitle() {
        sheet = workbook.createSheet("My trainers");
        XSSFRow row = sheet.createRow(0);
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setColor(org.apache.poi.ss.usermodel.Font.COLOR_RED);
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "Day", cellStyle);
        createCell(row, 1, "Working hours", cellStyle);
    }

    private void setData(List<WorkingTime> workingDays) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        cellStyle.setFont(font);

        for (int i = 0; i < workingDays.size(); i++) {
            WorkingTime workingTime = workingDays.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            createCell(row, 0, workingTime.getDayOfWeek().name(), cellStyle);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String hoursFrom = dateFormat.format(workingTime.getHoursFrom());
            String hoursTo = dateFormat.format(workingTime.getHoursTo());
            String workingHours = hoursFrom + " - " + hoursTo;
            createCell(row, 1, workingHours, cellStyle);
        }
    }

    private void createCell(XSSFRow row, int columnIndex, String value, CellStyle cellStyle) {
        XSSFCell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        sheet.autoSizeColumn(columnIndex);
    }
}
