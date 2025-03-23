package com.memory.nexigntasks.Controllers;

import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cdrrecord/")
public class CDRController {
    private final CDRRecordRepository cdrRecordRepository;

    @Autowired
    public CDRController(CDRRecordRepository cdrRecordRepository) {
        this.cdrRecordRepository = cdrRecordRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CDRRecord> findById(@PathVariable Long id) {
        Optional<CDRRecord> optionalCDRRecord = cdrRecordRepository.findById(id);

        if (optionalCDRRecord.isPresent()) {
            return ResponseEntity.ok(optionalCDRRecord.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/udr")
    public ResponseEntity<List<CDRRecord>> findAllRecords() {
        List<CDRRecord> all = cdrRecordRepository.findAll();
        return ResponseEntity.ok(all);
    }
}
