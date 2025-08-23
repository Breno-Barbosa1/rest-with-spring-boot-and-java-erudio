package br.com.breno_barbosa1.services;

import br.com.breno_barbosa1.config.EmailConfig;
import br.com.breno_barbosa1.data.dto.v1.request.EmailRequestDTO;
import br.com.breno_barbosa1.mail.EmailSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    EmailConfig emailConfigs;

    @Autowired
    EmailSender emailSender;

    public void sendSimpleEmail(EmailRequestDTO emailRequest) {
        emailSender
            .to(emailRequest.getTo())
            .withSubject(emailRequest.getSubject())
            .withMessage(emailRequest.getBody())
            .send(emailConfigs);
    }

    public void sendEmailWithAttachment(String emailRequestJson, MultipartFile attachment) {
        File tempFile = null;
        try {
            EmailRequestDTO emailRequest = new ObjectMapper().readValue(emailRequestJson, EmailRequestDTO.class);
            tempFile = File.createTempFile("attachment", attachment.getOriginalFilename());
            attachment.transferTo(tempFile);

            emailSender
                .to(emailRequest.getTo())
                .withSubject(emailRequest.getSubject())
                .withMessage(emailRequest.getBody())
                .attach(tempFile.getAbsolutePath())
                .send(emailConfigs);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing email request JSON!",e);
        } catch (IOException e) {
            throw new RuntimeException("Error processing the attachment file!",e);
        } finally {
            if (tempFile != null && tempFile.exists()) tempFile.delete();
        }
    }
}
