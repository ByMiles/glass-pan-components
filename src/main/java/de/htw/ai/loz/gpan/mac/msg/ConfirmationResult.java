package de.htw.ai.loz.gpan.mac.msg;

public enum ConfirmationResult {
    SUCCESS(200),
    DENIED(403),
    FAIL(501),
    INVALID(400),
    UNREACHABLE(502),
    TIMEOUT(504);

    private final int responseStatus;

    ConfirmationResult(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public int getResponseStatus() {
        return responseStatus;
    }
}
