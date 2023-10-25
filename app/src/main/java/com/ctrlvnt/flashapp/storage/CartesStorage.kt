package com.ctrlvnt.flashapp.storage

import android.content.Context
import android.content.SharedPreferences
import com.ctrlvnt.flashapp.model.Cartes
import com.ctrlvnt.flashapp.storage.utility.Storage

object CartesStorage {
    private const val STORAGE = "com/ctrlvnt/flashapp/storage"
    const val FILE_JSON = 1
    private const val DEFAULT = 1

    private fun getPreferences(context: Context): SharedPreferences{
        return context.getSharedPreferences("com.btm.info507.cartes", Context.MODE_PRIVATE)
    }

    fun getStorage(context: Context): Int {
        return getPreferences(context).getInt(STORAGE, DEFAULT)
    }

    fun setStorage(context: Context, prefStorage: Int) {
        getPreferences(context).edit().putInt(STORAGE, prefStorage).apply()
    }

    fun get(context: Context, name :String): Storage<Cartes> {
        lateinit var storage: Storage<Cartes>
        when (getStorage(context)) {
            FILE_JSON -> storage = CartesJSONFileStorage(context, name)
        }
        return storage
    }


}