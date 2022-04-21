package com.example.settingswitcher

import android.content.Context
import android.content.SharedPreferences

class PreferenceStorage(context: Context) {

    var preferencesChangedCallback: (() -> Unit)? = null

    var myPref: Boolean
        get() = sharedPreferences.getBoolean("my_pref", false)
        set(value) {
            sharedPreferences.edit().putBoolean("my_pref", value).apply()
        }

    private val sharedPreferences = context.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

    // we need strong reference for listener
    private val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            preferencesChangedCallback?.invoke()
        }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }
}
