package tgo1014.instabox.common.managers

import android.content.SharedPreferences
import androidx.core.content.edit

class UserManagerImpl(private val sharedPreferences: SharedPreferences) : UserManager {

    override val isUserLogged: Boolean
        get() = sharedPreferences.getString(PREF_TOKEN, null) != null

    override var token: String
        get() = sharedPreferences.getString(PREF_TOKEN, null) ?: ""
        set(value) = sharedPreferences.edit { putString(PREF_TOKEN, value) }

    override var userId: String
        get() = sharedPreferences.getString(PREF_USERID, null) ?: ""
        set(value) = sharedPreferences.edit { putString(PREF_USERID, value) }

    override var sessionId: String
        get() = sharedPreferences.getString(PREF_SESSIONID, null) ?: ""
        set(value) = sharedPreferences.edit { putString(PREF_SESSIONID, value) }


    override fun getFormattedUserAgent() = "ds_user_id=$userId; sessionid=$sessionId;"

    companion object {
        const val PREF_TOKEN = "PREF_TOKEN"
        const val PREF_USERID = "PREF_USERID"
        const val PREF_SESSIONID = "PREF_SESSIONID"
    }

}