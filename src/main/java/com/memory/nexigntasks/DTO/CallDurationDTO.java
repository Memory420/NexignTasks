package com.memory.nexigntasks.DTO;

/**
 * DTO для суммарной длительности (в формате hh:mm:ss).
 */
public class CallDurationDTO {

    private String totalTime;

    public CallDurationDTO() {
    }

    public CallDurationDTO(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
    public static String secondsToPrettyTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

}
