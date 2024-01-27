package com.example.inventorycountingapp.common.pref

import android.content.Context
import android.content.SharedPreferences
import com.example.inventorycountingapp.login.LoginResponse
import com.google.gson.Gson

object SpManager {
    private const val PREFERENCES_NAME = "SpManager"
    public const val KEY_USER = "user"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(context: Context, loginResponse: LoginResponse.Data) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_USER, Gson().toJson(loginResponse))
        editor.apply()
    }

    fun getUser(context: Context): LoginResponse.Data {
        val json = getSharedPreferences(context).getString(KEY_USER, null)
        return if (json != null) {
            Gson().fromJson(json, LoginResponse.Data::class.java)
        } else {
            LoginResponse.Data()
        }
    }

    fun saveString(context: Context, key: String?, value: String?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String?, defaultValue: String?): String? {
        return getSharedPreferences(context).getString(key, defaultValue)
    }

    fun saveInt(context: Context, key: String?, value: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(context: Context, key: String?, defaultValue: Int): Int {
        return getSharedPreferences(context).getInt(key, defaultValue)
    }
}
