Wierd optimization by R8

Hi! 
I stacked with odd behavior in my application in release. I think it is connected with R8 optimization.
I made a sample: 

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

And this code works fine in debug build. But if I built my app with `debuggable false` my Switch does not move.
I looked on my apk and noticed that in case `debuggable false` there is no field sharedPreferencesListener in class PreferenceStorage.
I think it's optimization by R8. It inlines the sharedPreferencesListener field into init block and my apk lost strong reference on listener, so GC collect it.

For repeat this behavior in sample I have to include in proguard-rules keep from my PreferenceStorage
```
-keep class com.example.settingswitcher.PreferenceStorage
```
