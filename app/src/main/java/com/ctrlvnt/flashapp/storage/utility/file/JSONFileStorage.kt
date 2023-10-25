package com.ctrlvnt.flashapp.storage.utility.file

import android.content.Context
import org.json.JSONObject

abstract class JSONFileStorage<T>(context: Context, name: String): FileStorage<T>(context,name, extension = "json") {

    protected abstract fun objectToJson(id: Int, obj : T): JSONObject
    protected abstract fun jsonToObject(json: JSONObject): T

    override fun dataToString(data: HashMap<Int, T>): String {
        val json = JSONObject()
        data.forEach{pair -> json.put("${pair.key}", objectToJson(pair.key, pair.value))}
        return json.toString()
    }

    override fun stringToData(value: String): HashMap<Int, T> {
        val data = HashMap<Int, T>()
        val json = JSONObject(value)
        val iterator = json.keys()
        while(iterator.hasNext()){
            val key = iterator.next()
            data[key.toInt()] = jsonToObject(json.getJSONObject(key))
        }
        return data
    }
}