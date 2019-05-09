package com.phone.step.dataMeter;

import java.util.Map;

public class UserHandler {

    private static UserHandler instance = null;
    private Map<String,String> userData = null;
    private boolean status = false;
    private String id = null;
    private String psw = null; //todo think about something else

    public static UserHandler getInstance(){
        if(instance == null){
            instance = new UserHandler();
        }
        return instance;
    }

    private UserHandler(){
    }

    public Map<String, String> getUserData() {
        return userData;
    }

    public void setUserData(Map<String, String> userData) {
        this.userData = userData;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

}
