package com.example.trangko_new_ver.Notification;

import com.google.android.gms.tasks.Task;

public class Token {

    String token;

    public Token(Task<String> token) {
    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
