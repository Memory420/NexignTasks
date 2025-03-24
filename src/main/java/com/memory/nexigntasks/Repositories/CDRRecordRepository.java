package com.memory.nexigntasks.Repositories;

import com.memory.nexigntasks.Entities.CDRRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CDRRecordRepository extends JpaRepository<CDRRecord, Long> {
    List<CDRRecord> findByCallingMsIdOrReceivingMsId(String msisdn1, String msisdn2);

    @Query("SELECT r FROM CDRRecord r WHERE (r.callingMsId = :msisdn OR r.receivingMsId = :msisdn) " +
            "AND r.callStartTime >= :start AND r.callStartTime < :end")
    List<CDRRecord> findByMsisdnAndPeriod(
            @Param("msisdn") String msisdn,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
