package adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flashapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import model.Cartes

class CartesAdapter(val c: Context, val collList:ArrayList<Cartes>):RecyclerView.Adapter<CartesAdapter.CartesViewHolder>() {

    private lateinit var mlistener : CartesAdapter.onItemClickListener

    interface onItemClickListener{
        fun onItemClick(questionItem: String, reponseItem: String, position: Int)
        fun deleteCardClick(position: Int)
    }

    fun setonItemClickListener(listener : onItemClickListener){
        mlistener = listener
    }

    inner class CartesViewHolder(val v:View, listener : CartesAdapter.onItemClickListener):RecyclerView.ViewHolder(v) {

        var question: TextView
        var reponse: TextView
        val qimagine: ImageView
        val rimagine: ImageView

        init {
            question = v.findViewById(R.id.card_question)
            reponse = v.findViewById(R.id.card_response)
            qimagine = v.findViewById(R.id.imagine_question)
            rimagine = v.findViewById(R.id.imagine_reponse)

            v.setOnClickListener() {
                mlistener.onItemClick(
                    question.text as String,
                    reponse.text as String,
                    adapterPosition
                )
            }
            v.findViewById<FloatingActionButton>(R.id.cart_delete).setOnClickListener {
                mlistener.deleteCardClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): CartesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.item_card,parent,false)
        return CartesViewHolder(v, mlistener)
    }

    override fun onBindViewHolder(holder: CartesViewHolder, position: Int) {
        val newList = collList[position]
        holder.question.text = newList.question
        holder.reponse.text = newList.reponse
        if (newList.imageq != Uri.EMPTY.toString()) {
            val selectedImage: Uri = Uri.parse(newList.imageq)
            holder.qimagine.setImageURI(selectedImage)
        }else{
            holder.qimagine.setImageURI(Uri.EMPTY)
        }
        if (newList.imager != Uri.EMPTY.toString()) {
            val selectedImage: Uri = Uri.parse(newList.imager)
            holder.rimagine.setImageURI(selectedImage)
        }else{
            holder.rimagine.setImageURI(Uri.EMPTY)
        }
    }

    override fun getItemCount(): Int {
        return  collList.size
    }
}