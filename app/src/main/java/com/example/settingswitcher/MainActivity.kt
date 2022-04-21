package com.example.settingswitcher

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferenceStorage = PreferenceStorage(this)
        preferenceStorage.preferencesChangedCallback = {
            findViewById<Switch>(R.id.switch_preference).isChecked = preferenceStorage.myPref
        }

        findViewById<Button>(R.id.toggle_preference).setOnClickListener {
            preferenceStorage.myPref = !preferenceStorage.myPref
        }
    }
}
