package de.htw.ai.loz.gpan.mac.msg;

public class MacDataCnf {

    private ConfirmationResult cmdStatus;
    private Integer commandId;

    public MacDataCnf(ConfirmationResult ntfStatus, int commandId) {
        this.cmdStatus = ntfStatus;
        this.commandId = commandId;
    }

    public ConfirmationResult getCmdStatus() {
        return cmdStatus;
    }

    public void setCmdStatus(ConfirmationResult cmdStatus) {
        this.cmdStatus = cmdStatus;
    }

    public Integer getHandleId() {
        return commandId;
    }

    public void setCommandId(Integer commandId) {
        this.commandId = commandId;
    }
}
