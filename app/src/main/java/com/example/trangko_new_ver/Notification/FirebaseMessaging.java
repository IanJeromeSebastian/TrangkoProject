package com.example.trangko_new_ver.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.security.identity.PersonalizationData;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.trangko_new_ver.Chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentUser = sp.getString("Current_USERID","None");


        String sent = message.getData().get("sent");
        String user = message.getData().get("user");
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null && sent.equals(fUser.getUid())){

            if (!savedCurrentUser.equals(user)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    sendOAndAboveNotification(message);

                }else{
                    sendNormalNotification(message);
                }
            }
        }

    }

    private void sendNormalNotification(RemoteMessage message) {
        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");

        RemoteMessage.Notification notification = message.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle= new Bundle();
        bundle.putString("hisUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this ,i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//        .setSmallIcon(Integer.parseInt(icon))
//                .setContentText(body)
//                .setContentTitle(title)
//                .setAutoCancel(true)
//                .setSound(defSoundUri)
//                .setContentIntent(pIntent);

        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
        Notification.Builder builder = notification1.getNotification(title, body, pIntent, defSoundUri, icon);




        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if (i>0){
            j=i;
        }
        notificationManager.notify(j,builder.build());



    }

    private void sendOAndAboveNotification(RemoteMessage message) {
        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");

        RemoteMessage.Notification notification = message.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle= new Bundle();
        bundle.putString("hisUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this ,i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1 =new OreoAndAboveNotification(this);
        Notification.Builder builder = notification1.getNotification(title,body, pIntent, defSoundUri,icon);

        int j=0;
        if (i>0){
            j=i;
        }
        notification1.getManager().notify(j,builder.build());


    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //update user token
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            //signed in, update token
            updateToken(token);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);
    }
}
