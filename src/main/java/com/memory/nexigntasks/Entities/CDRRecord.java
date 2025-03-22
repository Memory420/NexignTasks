package com.memory.nexigntasks.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class CDRRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String callType;
    private String callingMsId;
    private String receivingMsId;
    private LocalDateTime callStartTime;
    private LocalDateTime callEndTime;

    public CDRRecord(String callType, String callingMsId, String receivingMsId, LocalDateTime callStartTime, LocalDateTime callEndTime) {
        this.callType = callType;
        this.callingMsId = callingMsId;
        this.receivingMsId = receivingMsId;
        this.callStartTime = callStartTime;
        this.callEndTime = callEndTime;
    }

    public CDRRecord() {
    }

    public Long getId() {
        return id;
    }

    public String getCallType() {
        return callType;
    }

    public String getCallingMsId() {
        return callingMsId;
    }

    public String getReceivingMsId() {
        return receivingMsId;
    }

    public LocalDateTime getCallStartTime() {
        return callStartTime;
    }

    public LocalDateTime getCallEndTime() {
        return callEndTime;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public static String prettyDateTime(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        String date = dateTimeFormatter.format(dateTime);
        String time = timeFormatter.format(dateTime);

        return date + "T" + time;
    }
}
