package ricky.hastaprimasolusi.newandrosales.SendNotification;

import com.google.gson.annotations.SerializedName;

public class NotificationSender {
    @SerializedName ("data")
    public Data data;
    @SerializedName ("to")
    public String to;
    @SerializedName ("notification")
    public Notification notification;

    public NotificationSender(Data data, String to, Notification notification) {
        this.data = data;
        this.to = to;
        this.notification = notification;
    }

    public NotificationSender() {
    }
}