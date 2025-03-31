package org.librarymanagementsystem.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "BookController", description = "Book Management")
@RestController
@RequestMapping("/v1/api/book")
@RequiredArgsConstructor
public class BookController {
    // TODO: Implement book management endpoints
}
