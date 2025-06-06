package com.email.reply_generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGenService {
    private final WebClient webClient;
    public EmailGenService(WebClient.Builder webClientBuilder){
        this.webClient= WebClient.builder().build();
    }
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private  String getGeminiApiKey;

    public String generateEmailReply(EmailRequest emailRequest){
//        Building the prompt
        String prompt=buildPrompt(emailRequest);

//        Crafting a request
        Map<String,Object> requestBody=Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                }
        );

//        Do the request and get the response
        String response=webClient.post()
                .uri(geminiApiUrl + getGeminiApiKey)
                .header("Content-TYPE","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();



//       Extracting and Returning the response
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode= mapper.readTree(response);
            return  rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }catch (Exception e){
            return "Error processing request: "+ e.getMessage();
        }
    }


    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt=new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content.Please don't generate the subject line  ");
        if(emailRequest.getTone()!=null && !emailRequest.getTone().isEmpty()){
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal Email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();

    }
}
