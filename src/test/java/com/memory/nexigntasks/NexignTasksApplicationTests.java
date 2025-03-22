package com.memory.nexigntasks;

import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import com.memory.nexigntasks.Services.CDRService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NexignTasksApplicationTests {
    @Autowired
    private CDRService cdrService;

    @MockitoBean
    private CDRRecordRepository cdrRecordRepository;

    @Test
    void testGetCDRRecord() {
        CDRRecord record = new CDRRecord("01", "79555353535", "79123456789", LocalDateTime.now(), LocalDateTime.now().plusMinutes(3));
        record.setId(1L);

        Mockito.when(cdrRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        CDRRecord result = cdrService.getCDRRecord(1L);
        assertNotNull(result);
        assertEquals(record.getId(), result.getId());
        assertEquals("79555353535", result.getCallingMsId());
        assertEquals("79123456789", result.getReceivingMsId());
    }

    @Test
    void testGetCDRRecordNotFound() {
        Mockito.when(cdrRecordRepository.findById(2L)).thenReturn(Optional.empty());

        CDRRecord result = cdrService.getCDRRecord(2L);
        assertNull(result);
    }
}
