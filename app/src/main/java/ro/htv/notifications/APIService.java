package ro.htv.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA_K46Aak:APA91bE4lgG0uQdGqshoBLkEVjIbuIHDPDcE3yfufmGgWFnZF1Fik5sscIPMwkScqzDhGyryhkOy3rrFBOVRnuozGUlcuH8WAnAk2mOpyJ0__26p68PYdvQ_n4BI_IRpQ9FDe8z1L6zG"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
