package com.example.demo.repository;

import com.example.demo.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReceiptRepo extends JpaRepository <Receipt, Long> {
    Optional<Receipt> findByReceiptCode (String code);
    List<Receipt> findByUserIdOrderByDateCreatedDateFormatDesc (Long userId);

    List<Receipt> findAllByOrderByDateCreatedDateFormatDesc();

    List<Receipt> findByDateCreatedDateFormatBetween(LocalDateTime startDate, LocalDateTime endDate);
}
