package storage

import android.content.Context
import android.content.SharedPreferences
import model.Collection
import storage.utility.Storage

object CollectionStorage {

    private const val STORAGE = "storage"
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