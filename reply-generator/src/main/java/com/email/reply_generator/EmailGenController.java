package com.email.reply_generator;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class EmailGenController {

    private final EmailGenService emailGenService;

    public EmailGenController(EmailGenService emailGenService) {
        this.emailGenService = emailGenService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
        String response=emailGenService.generateEmailReply(emailRequest);
       return ResponseEntity.ok(response);
    }
}
