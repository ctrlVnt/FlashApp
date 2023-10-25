package com.ctrlvnt.flashapp.activities

import com.ctrlvnt.flashapp.adapter.CartesAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ctrlvnt.flashapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ctrlvnt.flashapp.model.Cartes
import com.ctrlvnt.flashapp.storage.CartesJSONFileStorage

class CollectionActivity : AppCompatActivity() {

    private lateinit var addsBtn: FloatingActionButton
    private lateinit var shuffle: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var cartesList: ArrayList<Cartes>
    private lateinit var cartesAdapter: CartesAdapter

    private var isReadPermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var storageCart: CartesJSONFileStorage

    private lateinit var galleryActivityLauncherQuestion: ActivityResultLauncher<Intent>
    private lateinit var galleryActivityLauncherReponse: ActivityResultLauncher<Intent>

    private lateinit var uriq: Uri
    private lateinit var urir: Uri

    private var i_local = 1

    private lateinit var nameCollection: String

    private lateinit var pulseAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_collection)

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation)

        var shufflePlay: Boolean
        shufflePlay = false;

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
            isReadPermissionGranted = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
        }

        addsBtn = findViewById(R.id.add_button)
        shuffle = findViewById(R.id.shuffle)

        findViewById<TextView>(R.id.collection_name).setText(intent.getStringExtra("collectionName"))
        findViewById<TextView>(R.id.tag).setText(intent.getStringExtra("collectionTag"))

        nameCollection = intent.getStringExtra("collectionName")!!

        cartesList = ArrayList()

        recv = findViewById(R.id.cartes_list)

        cartesAdapter = CartesAdapter(this, cartesList)
        recv.layoutManager = GridLayoutManager(this,3,RecyclerView.VERTICAL,false)
        recv.adapter = cartesAdapter

        uriq = Uri.EMPTY
        galleryActivityLauncherQuestion = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uriq = data?.data!!
                contentResolver.takePersistableUriPermission(uriq, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        urir = Uri.EMPTY
        galleryActivityLauncherReponse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                urir = data?.data!!
                contentResolver.takePersistableUriPermission(urir, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        storageCart = CartesJSONFileStorage(this, nameCollection)
        if(i_local <= storageCart.size()) {
            loadJson(storageCart, cartesList, i_local)
            i_local = storageCart.size()
        }
        findViewById<TextView>(R.id.nCartes).setText(getString(R.string.card_number) + " " + storageCart.size().toString())

        addsBtn.setOnClickListener {
            addsBtn.startAnimation(pulseAnimation)
            addCard(storageCart.size())
        }

        findViewById<Button>(R.id.play_button).setOnClickListener{
            findViewById<Button>(R.id.play_button).startAnimation(pulseAnimation)
            if(storageCart.size() == 0){
                val infltererror = LayoutInflater.from(this)
                val itemerror = infltererror.inflate(R.layout.play_error, null)
                val error = AlertDialog.Builder(this)
                error.setView(itemerror)
                error.setPositiveButton("Ok"){ dialog, _ ->
                    dialog.dismiss()
                }
                error.show()
            }else {
                startPlayActivity(nameCollection, shufflePlay)
            }
        }

        shuffle.setOnClickListener{
            shuffle.startAnimation(pulseAnimation)
            shufflePlay = !shufflePlay

            /*val default = ContextCompat.getColor(this, R.color.red)
            val on = ContextCompat.getColor(this, R.color.accent)

            if(shufflePlay){
                shuffle.setBackgroundColor(on)
            }else{
                shuffle.setBackgroundColor(default)
            }*/

            setButtonBackgroundTint(shuffle, shufflePlay)
        }

        cartesAdapter.setonItemClickListener(object : CartesAdapter.onItemClickListener{
            override fun onItemClick(questionItem: String, responseItem: String, position: Int) {
                editCard(questionItem, responseItem, position)
            }

            override fun deleteCardClick(position: Int) {

                val sup = LayoutInflater.from(this@CollectionActivity)
                val it = sup.inflate(R.layout.confirm_delete,null)
                val supDialog = AlertDialog.Builder(this@CollectionActivity)
                supDialog.setView(it)
                supDialog.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    for (k in position + 1 until storageCart.size()) {
                        storageCart.update(k, storageCart.find(k + 1)!!)
                    }
                    findViewById<TextView>(R.id.nCartes).setText(getString(R.string.card_number) + " " + storageCart.size().toString())
                    storageCart.delete(storageCart.size())
                    cartesList.removeAt(position)
                    cartesAdapter.notifyDataSetChanged()
                }
                supDialog.setNegativeButton(getString(R.string.not)){ dialog, _->
                    dialog.dismiss()
                }
                supDialog.create()
                supDialog.show()
            }
        })
    }

    private fun addCard(count:Int) {
        val inflter = LayoutInflater.from(this)
        val item = inflter.inflate(R.layout.layout_add_edit_card, null)
        checkPermission()

        val cardQuestion = item.findViewById<EditText>(R.id.edit_question)
        val cardAnswer = item.findViewById<EditText>(R.id.edit_answer)


        val addDialog = AlertDialog.Builder(this, R.style.MyDialogTheme)
            .setView(item)
            .setPositiveButton("Ok") {
                dialog, _ ->
                val question = cardQuestion.text.toString()
                val response = cardAnswer.text.toString()
                if((question != "" && uriq.toString() == Uri.EMPTY.toString() || question == "" && uriq.toString() != Uri.EMPTY.toString()) && (response != "" && urir.toString() == Uri.EMPTY.toString() || response == "" && urir.toString() != Uri.EMPTY.toString())) {
                    cartesList.add(Cartes(count, nameCollection, question, response, uriq.toString(), urir.toString()))
                    storageCart.insert(
                        Cartes(
                            count,
                            nameCollection,
                            question,
                            response,
                            uriq.toString(),
                            urir.toString()
                        )
                    )
                    cartesAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }else{
                    if(uriq.toString() == Uri.EMPTY.toString() && cardQuestion.visibility == INVISIBLE)
                        Toast.makeText(this,getString(R.string.error_img_upload), Toast.LENGTH_SHORT).show()
                    else if (urir.toString() == Uri.EMPTY.toString() && cardQuestion.visibility == INVISIBLE)
                        Toast.makeText(this,getString(R.string.error_img_upload), Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this,getString(R.string.error_img_notext), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            }
            .show()

        addDialog.findViewById<FloatingActionButton>(R.id.button_image_question)!!.setOnClickListener {
            if (isReadPermissionGranted) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                cardQuestion.setText("")
                cardQuestion.visibility = INVISIBLE
                galleryActivityLauncherQuestion.launch(intent)
                item.findViewById<ImageView>(R.id.image_view_question)
                    .setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_upload))
            }
        }
        addDialog.findViewById<FloatingActionButton>(R.id.button_image_reponse)!!.setOnClickListener {
            if (isReadPermissionGranted) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                cardAnswer.setText("")
                cardAnswer.visibility = INVISIBLE
                galleryActivityLauncherReponse.launch(intent)
                item.findViewById<ImageView>(R.id.image_view_reponse).setImageDrawable(ContextCompat.getDrawable(
                    applicationContext, R.drawable.ic_upload
                ))
            }
        }

        addDialog.setOnDismissListener {
            uriq = Uri.EMPTY
            urir = Uri.EMPTY
            findViewById<TextView>(R.id.nCartes).setText(getString(R.string.card_number) + " " + storageCart.size().toString())
        }
    }

    private fun editCard(questionItem: String, responseItem: String, position: Int) {
        val inflter = LayoutInflater.from(this)
        val item = inflter.inflate(R.layout.layout_add_edit_card, null)
        checkPermission()

        val cardQuestion = item.findViewById<EditText>(R.id.edit_question)
        val cardAnswer = item.findViewById<EditText>(R.id.edit_answer)
        val cardImageQ = item.findViewById<ImageView>(R.id.image_view_question)
        val cardImageR = item.findViewById<ImageView>(R.id.image_view_reponse)

        cardQuestion.setText(questionItem)
        cardAnswer.setText(responseItem)
        cardImageQ.setImageURI(Uri.EMPTY)
        cardImageR.setImageURI(Uri.EMPTY)

        if(storageCart.find(position + 1)!!.imageq != Uri.EMPTY.toString()){
            cardQuestion.visibility = INVISIBLE
            cardImageQ.setImageURI(Uri.parse(storageCart.find(position+1)!!.imageq))
        }

        if(storageCart.find(position + 1)!!.imager != Uri.EMPTY.toString()){
            cardAnswer.visibility = INVISIBLE
            cardImageR.setImageURI(Uri.parse(storageCart.find(position+1)!!.imager))
        }

        val addDialog = AlertDialog.Builder(this, R.style.MyDialogTheme)
            .setView(item)
            .setPositiveButton("Ok") {
                    dialog, _ ->
                val question = cardQuestion.text.toString()
                val response = cardAnswer.text.toString()
                val imageq = storageCart.find(position+1)!!.imageq
                val imager = storageCart.find(position+1)!!.imager
                if(uriq.toString() != Uri.EMPTY.toString() || urir.toString() != Uri.EMPTY.toString()){
                    if (uriq.toString() != Uri.EMPTY.toString() && urir.toString() != Uri.EMPTY.toString()) {
                        cartesList[position] =
                            Cartes(
                                position,
                                nameCollection,
                                question,
                                response,
                                uriq.toString(),
                                urir.toString()
                            )
                        storageCart.update(
                            position + 1,
                            Cartes(
                                position,
                                nameCollection,
                                question,
                                response,
                                uriq.toString(),
                                urir.toString()
                            )
                        )
                        cartesAdapter.notifyDataSetChanged()
                        dialog.dismiss()

                    }else if(uriq.toString() != Uri.EMPTY.toString() && urir.toString() == Uri.EMPTY.toString()){
                        cartesList[position] =
                            Cartes(
                                position,
                                nameCollection,
                                question,
                                response,
                                uriq.toString(),
                                imager
                            )
                        storageCart.update(
                            position + 1,
                            Cartes(
                                position,
                                nameCollection,
                                question,
                                response,
                                uriq.toString(),
                                imager
                            )
                        )
                        cartesAdapter.notifyDataSetChanged()
                        dialog.dismiss()

                    }else{
                        cartesList[position] =
                            Cartes(
                                position,
                                nameCollection,
                                question,
                                response,
                                imageq,
                                urir.toString()
                            )
                        storageCart.update(
                            position + 1,
                            Cartes(
                                position,
                                nameCollection,
                                question,
                                response,
                                imageq,
                                urir.toString()
                            )
                        )
                        cartesAdapter.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                }else {
                    cartesList[position] =
                        Cartes(position, nameCollection, question, response, imageq, imager)
                    storageCart.update(
                        position + 1,
                        Cartes(
                            position,
                            nameCollection,
                            question,
                            response,
                            imageq,
                            imager
                        )
                    )
                    cartesAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        addDialog.findViewById<FloatingActionButton>(R.id.button_image_question)!!.setOnClickListener {
            if (isReadPermissionGranted) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                galleryActivityLauncherQuestion.launch(intent)
            }
        }

        addDialog.findViewById<FloatingActionButton>(R.id.button_image_reponse)!!.setOnClickListener {
            if (isReadPermissionGranted) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                galleryActivityLauncherReponse.launch(intent)
            }
        }

        addDialog.setOnDismissListener {
            uriq = Uri.EMPTY
            urir = Uri.EMPTY
        }
    }

    private fun loadJson(storage: CartesJSONFileStorage, arraylist:ArrayList<Cartes>, fine:Int) {
        for (i in fine until storage.size() + 1) {
            arraylist.add(
                Cartes(
                    storage.find(i)!!.id,
                    "collection: ${storage.find(i)!!.collection}",
                    storage.find(i)!!.question,
                    storage.find(i)!!.reponse,
                    storage.find(i)!!.imageq,
                    storage.find(i)!!.imager
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission(){
        val isReadPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        isReadPermissionGranted = isReadPermission

        val permissionRequest = mutableListOf<String>()
        if (!isReadPermissionGranted){
            permissionRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private fun setButtonBackgroundTint(button: FloatingActionButton, t: Boolean) {
        if (t) {
            button.backgroundTintList =  ContextCompat.getColorStateList(button.context,
                R.color.yellow
            )
        } else {
            button.backgroundTintList =  ContextCompat.getColorStateList(button.context,
                R.color.accent
            )
        }
    }

    private fun startPlayActivity (list: String, shufflePlay: Boolean) {
        val intent = Intent(this, PlayActivity::class.java)
        intent.putExtra("shuffle", shufflePlay)
        intent.putExtra("list", list)
        intent.putExtra("numberOfCards", storageCart.size().toString())
        startActivity(intent)
    }
}