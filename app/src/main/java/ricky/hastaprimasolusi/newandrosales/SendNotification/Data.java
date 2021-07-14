package ricky.hastaprimasolusi.newandrosales.SendNotification;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName ("title")
    private String Title;
    @SerializedName ("message")
    private String Message;

    public Data(String title, String message) {
        Title = title;
        Message = message;
    }

    public Data() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

}