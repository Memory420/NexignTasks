package com.memory.nexigntasks.DTO;


public class UdrDTO {

    private String msisdn;

    private CallDurationDTO incomingCall;
    private CallDurationDTO outcomingCall;

    public UdrDTO() {
    }

    public UdrDTO(String msisdn, CallDurationDTO incomingCall, CallDurationDTO outcomingCall) {
        this.msisdn = msisdn;
        this.incomingCall = incomingCall;
        this.outcomingCall = outcomingCall;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public CallDurationDTO getIncomingCall() {
        return incomingCall;
    }

    public void setIncomingCall(CallDurationDTO incomingCall) {
        this.incomingCall = incomingCall;
    }

    public CallDurationDTO getOutcomingCall() {
        return outcomingCall;
    }

    public void setOutcomingCall(CallDurationDTO outcomingCall) {
        this.outcomingCall = outcomingCall;
    }
}
