package com.base.app.baseapp.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.base.app.baseapp.utils.Constants.SHARE_PREFERENCE_NAME
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceHelper(private val sharePref: SharedPreferences) {

    companion object {
        const val KEY_IS_FIRST_OPEN = "KEY_IS_FIRST_OPEN"
        const val KEY_IS_SHOW_IMAGE_PERMISSION = "KEY_IS_SHOW_IMAGE_PERMISSION"
        const val KEY_IS_SHOW_CAMERA_PERMISSION = "KEY_IS_SHOW_CAMERA_PERMISSION"
        const val KEY_LANGUAGE_CODE = "KEY_LANGUAGE_CODE"

        @Volatile
        private var _instance: PreferenceHelper? = null

        fun getInstance(applicationContext: Context): PreferenceHelper =
            _instance ?: synchronized(this) {
                _instance ?: PreferenceHelper(
                    applicationContext.getSharedPreferences(
                        SHARE_PREFERENCE_NAME,
                        MODE_PRIVATE
                    )
                ).also { _instance = it }
            }
    }


    fun getPreferences(): SharedPreferences {
        return sharePref
    }

    var languageCode by StringPreference(KEY_LANGUAGE_CODE, "en")

    open class BooleanPreference(
        private val key: String,
        private val defaultValue: Boolean = true,
    ) : ReadWriteProperty<PreferenceHelper, Boolean> {
        override fun getValue(thisRef: PreferenceHelper, property: KProperty<*>): Boolean {
            return thisRef.getValue(key, defaultValue)
        }

        override fun setValue(thisRef: PreferenceHelper, property: KProperty<*>, value: Boolean) {
            thisRef.saveValue(key, value)
        }
    }

    class StringPreference(
        private val key: String,
        private val defaultValue: String = "",
    ) : ReadWriteProperty<PreferenceHelper, String> {
        override fun getValue(thisRef: PreferenceHelper, property: KProperty<*>): String {
            return thisRef.getValue(key, defaultValue)
        }

        override fun setValue(thisRef: PreferenceHelper, property: KProperty<*>, value: String) {
            thisRef.saveValue(key, value)
        }
    }

    inner class LongPreference(
        private val key: String,
        private val defaultValue: Long
    ) : ReadWriteProperty<PreferenceHelper, Long> {
        override fun getValue(thisRef: PreferenceHelper, property: KProperty<*>): Long {
            return sharePref.getLong(key, defaultValue)
        }

        override fun setValue(thisRef: PreferenceHelper, property: KProperty<*>, value: Long) {
            sharePref.edit().putLong(key, value).apply()
        }
    }

    class IntPreference(
        private val key: String,
        private val defaultValue: Int = 0,
    ) : ReadWriteProperty<PreferenceHelper, Int> {
        override fun getValue(thisRef: PreferenceHelper, property: KProperty<*>): Int {
            return thisRef.getValue(key, defaultValue)
        }

        override fun setValue(thisRef: PreferenceHelper, property: KProperty<*>, value: Int) {
            thisRef.saveValue(key, value)
        }
    }

}

fun <T> PreferenceHelper.saveValue(key: String, value: T) {
    val editor = this.getPreferences().edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        is Float -> editor.putFloat(key, value)
        is Boolean -> editor.putBoolean(key, value)
        else -> throw IllegalArgumentException("Unsupported data type")
    }
    editor.apply()
}

inline fun <reified T> PreferenceHelper.getValue(key: String, defaultValue: T): T {
    val prefs = getPreferences()
    @Suppress("UNCHECKED_CAST")
    return when (T::class) {
        String::class -> prefs.getString(key, defaultValue as String) as T
        Int::class -> prefs.getInt(key, defaultValue as Int) as T
        Long::class -> prefs.getLong(key, defaultValue as Long) as T
        Float::class -> prefs.getFloat(key, defaultValue as Float) as T
        Boolean::class -> prefs.getBoolean(key, defaultValue as Boolean) as T

        else -> throw IllegalArgumentException("Unsupported data type")
    }
}



