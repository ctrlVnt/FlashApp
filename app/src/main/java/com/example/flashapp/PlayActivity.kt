package com.example.flashapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import androidx.cardview.widget.CardView
import storage.CartesJSONFileStorage

class PlayActivity: AppCompatActivity() {

    private lateinit var storage: CartesJSONFileStorage
    private lateinit var rejectButton: FloatingActionButton
    private lateinit var confrimButton: FloatingActionButton
    private lateinit var roundButton: Button
    private var punteggio = 0
    private var i = 1

    private lateinit var front_animation:AnimatorSet
    private lateinit var back_animation: AnimatorSet
    private var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_play)

        var nameList = intent.getStringExtra("list")!!

        findViewById<TextView>(R.id.collection_text).setText(nameList)

        storage = CartesJSONFileStorage(this, nameList)

        rejectButton = findViewById(R.id.reject)
        confrimButton = findViewById(R.id.confirm)
        roundButton = findViewById(R.id.round)

        rejectButton.visibility = View.INVISIBLE
        confrimButton.visibility = View.INVISIBLE

        start()

    }

    private fun start() {

        var scale = applicationContext.resources.displayMetrics.density
        val front = findViewById<CardView>(R.id.card_item_play) as CardView
        val back = findViewById<CardView>(R.id.card_item_play_back) as CardView

        front.cameraDistance = 8000 * scale
        back.cameraDistance = 8000 * scale

        front_animation = AnimatorInflater.loadAnimator(applicationContext, R.animator.front_animator) as AnimatorSet
        back_animation = AnimatorInflater.loadAnimator(applicationContext, R.animator.back_animator) as AnimatorSet

        if(i < storage.size() + 1) {
            rejectButton.visibility = View.INVISIBLE
            confrimButton.visibility = View.INVISIBLE
            if (storage.find(i)!!.imageq == Uri.EMPTY.toString()) {
                findViewById<TextView>(R.id.card_phrase).setText(storage.find(i)!!.question)
            }else{
                val selectedImage : Uri = Uri.parse(storage.find(i)!!.imageq)
                findViewById<ImageView>(R.id.question_img).setImageURI(selectedImage)
            }

            roundButton.setOnClickListener {
                roundButton.visibility = View.INVISIBLE
                rejectButton.visibility = View.VISIBLE
                confrimButton.visibility = View.VISIBLE

                if (storage.find(i)!!.imager == Uri.EMPTY.toString()) {
                    findViewById<TextView>(R.id.card_phrase_back).setText(storage.find(i)!!.reponse)
                }else{
                    val selectedImage : Uri = Uri.parse(storage.find(i)!!.imager)
                    findViewById<ImageView>(R.id.question_img_back).setImageURI(selectedImage)
                }

                if(isFront)
                {
                    front_animation.setTarget(front)
                    back_animation.setTarget(back)
                    front_animation.start()
                    back_animation.start()
                    isFront = false
                }

                findViewById<ImageView>(R.id.question_img).setImageURI(Uri.EMPTY)
                findViewById<TextView>(R.id.card_phrase).setText("")

                confrimButton.setOnClickListener {
                    punteggio += 1
                    if(!isFront)
                    {
                        front_animation.setTarget(back)
                        back_animation.setTarget(front)
                        back_animation.start()
                        front_animation.start()
                        isFront =true
                    }
                    findViewById<ImageView>(R.id.question_img_back).setImageURI(Uri.EMPTY)
                    findViewById<TextView>(R.id.card_phrase_back).setText("")
                    next()
                }
                rejectButton.setOnClickListener {
                    if(!isFront)
                    {
                        front_animation.setTarget(back)
                        back_animation.setTarget(front)
                        back_animation.start()
                        front_animation.start()
                        isFront =true
                    }
                    findViewById<ImageView>(R.id.question_img_back).setImageURI(Uri.EMPTY)
                    findViewById<TextView>(R.id.card_phrase_back).setText("")
                    next()
                }
            }
        }else{
            findViewById<LinearLayout>(R.id.linearLayout_play).visibility = INVISIBLE
            val inflate = LayoutInflater.from(this)
            val item = inflate.inflate(R.layout.win_item, null)
            item.findViewById<TextView>(R.id.win_text).setText("Résultat: $punteggio")
            val win = AlertDialog.Builder(this)
            win.setView(item)
            win.setCancelable(false)
            win.setPositiveButton("Ok"){ dialog, _ ->
                dialog.dismiss()
                finish()
            }
            win.show()
        }
    }

    private fun next(){
        rejectButton.visibility = View.INVISIBLE
        confrimButton.visibility = View.INVISIBLE
        roundButton.visibility = View.VISIBLE
        i +=1
        start()
    }

}