package com.example.monikers;

import com.google.firebase.database.ServerValue;

public class User {
    public String displayname;
    public String email;
    public String birthday;
    public Object timestamp;

    public User(String displayname, String email, String birthday) {
        this.displayname = displayname;
        this.email = email;
        this.birthday = birthday;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getBirthday() {
        return birthday;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public User() {

    }
}
