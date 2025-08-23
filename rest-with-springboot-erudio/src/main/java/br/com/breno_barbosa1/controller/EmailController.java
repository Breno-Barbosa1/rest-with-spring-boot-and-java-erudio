package br.com.breno_barbosa1.controller;

import br.com.breno_barbosa1.controller.docs.EmailControllerDocs;
import br.com.breno_barbosa1.data.dto.v1.request.EmailRequestDTO;
import br.com.breno_barbosa1.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/email/v1")
public class EmailController implements EmailControllerDocs {

    @Autowired
    EmailService service;

    @PostMapping
    @Override
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequest) {
        service.sendSimpleEmail(emailRequest);
        return new ResponseEntity<>("Email sent successfully!", HttpStatus.OK);
    }

    @PostMapping(value = "/withAttachment")
    @Override
    public ResponseEntity<String> sendEmailWithAttachment(
        @RequestParam("emailRequest") String emailRequest,
        @RequestParam("attachment") MultipartFile attachment) {
        service.sendEmailWithAttachment(emailRequest, attachment);
        return new ResponseEntity<>("Email with attachment sent successfully!", HttpStatus.OK);
    }
}
