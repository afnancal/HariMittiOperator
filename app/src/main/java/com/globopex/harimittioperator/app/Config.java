package com.globopex.harimittioperator.app;

/**
 * Created by Afnan on 30-Mar-17.
 */

public class Config {

    // For Firebase Notification
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    //public static final String SHARED_PREF = "ah_firebase";

    // server URL configuration
    // HariMitti Maintainer/Admin Login url
    public static final String LoginUrl = "loginMaintainer";
    // HariMitti get Maintainers Deatail url
    public static final String MaintainersDeatailUrl = "maintainerList";
    // HariMitti Search Maintainer By Id url
    public static final String SearchMaintainerByIdUrl = "searchMaintainerById/";
    // HariMitti Search Maintainer By Name url
    public static final String SearchMaintainerByNameUrl = "searchMaintainerByName/";
    // HariMitti Create Maintainer url
    public static final String CreateMaintainerUrl = "maintainer";
    // HariMitti Update Maintainer By Id url
    public static final String UpdateMaintainerUrl = "updateMaintainer/";

    // HariMitti get Members Deatail url
    public static final String MembersDeatailUrl = "memberList";
    // HariMitti Search Member By Id url
    public static final String SearchMemberByIdUrl = "searchMemberById/";
    // HariMitti Search Member By Name url
    public static final String SearchMemberByNameUrl = "searchMemberByName/";
    // HariMitti Create Member url
    public static final String CreateMemberUrl = "member";
    // HariMitti Update Member By Id url
    public static final String UpdateMemberUrl = "updateMember/";

    // HariMitti get Admins Deatail url
    public static final String AdminsDeatailUrl = "adminList";
    // HariMitti Search Admin By Id url
    public static final String SearchAdminByIdUrl = "searchAdminById/";
    // HariMitti Search Admin By Name url
    public static final String SearchAdminByNameUrl = "searchAdminByName/";
    // HariMitti Create Admin url
    public static final String CreateAdminUrl = "admin";
    // HariMitti Update Admin By Id url
    public static final String UpdateAdminUrl = "updateAdmin/";

    // HariMitti Search Allotment List By Maintainer Id url
    public static final String AllotmentListUrl = "searchMaintainerAllotmentMain/";
    // HariMitti Search Previous Maintainer Allotment url
    public static final String PreviousMaintainerAllotmentUrl = "searchPreviousMaintainerAllotmentMain/";
    // HariMitti Search Today Maintainer Allotment url
    public static final String TodayMaintainerAllotmentUrl = "searchTodaysMaintainerAllotmentMain/";
    // HariMitti Search Coming Maintainer Allotment url
    public static final String ComingMaintainerAllotmentUrl = "searchComingMaintainerAllotmentMain/";
    // HariMitti Create Maintainer Allotment url
    public static final String CreateMaintainerAllotmentUrl = "maintainerAllotment";
    // HariMitti Update Maintainer Allotment url
    public static final String UpdateMaintainersAllotmentUrl = "updateMaintainerAllotment/";


    // HariMitti Search Feedback By MemberId Url
    public static final String SearchFeedbackByMemberIdUrl = "searchFeedbackByMemberId/";
    // HariMitti Search Feedback By Maintainer, Member Id & Date Url
    public static final String SearchFeedbackByMainMembIdDate = "searchFeedByMainMembIdDate/";
    // HariMitti Create Feedback Data Url
    public static final String FeedbackDataUrl = "feedback";

    // HariMitti Search Maintainer Location Url
    public static final String SearchMaintainerLocationUrl = "searchMaintainerLocation/";
    // HariMitti Location Data Url
    public static final String LocationDataUrl = "maintainerLocation";
    // HariMitti Update Create Location Data Url
    public static final String UpdateCreateLocationDataUrl = "updateMaintainerLocation/";
    // HariMitti Database backup file send to email id
    public static final String DatabaseBackup = "databaseBackup/";

    // Plant pic upload url
    public static final String PlantPicUploadUrl = "http://harimitti.com/uploads/plantImageUpload.php";
    // Profile pic upload url
    public static final String ProfilePicUploadUrl = "http://harimitti.com/uploads/profilePicsUpload.php";
    // Cheque pic upload url
    public static final String ChequePicUploadUrl = "http://harimitti.com/uploads/chequeImageUpload.php";
    // Audio File upload url
    public static final String AudioFileUploadUrl = "http://harimitti.com/uploads/audioFilesUpload.php";

}
