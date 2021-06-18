package ricky.hastaprimasolusi.newandrosales.SendNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAALrkWhew:APA91bEtvib6XTVX0yGziGbQcIl4VX_py2EtuVjqfs-e-6WSlFtOtAKJp6y1kXdRmsiBJyV-tuvm6-WbNh9_IeLxyEY196ep8pnUN0Fd4qvaLD8kxI6xmv8kRP5iAHvZpm8dinPWl5L6" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}