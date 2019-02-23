package de.htw.ai.loz.gpan.mac.macCop.defs;

import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;

public enum MacCopEventStatus {

    // COPIED FROM .\TIMAC 1.5.2.43299\Components\imp\include\mac_api.h

    MAC_SUCCESS                 (0x00), /* Operation successful */
    MAC_AUTOACK_PENDING_ALL_ON  (0xFE), /* The AUTOPEND pending all is turned on */
    MAC_AUTOACK_PENDING_ALL_OFF (0xFF), /* The AUTOPEND pending all is turned off */
    MAC_BEACON_LOSS             (0xE0), /* The beacon was lost following a synchronization request */
    MAC_CHANNEL_ACCESS_FAILURE  (0xE1), /* The operation or data request failed because of
                                             activity on the channel */
    MAC_COUNTER_ERROR           (0xDB), /* The adaptation counter puportedly applied by the originator of
                                             the received adaptation is invalid */
    MAC_DENIED                  (0xE2), /* The MAC was not able to enter low power mode. */
    MAC_FRAME_TOO_LONG          (0xE5), /* The received adaptation or adaptation resulting from an operation
                                             or data request is too long to be processed by the MAC */
    MAC_IMPROPER_KEY_TYPE       (0xDC), /* The key purportedly applied by the originator of the
                                             received adaptation is not allowed */
    MAC_IMPROPER_SECURITY_LEVEL (0xDD), /* The security level purportedly applied by the originator of
                                             the received adaptation does not meet the minimum security level */
    MAC_INVALID_ADDRESS         (0xF5), /* The data request failed because neither the source address nor
                                             destination address parameters were present */
    MAC_INVALID_HANDLE          (0xE7), /* The purge request contained an invalid onMessageReceived */
    MAC_INVALID_PARAMETER       (0xE8), /* The API function parameter is out of range */
    MAC_LIMIT_REACHED           (0xFA), /* The scan terminated because the PAN descriptor storage limit
                                             was reached */
    MAC_NO_ACK                  (0xE9), /* The operation or data request failed because no
                                             acknowledgement was received */
    MAC_NO_BEACON               (0xEA), /* The scan request failed because no beacons were received or the
                                             orphan scan failed because no coordinator realignment was received */
    MAC_NO_DATA                 (0xEB), /* The associate request failed because no associate response was received
                                             or the poll request did not return any data */
    MAC_NO_SHORT_ADDRESS        (0xEC), /* The short address parameter of the start request was invalid */
    MAC_PAN_ID_CONFLICT         (0xEE), /* A PAN identifier conflict has been detected and
                                             communicated to the PAN coordinator */
    MAC_READ_ONLY               (0xFB), /* A set request was issued with a read-only identifier */
    MAC_REALIGNMENT             (0xEF), /* A coordinator realignment command has been received */
    MAC_SCAN_IN_PROGRESS        (0xFC), /* The scan request failed because a scan is already in progress */
    MAC_SECURITY_ERROR          (0xE4), /* Cryptographic processing of the received secure adaptation failed */
    MAC_SUPERFRAME_OVERLAP      (0xFD), /* The beacon start time overlapped the coordinator transmission time */
    MAC_TRACKING_OFF            (0xF8), /* The start request failed because the device is not tracking
                                             the beacon of its coordinator */
    MAC_TRANSACTION_EXPIRED     (0xF0), /* The associate response, disassociate request, or indirect
                                             data transmission failed because the peer device did not respond
                                             before the transaction expired or was purged */
    MAC_TRANSACTION_OVERFLOW    (0xF1), /* The request failed because MAC data buffers are full */
    MAC_UNAVAILABLE_KEY         (0xF3), /* The operation or data request failed because the
                                             security key is not available */
    MAC_UNSUPPORTED_ATTRIBUTE   (0xF4), /* The set or get request failed because the attribute is not supported */
    MAC_UNSUPPORTED_LEGACY      (0xDE), /* The received adaptation was secured with legacy security which is
                                             not supported */
    MAC_UNSUPPORTED_SECURITY    (0xDF), /* The security of the received adaptation is not supported */
    MAC_UNSUPPORTED             (0x18), /* The operation is not supported in the current configuration */
    MAC_BAD_STATE               (0x19), /* The operation could not be performed in the current state */
    MAC_NO_RESOURCES            (0x1A), /* The operation could not be completed because no
                                             memory resources were available */

    // CUSTOM

    ;
    private byte value;

    MacCopEventStatus(int value) {
        this.value = (byte) value;
    }

    public static ConfirmationResult map(MacCopEventStatus eventStatus) {
        switch (eventStatus) {
            case MAC_SUCCESS:
                return ConfirmationResult.SUCCESS;
            case MAC_INVALID_PARAMETER:
                return ConfirmationResult.INVALID;
            case MAC_BAD_STATE:
            case MAC_NO_RESOURCES:
            case MAC_UNSUPPORTED:
                return ConfirmationResult.DENIED;
            case MAC_TRANSACTION_OVERFLOW:
            case MAC_TRANSACTION_EXPIRED:
                return ConfirmationResult.TIMEOUT;
            case MAC_NO_ACK:
                return ConfirmationResult.UNREACHABLE;
                default:
                    System.out.println("FAIL REASON: " + eventStatus.name());
                    return ConfirmationResult.FAIL;
        }
    }

    public byte getValue() {
        return value;
    }

    public boolean isNotificationStatus(byte status){
        return status == value;
    }

    public static MacCopEventStatus resolve(byte status) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].isNotificationStatus(status))
                return values()[i];
        }
        return MacCopEventStatus.MAC_UNSUPPORTED;
    }
}
