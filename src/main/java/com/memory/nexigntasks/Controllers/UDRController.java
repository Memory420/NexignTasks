package com.memory.nexigntasks.Controllers;

import com.memory.nexigntasks.DTO.CallDurationDTO;
import com.memory.nexigntasks.DTO.UdrDTO;
import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Entities.Subscriber;
import com.memory.nexigntasks.Repositories.SubscriberRepository;
import com.memory.nexigntasks.Utils.CallType;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static com.memory.nexigntasks.DTO.CallDurationDTO.secondsToPrettyTime;

/**
 * Контроллер для формирования и выдачи UDR по абонентам.
 */
@RestController
@RequestMapping("/api/udr")
public class UDRController {
    private final CDRRecordRepository cdrRecordRepository;
    private final SubscriberRepository subscriberRepository;

    @Autowired
    public UDRController(CDRRecordRepository cdrRecordRepository, SubscriberRepository subscriberRepository) {
        this.cdrRecordRepository = cdrRecordRepository;
        this.subscriberRepository = subscriberRepository;
    }

    @GetMapping("/subscribed")
    public ResponseEntity<UdrDTO> getSubscribedUdr(@RequestParam String msisdn, @RequestParam(required = false) String ym) {
        if (msisdn == null || msisdn.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (!msisdn.startsWith("79") || msisdn.length() != 11) {
            return ResponseEntity.badRequest().build();
        }
        if (ym == null || ym.isEmpty()) {
            List<CDRRecord> cdrRecords = cdrRecordRepository.findByCallingMsIdOrReceivingMsId(msisdn, msisdn);
            if (cdrRecords.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            UdrDTO dto = buildUdrDTO(msisdn, cdrRecords);
            return ResponseEntity.ok(dto);
        } else {
            if (!isCorrectDataForm(ym)) {
                return ResponseEntity.badRequest().build();
            }
            LocalDateTime[] range = parseYm(ym);
            List<CDRRecord> cdrRecords = cdrRecordRepository.findByMsisdnAndPeriod(msisdn, range[0], range[1]);
            if (cdrRecords.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            UdrDTO dto = buildUdrDTO(msisdn, cdrRecords);
            return ResponseEntity.ok(dto);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UdrDTO>> getAllUdr(@RequestParam String ym) {
        if (!isCorrectDataForm(ym)) {
            return ResponseEntity.badRequest().build();
        }
        LocalDateTime[] range = parseYm(ym);
        List<Subscriber> allSubs = subscriberRepository.findAll();
        if (allSubs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<UdrDTO> results = new ArrayList<>();
        for (Subscriber subscriber : allSubs) {
            String msisdn = subscriber.getNumber();
            List<CDRRecord> cdrRecords = cdrRecordRepository.findByMsisdnAndPeriod(msisdn, range[0], range[1]);
            UdrDTO dto = buildUdrDTO(msisdn, cdrRecords);
            results.add(dto);
        }
        return ResponseEntity.ok(results);
    }

    public static boolean isCorrectDataForm(String yearMonth) {
        if (yearMonth == null) {
            return false;
        }
        if (yearMonth.length() == 4) {
            return yearMonth.matches("\\d{4}");
        } else if (yearMonth.length() == 7) {
            if (!yearMonth.matches("\\d{4}-\\d{2}")) return false;
            String[] parts = yearMonth.split("-");
            int month = Integer.parseInt(parts[1]);

            return month >= 1 && month <= 12;
        }
        return false;
    }

    private LocalDateTime[] parseYm(String ym) {
        if (ym.length() == 4) {
            int year = Integer.parseInt(ym);
            return new LocalDateTime[]{
                    LocalDateTime.of(year, 1, 1, 0, 0),
                    LocalDateTime.of(year + 1, 1, 1, 0, 0)
            };
        } else {
            YearMonth yearMonth = YearMonth.parse(ym);
            return new LocalDateTime[]{
                    yearMonth.atDay(1).atStartOfDay(),
                    yearMonth.plusMonths(1).atDay(1).atStartOfDay()
            };
        }
    }

    private UdrDTO buildUdrDTO(String msisdn, List<CDRRecord> records) {
        int incomingSeconds = 0;
        int outgoingSeconds = 0;
        for (CDRRecord cdr : records) {
            LocalDateTime start = cdr.getCallStartTime();
            LocalDateTime end = cdr.getCallEndTime();
            long callDurationSec = Math.abs(Duration.between(start, end).toSeconds());
            if (cdr.getCallType() == CallType.INCOMING) {
                incomingSeconds += callDurationSec;
            } else {
                outgoingSeconds += callDurationSec;
            }
        }
        return new UdrDTO(
                msisdn,
                new CallDurationDTO(secondsToPrettyTime(incomingSeconds)),
                new CallDurationDTO(secondsToPrettyTime(outgoingSeconds))
        );
    }

}
