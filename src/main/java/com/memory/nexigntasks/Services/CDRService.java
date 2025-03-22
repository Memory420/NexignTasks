package com.memory.nexigntasks.Services;

import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CDRService {
    private final CDRRecordRepository repository;

    @Autowired
    public CDRService(CDRRecordRepository repository) {
        this.repository = repository;
    }

    public CDRRecord getCDRRecord(long id) {
        return repository.findById(id).orElse(null);
    }
}
