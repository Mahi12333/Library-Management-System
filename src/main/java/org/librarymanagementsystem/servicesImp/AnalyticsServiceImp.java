package org.librarymanagementsystem.servicesImp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.repository.BookBorrowRepository;
import org.librarymanagementsystem.repository.BookRepository;
import org.librarymanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImp {
    private final UserRepository userRepository;
    private final BookBorrowRepository bookBorrowRepository;
    private final BookRepository bookRepository;

    public Map<String, Object> getLibraryOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalBooks", bookRepository.count());
        overview.put("totalMembers", userRepository.count());
        overview.put("totalBorrowings", bookBorrowRepository.count());
        return overview;
    }

    public List<Map<String, Object>> getPopularBooks(int limit) {
        return bookBorrowRepository.findMostBorrowedBooks(limit);
    }

    public List<Map<String, Long>> getBorrowingTrends(LocalDate startDate, LocalDate endDate) {
        log.info("startDate--{}", startDate);
        log.info("endDate--{}", endDate);

        ZoneId defaultZoneId = ZoneId.systemDefault();

        // Convert LocalDate to Date
        Date startDateConverted = Date.from(startDate.atStartOfDay(defaultZoneId).toInstant());
        Date endDateConverted = Date.from(endDate.plusDays(1).atStartOfDay(defaultZoneId).toInstant());
        log.info("startDateConverted--{}", startDateConverted);
        log.info("endDateConverted--{}", endDateConverted);

        return bookBorrowRepository.getBorrowingTrendsBetweenDates(startDateConverted, endDateConverted);
    }

    public List<Map<String, Object>> getMemberActivityReport() {
        return userRepository.getUserActivityReport();
    }
}
