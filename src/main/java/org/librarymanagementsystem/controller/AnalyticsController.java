package org.librarymanagementsystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.servicesImp.AnalyticsServiceImp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "AnalyticsController", description = "Analytics Management")
@RestController
@RequestMapping("/v1/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsServiceImp analyticsServiceImp;

    @Operation(summary = "overview of Analytics ", description = "This ")
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getLibraryOverview() {
        return ResponseEntity.ok(analyticsServiceImp.getLibraryOverview());
    }

    @Operation(summary = "popular-books of Analytics ", description = "This ")
    @GetMapping("/popular-books")
    public ResponseEntity<List<Map<String, Object>>> getPopularBooks(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsServiceImp.getPopularBooks(limit));
    }

    @Operation(summary = "borrowing-trends of Analytics ", description = "This ")
    @GetMapping("/borrowing-trends")
    public ResponseEntity <List<Map<String, Long>>> getBorrowingTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsServiceImp.getBorrowingTrends(startDate, endDate));
    }

    @Operation(summary = "member-activity of Analytics ", description = "This ")
    @GetMapping("/member-activity")
    public ResponseEntity<List<Map<String, Object>>> getMemberActivityReport() {
        return ResponseEntity.ok(analyticsServiceImp.getMemberActivityReport());
    }
}
