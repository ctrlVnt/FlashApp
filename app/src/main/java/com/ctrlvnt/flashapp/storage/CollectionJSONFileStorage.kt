package com.ctrlvnt.flashapp.storage

import android.content.Context
import com.ctrlvnt.flashapp.model.Collection
import org.json.JSONObject
import com.ctrlvnt.flashapp.storage.utility.file.JSONFileStorage

class CollectionJSONFileStorage(context: Context, name: String): JSONFileStorage<Collection>(context, name) {
    override fun create(id: Int, obj: Collection): Collection {
        return Collection(obj.id, obj.name,obj.tag,obj.card_number)
    }

    override fun objectToJson(id: Int, obj: Collection): JSONObject {
        var res = JSONObject()
        res.put(Collection.ID,obj.id)
        res.put(Collection.NAME,obj.name)
        res.put(Collection.TAG,obj.tag)
        res.put(Collection.CARDNUMBER,obj.card_number)
        return res
    }

    override fun jsonToObject(json: JSONObject): Collection {
        return Collection(
            json.getInt(Collection.ID),
            json.getString(Collection.NAME),
            json.getString(Collection.TAG),
            json.getInt(Collection.CARDNUMBER),
        )
    }
}