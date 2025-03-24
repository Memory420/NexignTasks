package com.memory.nexigntasks.Controllers;

import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import com.memory.nexigntasks.Services.CDRReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/cdrrecord/")
public class CDRController {
    private final CDRRecordRepository cdrRecordRepository;
    private final CDRReportingService cdrReportingService;

    @Autowired
    public CDRController(CDRRecordRepository cdrRecordRepository, CDRReportingService cdrReportingService) {
        this.cdrRecordRepository = cdrRecordRepository;
        this.cdrReportingService = cdrReportingService;
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

    @GetMapping("/report")
    public ResponseEntity<String> generateReport(
            @RequestParam String msisdn,
            @RequestParam String from,
            @RequestParam String to
    ) {
        try {
            LocalDateTime fromDate = LocalDateTime.parse(from);
            LocalDateTime toDate = LocalDateTime.parse(to);

            List<CDRRecord> records = cdrRecordRepository.findByMsisdnAndPeriod(msisdn, fromDate, toDate);

            if (records.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String uuid = UUID.randomUUID().toString();

            cdrReportingService.generateReport(msisdn, uuid, records);

            return ResponseEntity.ok("Report generation started. Request ID: " + uuid);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error parsing date or generating report: " + e.getMessage());
        }
    }

}
