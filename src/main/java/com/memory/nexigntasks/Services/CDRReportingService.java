package com.memory.nexigntasks.Services;

import com.memory.nexigntasks.Entities.CDRRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Сервис для асинхронной генерации CSV отчёта с CDR.
 */
@Service
public class CDRReportingService {
    private static final String REPORT_DIR = "reports";

    @Async
    public void generateReport(String msisdn, String uuid, List<CDRRecord> records) {
        try {
            Path directory = Path.of(REPORT_DIR);
            if (!Files.exists(directory)) {
                Files.createDirectory(directory);
            }

            String fileName = msisdn + "-" + uuid + ".csv";
            File file = directory.resolve(fileName).toFile();

            try (Writer out = new FileWriter(file);
                 CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(
                         "call_type", "from", "to", "startTime", "endTime"))) {

                for (CDRRecord record : records) {
                    printer.printRecord(
                            record.getCallType().getDbCode(),
                            record.getCallingMsId(),
                            record.getReceivingMsId(),
                            CDRRecord.prettyDateTime(record.getCallStartTime()),
                            CDRRecord.prettyDateTime(record.getCallEndTime())
                    );
                }
            }
            System.out.println("Отчёт создан: " + fileName);

        } catch (Exception e) {
            System.err.println("Ошибка создания отчёта: " + e.getMessage());
        }
    }
}
