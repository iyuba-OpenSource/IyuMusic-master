package com.iyuba.music.event;

public class LoginEvent {
    public String username;
    public String userId;
    public LoginEvent(String pp, String id) {
        username = pp;
        userId = id;
    }
}
