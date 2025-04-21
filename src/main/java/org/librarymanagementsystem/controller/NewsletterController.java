package org.librarymanagementsystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.services.NewsletterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "NewsletterController", description = "Newsletter Management")
@RestController
@RequestMapping("/v1/api/Newsletter")
@RequiredArgsConstructor
public class NewsletterController {
    public final NewsletterService newsletterService;

    @Operation(summary = "subscribe of Newsletter Management ", description = "This ")
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestParam String email){
        try {
            String result = newsletterService.subscribe(email);

            return switch (result) {

                case "Email is already subscribed." ->
                        ResponseEntity.status(HttpStatus.CONFLICT).body(result); // 409 Conflict

                case "You have successfully subscribed!" ->
                        ResponseEntity.status(HttpStatus.CREATED).body(result); // 201 Created

                case "You have successfully re-subscribed!" ->
                        ResponseEntity.status(HttpStatus.OK).body(result); // 200 OK

                default -> ResponseEntity.status(HttpStatus.OK).body(result); // Default 200 OK
            };
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your subscription.");
        }
    }

    @Operation(summary = "unsubscribe of Newsletter Management ", description = "This ")
   @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@Valid @RequestParam  String email, String token){
        String result = newsletterService.unsubscribe(email, token);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
