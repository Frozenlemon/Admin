package com.company.admin.model;

public class Staff {

    private String name;
    private String role;
    private String email;
    private String phone;
    private int activeJob;
    private String photo;

    public Staff() {
    }

    public Staff(String name, String role, String email, String phone, int activeJob, String photo) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.activeJob = activeJob;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getActiveJob() {
        return activeJob;
    }

    public void setActiveJob(int activeJob) {
        this.activeJob = activeJob;
    }

    public void setActiveJob(){
        this.activeJob = 0;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
