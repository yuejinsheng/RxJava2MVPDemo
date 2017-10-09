package com.jaydenxiao.common.commonutils;

import com.jaydenxiao.common.sharepref.SharePref;

public class TokenUtits {

    /** AccessToken **/
    public static String getToken() {
        return SharePref.getString(SharePref.ACCESS_TOKEN, "");
    }

    public static void saveToken(String access_token) {
        SharePref.saveString(SharePref.ACCESS_TOKEN, access_token);
    }
    
    /** api_uid **/
    public static String getApiUid() {
        return SharePref.getString(SharePref.API_UID, "");
    }

    public static void saveApiUid(String apiUid) {
        SharePref.saveString(SharePref.API_UID, apiUid);
    }

}
