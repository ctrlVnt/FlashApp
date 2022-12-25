package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.flashapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CollectionAdapter(val c: Context, private val collList:ArrayList<model.Collection>):RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    private lateinit var mlistener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(nameItem: String, tagItem: String)
        fun onAddClick(position: Int)
    }

    fun setonItemClickListener(listener : onItemClickListener){
        mlistener = listener
    }

    inner class CollectionViewHolder(val v: View, listener: onItemClickListener):RecyclerView.ViewHolder(v){
        var nameCollection:TextView
        var tagCollection:TextView

        init {
            nameCollection = v.findViewById(R.id.collection_name)
            tagCollection = v.findViewById(R.id.collection_tag)
            v.setOnClickListener{
                mlistener.onItemClick(nameCollection.text as String, tagCollection.text as String)
            }
            v.findViewById<FloatingActionButton>(R.id.delete_collection).setOnClickListener {
                mlistener.onAddClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.item_collection,parent,false)
        return CollectionViewHolder(v, mlistener)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val newList = collList[position]
        holder.nameCollection.text = newList.name
        holder.tagCollection.text = "#"+newList.tag
    }

    override fun getItemCount(): Int {
        return  collList.size
    }
}