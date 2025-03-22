package com.memory.nexigntasks.Controllers;

import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cdrrecord/")
public class CDRController {
    private final CDRRecordRepository cdrRecordRepository;

    @Autowired
    public CDRController(CDRRecordRepository cdrRecordRepository) {
        this.cdrRecordRepository = cdrRecordRepository;
    }

    @GetMapping("/{id}")
    public CDRRecord findById(@PathVariable Long id) {
        return cdrRecordRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("CDR record not found"));
    }
    @GetMapping("/udr")
    public List<CDRRecord> findAllRecords() {
        return cdrRecordRepository.findAll();
    }
}
