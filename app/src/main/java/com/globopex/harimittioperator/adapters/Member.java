package com.globopex.harimittioperator.adapters;

/**
 * Created by Afnan on 04-Jun-18.
 */
public class Member {

    private String activity_name;
    private String membership_id;
    private String name;
    private String address;
    private String contact_no;
    private String email;
    private String password;
    private String gcm_reg;
    private String img_url;

    public Member(String membership_id, String name, String address, String contact_no, String email, String password,
                  String gcm_reg, String img_url) {
        this.membership_id = membership_id;
        this.name = name;
        this.address = address;
        this.contact_no = contact_no;
        this.email = email;
        this.password = password;
        this.gcm_reg = gcm_reg;
        this.img_url = img_url;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getMembership_id() {
        return membership_id;
    }

    public void setMembership_id(String membership_id) {
        this.membership_id = membership_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGcm_reg() {
        return gcm_reg;
    }

    public void setGcm_reg(String gcm_reg) {
        this.gcm_reg = gcm_reg;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

}
