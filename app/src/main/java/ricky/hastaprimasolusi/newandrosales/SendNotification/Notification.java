package ricky.hastaprimasolusi.newandrosales.SendNotification;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName ("body")
    String body;
    @SerializedName ("title")
    String title;

    public Notification(String body, String title){
        this.body = body;
        this.title = title;
    }
}
