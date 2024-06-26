package com.adv.ilook.model.util.assets

/**SharedPref */
object SharedPrefKey{
    const val APP_LANGUAGE:String = "APP_LANGUAGE"
    const val APP_WEBLINK:String = "APP_WEBLINK"
    const val APP_USERID:String = "APP_USERID"
    const val APP_USERMAIL:String = "APP_USERMAIL"
    const val APP_USERLOGIN:String = "APP_USERLOGIN"
    const val APP_USERNAME:String = "APP_USERNAME"
    const val APP_USERPHONE:String = "APP_USERPHONE"
    const val APP_SELECTED_ROOM:String = "APP_SELECTED_ROOM"
    const val APP_SELECTED_DEVICES:String = "APP_SELECTED_DEVICES"
    const val APP_AVAILABLE_ROOMS:String = "APP_AVAILABLE_ROOMS"
    const val APP_AVAILABLE_DEVICES:String = "APP_AVAILABLE_DEVICES"
    const val APP_ADV_WORKFLOW:String = "APP_ADV_WORKFLOW"
    const val APP_CONFIGURE_DEVICES:String = "APP_CONFIGURE_DEVICES"
    const val APP_SWITCH_STATUS:String = "APP_SWITCH_STATUS"
    const val APP_AVAILABLE_ROOMS_DOWNLOAD:String = "APP_AVAILABLE_ROOMS_DOWNLOAD"
}

object IntentKeys {}
object BundleKeys {

    const val USER_NAME_KEY:String = "USER_NAME_KEY"
    const val USER_PHONE_KEY:String = "USER_PHONE_KEY"
    const val LOGIN_OTP_KEY:String = "LOGIN_OTP_KEY"
}

object FirebaseKeys {
    const val user_name:String = "user_name"
    const val status:String = "status"
    const val call_event:String = "call_event"
    const val contacts:String = "contacts"
    const val latest_event:String = "latest_event"
    const val login_status:String = "login_status"
    const val workflow:String = "workflow"
}
enum class UserStatus {
 ONLINE,OFFLINE,UNREGISTER,IN_CALL
}