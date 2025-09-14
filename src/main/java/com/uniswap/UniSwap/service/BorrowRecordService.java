package com.uniswap.UniSwap.service;

import com.uniswap.UniSwap.entity.BorrowRecord;
import com.uniswap.UniSwap.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Transactional(readOnly = true)
    public List<BorrowRecord> getByLender(Integer userId) {
        return borrowRecordRepository.findByLenderUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<BorrowRecord> getByBorrower(Integer userId) {
        return borrowRecordRepository.findByBorrowerUserId(userId);
    }
}



