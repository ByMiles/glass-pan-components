package de.htw.ai.loz.gpan.mac.macCop.defs;

public enum MacCopStatus {

    OFFLINE (MacCopCommandSet.SET_PARAM),
    RESETTING (MacCopCommandSet.RESET),
    SUBSCRIBING (MacCopCommandSet.SUBSCRIBE),
    EXTENDEDSETTING (MacCopCommandSet.SET_PARAM),
    SHORTSETTING (MacCopCommandSet.SET_PARAM),
    STARTING (MacCopCommandSet.START),
    ONLINE (MacCopCommandSet.DATA),
    ;

    private MacCopCommandSet intendedCommand;

    MacCopStatus(MacCopCommandSet subscribe) {

        intendedCommand = subscribe;
    }

    public boolean isIntendedCommand(MacCopCommandSet answeredCommand) {
        return (intendedCommand != null) && (intendedCommand == answeredCommand);
    }
}
