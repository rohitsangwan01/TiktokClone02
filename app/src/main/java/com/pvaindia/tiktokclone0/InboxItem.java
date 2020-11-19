package com.pvaindia.tiktokclone0;

public class InboxItem {

    private String mProfilePicUrl;
    private String mHeading;
    private String mNotificationTimeDifference;
    private String mAssociatedLink;

    public InboxItem(String profilePicUrl,
                     String heading,
                     String notificationTimeDifference,
                     String associatedLink) {

        mProfilePicUrl = profilePicUrl;
        mHeading = heading;
        mNotificationTimeDifference = notificationTimeDifference;
        mAssociatedLink = associatedLink;

    }

    public String getProfilePicUrl() {
        return mProfilePicUrl;
    }

    public String getHeading() {
        return mHeading;
    }

    public String getNotificationTimeDifference() {
        return mNotificationTimeDifference;
    }

    public String getAssociatedLink() {
        return mAssociatedLink;
    }
}
