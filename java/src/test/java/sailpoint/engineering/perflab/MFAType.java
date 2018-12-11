package sailpoint.engineering.perflab;

public enum MFAType {
    KBA("KBA answer"),
    EMAIL_WORK("work email"),
    EMAIL_PERSONAL("personal email"),
    SMS_WORK("work phone"),
    SMS_PERSONAL("alternative phone");

    private String friendlyName;

    MFAType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName () {
        return friendlyName;
    }
}
