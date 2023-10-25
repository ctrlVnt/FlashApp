package com.ctrlvnt.flashapp.storage

import android.content.Context
import android.content.SharedPreferences
import com.ctrlvnt.flashapp.model.Collection
import com.ctrlvnt.flashapp.storage.utility.Storage

object CollectionStorage {

    private const val STORAGE = "com/ctrlvnt/flashapp/storage"
    const val FILE_JSON = 1
    private const val DEFAULT = 1

    private fun getPreferences(context: Context): SharedPreferences{
        return context.getSharedPreferences("com.btm.info507.collection", Context.MODE_PRIVATE)
    }

    fun getStorage(context: Context): Int {
        return getPreferences(context).getInt(STORAGE, DEFAULT)
    }

    fun setStorage(context: Context) {
        getPreferences(context).edit().putInt(STORAGE, FILE_JSON).apply()
    }

    fun get(context: Context, name: String): Storage<Collection> {
        lateinit var storage: Storage<Collection>
        when (getStorage(context)) {
            FILE_JSON -> storage = CollectionJSONFileStorage(context, name)
        }
        return storage
    }

}