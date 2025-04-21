package org.librarymanagementsystem.servicesImp;

import lombok.RequiredArgsConstructor;
import org.librarymanagementsystem.repository.BookBorrowRepository;
import org.librarymanagementsystem.repository.BookRepository;
import org.librarymanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Long> getBorrowingTrends(LocalDate startDate, LocalDate endDate) {
        return bookBorrowRepository.getBorrowingTrendsBetweenDates(startDate, endDate);

    }

    public List<Map<String, Object>> getMemberActivityReport() {
        return userRepository.getUserActivityReport();
    }
}
