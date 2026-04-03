package com.finance.dashboard.service;

import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.repository.FinancialRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialRecordService {

    private final FinancialRecordRepository repository;

    public FinancialRecordService(FinancialRecordRepository repository) {
        this.repository = repository;
    }

    public FinancialRecord createRecord(FinancialRecord record) {
        return repository.save(record);
    }

    public FinancialRecord updateRecord(Long id, FinancialRecord updatedRecord) {
        FinancialRecord existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
        existing.setAmount(updatedRecord.getAmount());
        existing.setType(updatedRecord.getType());
        existing.setCategory(updatedRecord.getCategory());
        existing.setDate(updatedRecord.getDate());
        existing.setDescription(updatedRecord.getDescription());
        return repository.save(existing);
    }

    public void deleteRecord(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Record not found");
        }
        repository.deleteById(id);
    }

    public FinancialRecord getRecordById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    }

    public List<FinancialRecord> getAllRecords() {
        return repository.findAll();
    }
}
