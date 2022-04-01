package com.esoftwere.hfk.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class TinyDB(val context: Context) {
    val gson by lazy { Gson() }
    val sharedPrefs: SharedPreferences = context.getSharedPreferences("${context.packageName}_prefs", Context.MODE_PRIVATE)
    val sharedPrefsEditor: SharedPreferences.Editor = sharedPrefs.edit()

    /* String Related Methods */

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    fun readString(what: String): String {
        return sharedPrefs.getString(what, DEFAULT_STRING_VALUE) ?: ""
    }

    /**
     * @param what
     * @param defaultString
     * @return Returns the stored value of 'what'
     */
    fun readString(what: String, defaultString: String): String {
        return sharedPrefs.getString(what, defaultString) ?: ""
    }

    /**
     * @param where
     * @param what
     */
    fun writeString(where: String, what: String) {
        sharedPrefsEditor.putString(where, what).apply()
    }

    /* Int Related Methods */

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    fun readInt(what: String): Int {
        return sharedPrefs.getInt(what, DEFAULT_INT_VALUE)
    }

    /**
     * @param what
     * @param defaultInt
     * @return Returns the stored value of 'what'
     */
    fun readInt(what: String, defaultInt: Int): Int {
        return sharedPrefs.getInt(what, defaultInt)
    }

    /**
     * @param where
     * @param what
     */
    fun writeInt(where: String, what: Int) {
        sharedPrefsEditor.putInt(where, what).apply()
    }

    /* Double related methods */

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    fun readDouble(what: String): Double {
        return if (!contains(what)) DEFAULT_DOUBLE_VALUE else java.lang.Double.longBitsToDouble(readLong(what))
    }

    /**
     * @param what
     * @param defaultDouble
     * @return Returns the stored value of 'what'
     */
    fun readDouble(what: String, defaultDouble: Double): Double {
        return if (!contains(what)) defaultDouble else java.lang.Double.longBitsToDouble(readLong(what))
    }

    /**
     * @param where
     * @param what
     */
    fun writeDouble(where: String, what: Double) {
        writeLong(where, java.lang.Double.doubleToRawLongBits(what))
    }

    /* Float related methods */

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    fun readFloat(what: String): Float {
        return sharedPrefs.getFloat(what, DEFAULT_FLOAT_VALUE)
    }

    /**
     * @param what
     * @param defaultFloat
     * @return Returns the stored value of 'what'
     */
    fun readFloat(what: String, defaultFloat: Float): Float {
        return sharedPrefs.getFloat(what, defaultFloat)
    }

    /**
     * @param where
     * @param what
     */
    fun writeFloat(where: String, what: Float) {
        sharedPrefsEditor.putFloat(where, what).apply()
    }

    /* Long related methods */

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    fun readLong(what: String): Long {
        return sharedPrefs.getLong(what, DEFAULT_LONG_VALUE)
    }

    /**
     * @param what
     * @param defaultLong
     * @return Returns the stored value of 'what'
     */
    fun readLong(what: String, defaultLong: Long): Long {
        return sharedPrefs.getLong(what, defaultLong)
    }

    /**
     * @param where
     * @param what
     */
    fun writeLong(where: String, what: Long) {
        sharedPrefsEditor.putLong(where, what).apply()
    }

    /* Boolean related methods */

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    fun readBoolean(what: String): Boolean {
        return sharedPrefs.getBoolean(what, DEFAULT_BOOLEAN_VALUE)
    }

    /**
     * @param what
     * @param defaultBoolean
     * @return Returns the stored value of 'what'
     */
    fun readBoolean(what: String, defaultBoolean: Boolean): Boolean {
        return sharedPrefs.getBoolean(what, defaultBoolean)
    }

    /**
     * @param where
     * @param what
     */
    fun writeBoolean(where: String, what: Boolean) {
        sharedPrefsEditor.putBoolean(where, what).apply()
    }

    /**
     * Saves object into the Preferences.
     *
     * @param `what` Object of model class (of type [T]) to save
     * @param where Key with which Shared preferences to
     **/
    fun <T> writeCustomDataObjects(where: String, what: T?) {
        sharedPrefsEditor.putString(where, gson.toJson(what)).apply()
    }

    /**
     * Used to retrieve object from the Preferences.
     *
     * @param key Shared Preference key with which object was saved.
     **/
    inline fun <reified T> getCustomDataObjects(key: String): T? {
        val value = sharedPrefs.getString(key, null)
        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of
        //type Class < T >" is used to cast.
        return gson.fromJson(value, T::class.java)
    }

    /**
     * @param key
     * @return Returns if that key exists
     */
    operator fun contains(key: String): Boolean {
        return sharedPrefs.contains(key)
    }

    /**
     * @param key
     */
    fun remove(key: String) {
        if (contains(key + LENGTH)) {
            // Workaround for pre-HC's lack of StringSets
            val stringSetLength = readInt(key + LENGTH)
            if (stringSetLength >= 0) {
                sharedPrefsEditor.remove(key + LENGTH).apply()
                for (i in 0 until stringSetLength) {
                    sharedPrefsEditor.remove("$key[$i]").apply()
                }
            }
        }

        sharedPrefsEditor.remove(key).apply()
    }

    /**
     * Clear all the preferences
     */
    fun clear() {
        sharedPrefsEditor.clear().apply()
    }

    companion object {
        private const val LENGTH = "_length"
        private const val DEFAULT_STRING_VALUE = ""
        private const val DEFAULT_INT_VALUE = -1
        private const val DEFAULT_DOUBLE_VALUE = -1.0
        private const val DEFAULT_FLOAT_VALUE = -1f
        private const val DEFAULT_LONG_VALUE = -1L
        private const val DEFAULT_BOOLEAN_VALUE = false
    }
}