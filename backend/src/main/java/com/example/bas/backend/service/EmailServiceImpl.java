package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.Entry;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = Logger.getLogger(EmailServiceImpl.class.getName());
    private final JavaMailSender javaMailSender;
    private final AdditionalInfoService additionalInfoService;
    private final EntryService entryService;
    private final AppUserService appUserService;


    @Override
    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("chrisdownander@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    @Override
    public void sendStats(String to, String username) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = appUserService.findUserByEmail(to);
        Entry todayEntry = entryService.findByEntryDateAndUserId(new Date(), user.getId());
        AdditionalInfo todayInfo = additionalInfoService.findByUserIdAndEntryDate(user.getId(), new Date());
        Map<String, Map<String, Double>> entryStats = entryService.generateEntryStats(user.getId());
        Map<String, Map<String, Double>> infoStats = additionalInfoService.generateAdditionalStats(user.getId());
        infoStats.get("sleep").remove("healthySleep");
        infoStats.get("sleep").remove("sleepHoursPercentage");


        Document document = new Document();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(String.format("stats_%s_%s.pdf", username, simpleDateFormat.format(new Date()))));
            document.open();

            PdfPTable userTable = new PdfPTable(2);
            createUserTable(userTable, user, simpleDateFormat);

            PdfPTable entryTable = new PdfPTable(7);
            createEntryTable(entryTable, todayEntry, entryStats, simpleDateFormat);

            PdfPTable infoTable = new PdfPTable(7);
            createInfoTable(infoTable, todayInfo, infoStats, simpleDateFormat);

            Chunk chunk;
            Paragraph paragraph = new Paragraph();
            Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 20, BaseColor.BLACK);
            chunk = new Chunk(String.format("Report from %s", simpleDateFormat.format(new Date())), font);
            paragraph.add(chunk);
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(paragraph);

            document.add(new Paragraph("\n"));
            document.add(userTable);

            chunk = new Chunk("Entry info:");
            document.add(new Paragraph("\n"));
            document.add(chunk);
            document.add(new Paragraph("\n"));
            document.add(entryTable);

            chunk = new Chunk("Additional info:");
            document.add(new Paragraph("\n"));
            document.add(chunk);
            document.add(new Paragraph("\n"));
            document.add(infoTable);
            document.add(new Paragraph("\n"));

            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File(String.format("stats_%s_%s.pdf", username, simpleDateFormat.format(new Date())));
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom("chrisdownander@gmail.com");
            helper.setTo(to);
            helper.setSubject(String.format("Stats from %s", simpleDateFormat.format(new Date())));
            helper.setText("In the attachment you can find a report of your stats!");
            helper.addAttachment("stats.pdf", file);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        if (file.delete()) {
            logger.info("Deleted the file: " + file.getName());
        } else {
            logger.warning("Failed to delete the file.");
        }
    }

    private void createEntryTable(PdfPTable entryTable, Entry todayEntry, Map<String, Map<String, Double>> entryStats, SimpleDateFormat simpleDateFormat) {
        List<String> keys = Arrays.asList("weight", "glucose", "insulin", "bloodPressure", "healthy", "sizes");
        Stream.of("", "Weight (kg)", "Glucose (mg/dL)", "Insulin (mU/ml)", "Blood Pressure (mm/Hg)", "Healthy", "Amount of Entries").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            entryTable.addCell(header);
        });
        entryTable.addCell(simpleDateFormat.format(todayEntry.getEntryDate()));
        entryTable.addCell(todayEntry.getWeight().toString());
        entryTable.addCell(todayEntry.getGlucose().toString());
        entryTable.addCell(todayEntry.getInsulin().toString());
        entryTable.addCell(todayEntry.getBloodPressure().toString());
        entryTable.addCell(String.valueOf(todayEntry.getHealthy() ? 100.0 : 0.0));
        entryTable.addCell("");

        entryTable.addCell("Weekly");
        for (String key : keys) {
            if (key.equals("sizes")) {
                entryTable.addCell(entryStats.get(key).get("weekly") + " / 7.0");
            } else {
                entryTable.addCell(String.valueOf(entryStats.get(key).get("weekly")));
            }
        }
        entryTable.addCell("Monthly");
        for (String key : keys) {
            if (key.equals("sizes")) {
                entryTable.addCell(entryStats.get(key).get("monthly") + " / 30.0");
            } else {
                entryTable.addCell(String.valueOf(entryStats.get(key).get("monthly")));
            }
        }

        entryTable.addCell("Yearly");
        for (String key : keys) {
            if (key.equals("sizes")) {
                entryTable.addCell(entryStats.get(key).get("yearly") + " / 365.0");
            } else {
                entryTable.addCell(String.valueOf(entryStats.get(key).get("yearly")));
            }
        }
    }

    private void createInfoTable(PdfPTable infoTable, AdditionalInfo todayInfo, Map<String, Map<String, Double>> infoStats, SimpleDateFormat simpleDateFormat) {
        List<String> keys = Arrays.asList("sleep", "alcohol", "cigarettes", "training", "water", "sizes");
        Stream.of("", "Hours of sleep", "Alcohol (unit)", "Cigarettes (unit)", "Hours of training", "Water (unit)", "Amount of Entries").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            infoTable.addCell(header);
        });
        infoTable.addCell(simpleDateFormat.format(todayInfo.getEntryDate()));
        infoTable.addCell(todayInfo.getSleepHours().toString());
        infoTable.addCell(todayInfo.getAlcoholAmount().toString());
        infoTable.addCell(todayInfo.getCigarettesAmount().toString());
        infoTable.addCell(todayInfo.getTrainingHours().toString());
        infoTable.addCell(todayInfo.getGlassesOfWater().toString());
        infoTable.addCell("");

        infoTable.addCell("Weekly");
        for (String key : keys) {
            if (key.equals("sizes")) {
                infoTable.addCell(infoStats.get(key).get("weekly") + " / 7.0");
            } else {
                infoTable.addCell(String.valueOf(infoStats.get(key).get("weekly")));
            }
        }
        infoTable.addCell("Monthly");
        for (String key : keys) {
            if (key.equals("sizes")) {
                infoTable.addCell(infoStats.get(key).get("monthly") + " / 30.0");
            } else {
                infoTable.addCell(String.valueOf(infoStats.get(key).get("monthly")));
            }
        }

        infoTable.addCell("Yearly");
        for (String key : keys) {
            if (key.equals("sizes")) {
                infoTable.addCell(infoStats.get(key).get("yearly") + " / 365.0");
            } else {
                infoTable.addCell(String.valueOf(infoStats.get(key).get("yearly")));
            }
        }
    }

    private void createUserTable(PdfPTable userTable, AppUser user, SimpleDateFormat simpleDateFormat) {
        PdfPCell cell;

        cell = new PdfPCell();
        cell.setBorderColor(BaseColor.WHITE);
        cell.setPhrase(new Phrase("Username: " + user.getUsername()));
        userTable.addCell(cell);

        cell = new PdfPCell();
        cell.setBorderColor(BaseColor.WHITE);
        cell.setPhrase(new Phrase(String.format("Name: %s %s", user.getFirstName(), user.getLastName())));
        userTable.addCell(cell);

        cell = new PdfPCell();
        cell.setBorderColor(BaseColor.WHITE);
        cell.setPhrase(new Phrase("Email: " + user.getEmail()));
        userTable.addCell(cell);

        cell = new PdfPCell();
        cell.setBorderColor(BaseColor.WHITE);
        cell.setPhrase(new Phrase("Birth date: " + simpleDateFormat.format(user.getBirthDate())));
        userTable.addCell(cell);

        cell = new PdfPCell();
        cell.setBorderColor(BaseColor.WHITE);
        cell.setPhrase(new Phrase(""));
        userTable.addCell(cell);

        cell = new PdfPCell();
        cell.setBorderColor(BaseColor.WHITE);
        cell.setPhrase(new Phrase("Gender: " + user.getGender()));
        userTable.addCell(cell);

        cell = new PdfPCell();
        cell.setBorderColor(BaseColor.WHITE);
        cell.setPhrase(new Phrase(""));
        userTable.addCell(cell);

        cell = new PdfPCell();
        cell.setBorderColor(BaseColor.WHITE);
        cell.setPhrase(new Phrase("Height: " + user.getHeight()));
        userTable.addCell(cell);
    }

}
