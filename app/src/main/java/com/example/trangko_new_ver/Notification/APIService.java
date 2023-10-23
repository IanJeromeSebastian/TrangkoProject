package com.example.trangko_new_ver.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({


                    "Content-Type: application/json",
                    "Authorization:key=AAAA3E11gkU:APA91bHfduh-BbVgxr3TDPnDgar7UDNqtWGHzQOM7IhOjetjnsy0JrD37Dd9yO2KLSakKGHxBv-vDjPPBPrs4FCkJIBvuEum3oFSTV0sXM9weuTavnoVdFGsG5G8KeiJvrYp3No8jUVD"





    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
