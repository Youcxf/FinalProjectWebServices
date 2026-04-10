package com.champsoft.finalprojectwebservices.borrowing.api;

import com.champsoft.finalprojectwebservices.book.api.BookResponse;
import com.champsoft.finalprojectwebservices.borrowing.domain.BorrowingStatus;
import com.champsoft.finalprojectwebservices.borrowing.domain.ReturnStatus;
import com.champsoft.finalprojectwebservices.member.api.MemberResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BorrowingResponse(
        UUID id,
        UUID memberId,
        BorrowingStatus status,
        LocalDate startDate,
        LocalDate dueDate,
        MemberResponse member,
        List<BorrowingResponseItem> items,
        ReturnSummary returnSummary
) {
    public record BorrowingResponseItem(UUID id, UUID bookId, int quantity, BookResponse book) {
    }

    public record ReturnSummary(UUID id, LocalDate returnDate, ReturnStatus returnStatus) {
    }
}
