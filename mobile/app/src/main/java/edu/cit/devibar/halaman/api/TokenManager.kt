package edu.cit.devibar.halaman.api

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "halaman_prefs"
    private const val ACCESS_TOKEN = "access_token"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(ACCESS_TOKEN, null)
    }

    fun saveToken(context: Context, token: String) {
        getPrefs(context).edit().putString(ACCESS_TOKEN, token).apply()
    }

    fun clearToken(context: Context) {
        getPrefs(context).edit().remove(ACCESS_TOKEN).apply()
    }
}