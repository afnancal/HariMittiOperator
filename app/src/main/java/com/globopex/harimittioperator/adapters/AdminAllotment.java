package com.globopex.harimittioperator.adapters;

/**
 * Created by Afnan on 05-Jun-18.
 */
public class AdminAllotment {

    private int allotment_id;
    private String maintainer_id;
    private String membership_id;
    private String maintainerName;
    private String memberName;
    private String schedule;
    private String status;

    public AdminAllotment(int allotment_id, String maintainer_id, String membership_id, String maintainerName, String memberName,
                          String schedule, String status) {
        this.allotment_id = allotment_id;
        this.maintainer_id = maintainer_id;
        this.membership_id = membership_id;
        this.maintainerName = maintainerName;
        this.memberName = memberName;
        this.schedule = schedule;
        this.status = status;
    }

    public int getAllotment_id() {
        return allotment_id;
    }

    public String getMaintainer_id() {
        return maintainer_id;
    }

    public String getMembership_id() {
        return membership_id;
    }

    public String getMaintainerName() {
        return maintainerName;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getStatus() {
        return status;
    }

}
