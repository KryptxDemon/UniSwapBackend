// src/main/java/com/uniswap/UniSwap/controller/BorrowRecordController.java
package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.entity.BorrowRecord;
import com.uniswap.UniSwap.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @GetMapping("/lender/{userId}")
    public ResponseEntity<List<BorrowRecord>> getLentRecords(@PathVariable Integer userId) {
        return ResponseEntity.ok(borrowRecordService.getByLender(userId));
    }

    @GetMapping("/borrower/{userId}")
    public ResponseEntity<List<BorrowRecord>> getBorrowedRecords(@PathVariable Integer userId) {
        return ResponseEntity.ok(borrowRecordService.getByBorrower(userId));
    }
}
