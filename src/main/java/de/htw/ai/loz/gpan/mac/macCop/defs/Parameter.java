package de.htw.ai.loz.gpan.mac.macCop.defs;

public enum Parameter {
    ZMAC_ACK_WAIT_DURATION ((byte) 0x40),
    ZMAC_ASSOCIATION_PERMIT ((byte) 0x41),
    ZMAC_AUTO_REQUEST ((byte) 0x42),
    ZMAC_BATT_LIFE_EXT ((byte) 0x43),
    ZMAC_BATT_LEFT_EXT_PERIODS ((byte) 0x44),
    ZMAC_BEACON_MSDU ((byte) 0x45),
    ZMAC_BEACON_MSDU_LENGTH ((byte) 0x46),
    ZMAC_BEACON_ORDER ((byte) 0x47),
    ZMAC_BEACON_TX_TIME ((byte) 0x48),
    ZMAC_BSN ((byte) 0x49),
    ZMAC_COORD_EXTENDED_ADDRESS ((byte) 0x4A),
    ZMAC_COORD_SHORT_ADDRESS ((byte) 0x4B),
    ZMAC_DSN ((byte) 0x4C),
    ZMAC_GTS_PERMIT ((byte) 0x4D),
    ZMAC_MAX_CSMA_BACKOFFS ((byte) 0x4E),
    ZMAC_MIN_BE ((byte) 0x4F),
    ZMAC_PANID ((byte) 0x50),
    ZMAC_PROMISCUOUS_MODE ((byte) 0x51),
    ZMAC_RX_ON_IDLE ((byte) 0x52),
    ZMAC_SHORT_ADDRESS ((byte) 0x53),
    ZMAC_SUPERFRAME_ORDER ((byte) 0x54),
    ZMAC_TRANSACTION_PERSISTENCE_TIME ((byte) 0x55),
    ZMAC_ASSOCIATED_PAN_COORD ((byte) 0x56),
    ZMAC_MAX_BE ((byte) 0x57),
    ZMAC_FRAME_TOTAL_WAIT_TIME ((byte) 0x58),
    ZMAC__MAC_FRAME_RETRIES ((byte) 0x59),
    ZMAC_RESPONSE_WAIT_TIME ((byte) 0x5A),
    ZMAC_SYNC_SYMBOL_OFFSET ((byte) 0x5B),
    ZMAC_TIMESTAMP_SUPPORTED ((byte) 0x5C),
    ZMAC_SECURITY_ENABLED ((byte) 0x5D),
    ZMAC_PHY_TRANSMIT_POWER ((byte) 0xE0),
    ZMAC_LOGICAL_CHANNEL ((byte) 0xE1),
    ZMAC_EXTENDED_ADDRESS ((byte) 0xE2),
    ZMAC_ALT_BE ((byte) 0xE3);
    private byte value;
    Parameter(byte value){
        this.value = value;
    }
    
    public byte getValue(){
        return value;
    }
}
