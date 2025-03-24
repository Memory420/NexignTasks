package com.memory.nexigntasks;

import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Utils.CallType;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Тесты для CDRController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CDRControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CDRRecordRepository cdrRecordRepository;

    @Test
    void testGetCDRRecordById() throws Exception {
        CDRRecord record = new CDRRecord(
                CallType.OUTGOING,
                "79123456789",
                "79991112233",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(3)
        );
        record.setId(1L);

        Mockito.when(cdrRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        mockMvc.perform(get("/api/cdrrecord/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.callType").value("01"))
                .andExpect(jsonPath("$.callingMsId").value("79123456789"))
                .andExpect(jsonPath("$.receivingMsId").value("79991112233"));
    }

    @Test
    void testGetCDRRecordNotFound() throws Exception {
        Mockito.when(cdrRecordRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cdrrecord/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllCDRRecords() throws Exception {
        CDRRecord record1 = new CDRRecord(
                CallType.OUTGOING,
                "79123456789",
                "79991112233",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(3)
        );
        record1.setId(1L);

        CDRRecord record2 = new CDRRecord(
                CallType.INCOMING,
                "79887766554",
                "79997766555",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5)
        );
        record2.setId(2L);

        Mockito.when(cdrRecordRepository.findAll()).thenReturn(List.of(record1, record2));

        mockMvc.perform(get("/api/cdrrecord/udr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].callType").value("01"))
                .andExpect(jsonPath("$[1].callType").value("02"));
    }
}
