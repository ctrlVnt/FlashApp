package com.ctrlvnt.flashapp.activities

import com.ctrlvnt.flashapp.adapter.CollectionAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ctrlvnt.flashapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ctrlvnt.flashapp.model.Collection
import com.ctrlvnt.flashapp.storage.CollectionJSONFileStorage
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    private lateinit var addsBtn: Button
    private lateinit var recvLocale:RecyclerView
    private lateinit var localList:ArrayList<Collection>
    private lateinit var collectionAdapter: CollectionAdapter

    private lateinit var storageLocal: CollectionJSONFileStorage

    private var i_local = 1

    private lateinit var firstText: TextView

    private lateinit var tutorial: FloatingActionButton

    private lateinit var pulseAnimation: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        localList = ArrayList()

        addsBtn = findViewById(R.id.add_button)
        tutorial = findViewById(R.id.tutorial)
        recvLocale = findViewById(R.id.collection_list)

        collectionAdapter = CollectionAdapter(this,localList)

        recvLocale.layoutManager = LinearLayoutManager(this)

        recvLocale.adapter = collectionAdapter

        storageLocal = CollectionJSONFileStorage(this, "collection")
        if(i_local <= storageLocal.size()) {
            loadJson(storageLocal, localList, i_local)
            i_local = storageLocal.size()
        }

        firstText = findViewById<TextView>(R.id.empty_write)
        if(storageLocal.size() == 0){
            firstText.visibility = VISIBLE
        }else{
            firstText.visibility = INVISIBLE
        }

        addsBtn.setOnClickListener {
            addsBtn.startAnimation(pulseAnimation)
            addInfo(i_local)
        }

        tutorial.setOnClickListener{
            tutorial.startAnimation(pulseAnimation)
            showTutorial()
        }


        collectionAdapter.setonItemClickListener(object : CollectionAdapter.onItemClickListener{
            override fun onItemClick(nameItem: String, tagItem: String) {
                startCollectionActivity(findViewById(R.id.collection_item), nameItem, tagItem)
            }

            override fun onAddClick(position: Int) {

                val sup = LayoutInflater.from(this@MainActivity)
                val it = sup.inflate(R.layout.confirm_delete,null)
                val supDialog = AlertDialog.Builder(this@MainActivity)
                supDialog.setView(it)
                supDialog.setPositiveButton(getString(R.string.yes)){ dialog, _ ->
                    if(storageLocal.size() > 1) {
                        for (k in position + 1 until storageLocal.size()) {
                            storageLocal.update(k, storageLocal.find(k + 1)!!)
                        }
                    }
                    storageLocal.delete(storageLocal.size())
                    localList.removeAt(position)
                    i_local -= 1
                    collectionAdapter.notifyDataSetChanged()
                    if(storageLocal.size() == 0){
                        firstText.visibility = VISIBLE
                    }
                    dialog.dismiss()
                }
                supDialog.setNegativeButton(getString(R.string.not)){ dialog, _->
                    dialog.dismiss()
                }
                supDialog.create()
                supDialog.show()
            }
        })

    }

    private fun addInfo(int: Int) {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.activity_popup_main,null)

        val collectionName = v.findViewById<EditText>(R.id.collect_name)
        val collectionTag = v.findViewById<EditText>(R.id.tag_name)

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val names = collectionName.text.toString()
            var tag = collectionTag.text.toString()
            if(names != "" && dontExist(names)) {
                if(tag == "")
                    tag = "..."
                localList.add(Collection(int, names, tag, 0))
                storageLocal.insert(
                    Collection(
                        int,
                        names,
                        tag,
                        0
                    )
                )

                collectionAdapter.notifyDataSetChanged()
                firstText.visibility = INVISIBLE
                dialog.dismiss()
            }else{
                if(!dontExist(names))
                    Toast.makeText(this,getString(R.string.error_already_exist), Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this,getString(R.string.error_not_empty), Toast.LENGTH_SHORT).show()
            }
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()

        }
        addDialog.create()
        addDialog.show()
    }

    private fun dontExist(name: String) : Boolean{
        var count = localList.size

        for (i in 0 until count) {
            if (localList[i].name == name) {
                return false
            }
        }

        return true
    }

    private fun loadJson(storage: CollectionJSONFileStorage, arraylist:ArrayList<Collection>, fine:Int) {

        for (i in fine until storage.size() + 1) {
            arraylist.add(
                Collection(
                    storage.find(i)!!.id,
                    storage.find(i)!!.name,
                    storage.find(i)!!.tag,
                    storage.find(i)!!.card_number
                )
            )
        }
    }

    private fun showTutorial(){
        val intent = Intent(this, Tutorial::class.java)
        startActivity(intent)
    }

    fun startCollectionActivity (view: View, name : String, tag : String) {
        val intent = Intent(this, CollectionActivity::class.java)

        intent.putExtra("collectionName", name)
        intent.putExtra("collectionTag", tag)
        startActivity(intent)
    }
}