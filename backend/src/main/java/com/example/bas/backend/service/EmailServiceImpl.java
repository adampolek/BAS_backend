package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.Entry;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

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


        Document document = new Document();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(String.format("stats_%s_%s.pdf", username, simpleDateFormat.format(new Date()))));
            document.open();


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

}
