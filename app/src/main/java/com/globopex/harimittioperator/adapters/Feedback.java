package com.globopex.harimittioperator.adapters;

/**
 * Created by Afnan on 22-Jun-18.
 */
public class Feedback {

    private String maintainer_id;
    private String maintainer_name;
    private String membership_id;
    private String plant_img1;
    private String plant_img2;
    private String plant_img3;
    private String plant_img4;
    private String plant_img5;
    private String description;
    private String audio_file_url;
    private int status;
    private String payment_method;
    private String chk_img_url;
    private String amount;
    private String action_on;

    public Feedback(String maintainer_id, String maintainer_name, String membership_id, String plant_img1, String plant_img2,
                    String plant_img3, String plant_img4, String plant_img5, String description, String audio_file_url, int status,
                    String payment_method, String chk_img_url, String amount, String action_on) {
        this.maintainer_id = maintainer_id;
        this.maintainer_name = maintainer_name;
        this.membership_id = membership_id;
        this.plant_img1 = plant_img1;
        this.plant_img2 = plant_img2;
        this.plant_img3 = plant_img3;
        this.plant_img4 = plant_img4;
        this.plant_img5 = plant_img5;
        this.description = description;
        this.audio_file_url = audio_file_url;
        this.status = status;
        this.payment_method = payment_method;
        this.chk_img_url = chk_img_url;
        this.amount = amount;
        this.action_on = action_on;
    }

    public String getMaintainer_id() {
        return maintainer_id;
    }

    public String getMaintainer_name() {
        return maintainer_name;
    }

    public String getMembership_id() {
        return membership_id;
    }

    public String getPlant_img1() {
        return plant_img1;
    }

    public String getPlant_img2() {
        return plant_img2;
    }

    public String getPlant_img3() {
        return plant_img3;
    }

    public String getPlant_img4() {
        return plant_img4;
    }

    public String getPlant_img5() {
        return plant_img5;
    }

    public String getDescription() {
        return description;
    }

    public String getAudio_file_url() {
        return audio_file_url;
    }

    public int getStatus() {
        return status;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getChk_img_url() {
        return chk_img_url;
    }

    public String getAmount() {
        return amount;
    }

    public String getAction_on() {
        return action_on;
    }

}
