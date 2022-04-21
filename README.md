Wierd optimization by R8

Hi! 
I stacked with odd behavior in my application in release. I suppose it's connected with R8 optimization.
See my sample below: 


app/build.gradle:
```
...
buildTypes {
        debug {
            shrinkResources true
            minifyEnabled true
            debuggable true
        }
    }
...
```

PreferenceStorage.kt:
```
class PreferenceStorage(context: Context) {

    var preferencesChangedCallback: (() -> Unit)? = null

    var myPref: Boolean
        get() = sharedPreferences.getBoolean("my_pref", false)
        set(value) {
            sharedPreferences.edit().putBoolean("my_pref", value).apply()
        }

    private val sharedPreferences = context.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

    // we need strong reference for the listener
    private val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            preferencesChangedCallback?.invoke()
        }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }
}
```

MainActivity.kt:
```
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
```

And this code works fine in debug build. But if I build my app with `debuggable false` my Switch does not move.
I looked on my apk and noticed that in case `debuggable false` there is no field sharedPreferencesListener in class PreferenceStorage.
I suppose this happens due to optimization by R8. It inlines the sharedPreferencesListener field into init block and my apk lost strong reference on listener, so GC collect it.

For repeat this behavior in sample I have to include in proguard-rules `keep` for my PreferenceStorage
```
-keep class com.example.settingswitcher.PreferenceStorage
```

Links:
[video](https://disk.yandex.ru/d/uQdIqi3YHJztGQ)

[apk with debuggable false](https://disk.yandex.ru/d/MY66dRN7vDroug)

[apk with debuggable true](https://disk.yandex.ru/d/fQhTVCz0phY03Q)

![PrefeenceStorage in dex. debuggable false](https://user-images.githubusercontent.com/4678187/164457000-33e13ff5-8a54-4a9e-a613-8bcdd1abb7f4.png)

![PrefeenceStorage in dex. debuggable true](https://user-images.githubusercontent.com/4678187/164457621-324ea6dc-c9dd-4089-8c9f-810fde825f06.png)

