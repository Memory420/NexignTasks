package com.memory.nexigntasks.Repositories;

import com.memory.nexigntasks.Entities.CDRRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CDRRecordRepository extends JpaRepository<CDRRecord, Long> {
}
