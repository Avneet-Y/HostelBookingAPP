package com.example.hostelappnitj.ModelResponse;

public class RegisterResponse {
    String message ;
    String error;
    String avatar ;
    User user ;
    public RegisterResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public User getUser() {
        return user;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
