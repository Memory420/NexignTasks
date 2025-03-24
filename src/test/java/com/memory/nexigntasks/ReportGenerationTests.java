package com.memory.nexigntasks;

import com.memory.nexigntasks.Controllers.CDRController;
import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import com.memory.nexigntasks.Services.CDRReportingService;
import com.memory.nexigntasks.Utils.CallType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Тесты генерации отчёта.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReportGenerationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CDRRecordRepository cdrRecordRepository;

    @MockitoBean
    private CDRReportingService cdrReportingService;

    @Test
    void testGenerateReportOk() throws Exception {
        List<CDRRecord> mockRecords = List.of(
                new CDRRecord(CallType.OUTGOING, "79123456789", "79991112233",
                        LocalDateTime.now(), LocalDateTime.now().plusMinutes(3))
        );
        Mockito.when(cdrRecordRepository.findByMsisdnAndPeriod(
                Mockito.eq("79123456789"),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(mockRecords);

        mockMvc.perform(get("/api/cdrrecord/report")
                        .param("msisdn", "79123456789")
                        .param("from", "2024-05-01T00:00:00")
                        .param("to",   "2026-06-01T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Report generation started. Request ID: ")));

        ArgumentCaptor<String> msisdnCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> uuidCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<CDRRecord>> recordsCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(cdrReportingService, Mockito.times(1))
                .generateReport(msisdnCaptor.capture(), uuidCaptor.capture(), recordsCaptor.capture());

        assertEquals("79123456789", msisdnCaptor.getValue());
        assertFalse(uuidCaptor.getValue().isEmpty());
        assertEquals(mockRecords, recordsCaptor.getValue());
    }

    @Test
    void testGenerateReportNotFound() throws Exception {
        Mockito.when(cdrRecordRepository.findByMsisdnAndPeriod(
                Mockito.eq("79999999999"),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/cdrrecord/report")
                        .param("msisdn", "79999999999")
                        .param("from", "2024-05-01T00:00:00")
                        .param("to",   "2026-06-01T00:00:00"))
                .andExpect(status().isNotFound());

        Mockito.verify(cdrReportingService, Mockito.never())
                .generateReport(Mockito.anyString(), Mockito.anyString(), Mockito.anyList());
    }

    @Test
    void testGenerateReportBadRequest() throws Exception {
        mockMvc.perform(get("/api/cdrrecord/report")
                        .param("msisdn", "79123456789")
                        .param("from", "NOT_A_DATE")
                        .param("to",   "ALSO_BAD"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error parsing date or generating report")));
    }
}
