package com.aircraftwar.android.application.datahandle;

import java.io.Serializable;

public class Score implements Serializable {
    public Long uid;
    public String username;
    public int userscore;
    public Score(Long uid,String username,int userscore){
        this.uid = uid;
        this.username = username;
        this.userscore = userscore;
    }
}
