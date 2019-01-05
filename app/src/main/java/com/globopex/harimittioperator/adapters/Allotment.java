package com.globopex.harimittioperator.adapters;

/**
 * Created by Afnan on 02-May-18.
 */
public class Allotment {

    private int allotment_id;
    private String maintainer_id;
    private String membership_id;
    private String name;
    private String address;
    private String contact_no;
    private String email;
    private String img_url;
    private String schedule;
    private String status;

    public Allotment(int allotment_id, String maintainer_id, String membership_id, String name, String address, String contact_no,
                     String email, String img_url, String schedule, String status) {
        this.allotment_id = allotment_id;
        this.maintainer_id = maintainer_id;
        this.membership_id = membership_id;
        this.name = name;
        this.address = address;
        this.contact_no = contact_no;
        this.email = email;
        this.img_url = img_url;
        this.schedule = schedule;
        this.status = status;
    }

    public int getAllotment_id() {
        return allotment_id;
    }

    public void setAllotment_id(int allotment_id) {
        this.allotment_id = allotment_id;
    }

    public String getMaintainer_id() {
        return maintainer_id;
    }

    public void setMaintainer_id(String maintainer_id) {
        this.maintainer_id = maintainer_id;
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

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
