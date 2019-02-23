package de.htw.ai.loz.gpan.mac.msg;

public class MacDataRsp {

    private ConfirmationResult result;

    public MacDataRsp(ConfirmationResult result) {
        this.result = result;
    }

    public ConfirmationResult getResult() {
        return result;
    }
}
