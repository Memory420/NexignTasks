package com.memory.nexigntasks;

import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Entities.Subscriber;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import com.memory.nexigntasks.Repositories.SubscriberRepository;
import com.memory.nexigntasks.Utils.CallType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для UDRController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UDRControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CDRRecordRepository cdrRecordRepository;

    @MockitoBean
    private SubscriberRepository subscriberRepository;

    @Test
    void testGetSubscribedUdr_NoYm() throws Exception {
        List<CDRRecord> mockRecords = List.of(
                new CDRRecord(CallType.INCOMING, "79123456789", "79991112233",
                        LocalDateTime.now(), LocalDateTime.now().plusMinutes(2)),
                new CDRRecord(CallType.OUTGOING, "79123456789", "79993334455",
                        LocalDateTime.now(), LocalDateTime.now().plusSeconds(30))
        );

        Mockito.when(cdrRecordRepository.findByCallingMsIdOrReceivingMsId("79123456789", "79123456789"))
                .thenReturn(mockRecords);

        mockMvc.perform(get("/api/udr/subscribed")
                        .param("msisdn", "79123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value("79123456789"))
                .andExpect(jsonPath("$.incomingCall.totalTime").exists())
                .andExpect(jsonPath("$.outcomingCall.totalTime").exists());
    }

    @Test
    void testGetSubscribedUdr_NotFound() throws Exception {
        Mockito.when(cdrRecordRepository.findByCallingMsIdOrReceivingMsId("79999999999", "79999999999"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/udr/subscribed")
                        .param("msisdn", "79999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUdr() throws Exception {

        Subscriber s1 = new Subscriber("79123456789");
        s1.setId(1L);
        Subscriber s2 = new Subscriber("79887766554");
        s2.setId(2L);

        Mockito.when(subscriberRepository.findAll()).thenReturn(List.of(s1, s2));

        List<CDRRecord> mockForS1 = List.of(
                new CDRRecord(CallType.INCOMING, "79991234567", "79123456789",
                        LocalDateTime.of(2025,5,3,10,0),
                        LocalDateTime.of(2025,5,3,10,5))
        );
        Mockito.when(cdrRecordRepository.findByMsisdnAndPeriod(
                Mockito.eq("79123456789"),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(mockForS1);

        Mockito.when(cdrRecordRepository.findByMsisdnAndPeriod(
                Mockito.eq("79887766554"),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/udr/all")
                        .param("ym", "2025-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].msisdn").value("79123456789"))
                .andExpect(jsonPath("$[1].msisdn").value("79887766554"));
    }
}
