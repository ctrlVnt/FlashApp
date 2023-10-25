package com.ctrlvnt.flashapp.activities

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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.cardview.widget.CardView
import com.ctrlvnt.flashapp.R
import com.ctrlvnt.flashapp.storage.CartesJSONFileStorage
import kotlin.random.Random

class PlayActivity: AppCompatActivity() {

    private lateinit var storage: CartesJSONFileStorage
    private lateinit var rejectButton: FloatingActionButton
    private lateinit var confrimButton: FloatingActionButton
    private lateinit var roundButton: Button
    private var punteggio = 0
    private var i = 0
    private var shuffle : Boolean = false

    private lateinit var numeriPossibili : MutableList<Int>

    private lateinit var front_animation:AnimatorSet
    private lateinit var back_animation: AnimatorSet
    private var isFront = true

    private lateinit var pulseAnimation: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_play)

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation)

        val nameList = intent.getStringExtra("list")!!
        val tot = intent.getStringExtra("numberOfCards")!!
        shuffle = intent.getBooleanExtra("shuffle", false)

        findViewById<TextView>(R.id.collection_text).setText(nameList)

        storage = CartesJSONFileStorage(this, nameList)

        numeriPossibili = mutableListOf<Int>()
        for( n in 1 until  storage.size() + 2){
            numeriPossibili.add(n)
        }

        if(!shuffle) {
            i = numeriPossibili.removeAt(0)
        }else{
            val remove = Random.nextInt(numeriPossibili.size - 1)
            i = numeriPossibili.removeAt(remove)
        }

        rejectButton = findViewById(R.id.reject)
        confrimButton = findViewById(R.id.confirm)
        roundButton = findViewById(R.id.round)

        rejectButton.visibility = View.INVISIBLE
        confrimButton.visibility = View.INVISIBLE

        start(tot)
    }

    private fun start(tot: String) {

        var scale = applicationContext.resources.displayMetrics.density
        val front = findViewById<CardView>(R.id.card_item_play) as CardView
        val back = findViewById<CardView>(R.id.card_item_play_back) as CardView

        front.cameraDistance = 8000 * scale
        back.cameraDistance = 8000 * scale

        front_animation = AnimatorInflater.loadAnimator(applicationContext,
            R.animator.front_animator
        ) as AnimatorSet
        back_animation = AnimatorInflater.loadAnimator(applicationContext, R.animator.back_animator) as AnimatorSet

        if(numeriPossibili.isNotEmpty()) {
            rejectButton.visibility = View.INVISIBLE
            confrimButton.visibility = View.INVISIBLE
            if (storage.find(i)!!.imageq == Uri.EMPTY.toString()) {
                findViewById<TextView>(R.id.card_phrase).setText(storage.find(i)!!.question)
            }else{
                val selectedImage : Uri = Uri.parse(storage.find(i)!!.imageq)
                findViewById<ImageView>(R.id.question_img).setImageURI(selectedImage)
            }

            roundButton.setOnClickListener {
                roundButton.startAnimation(pulseAnimation)
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
                    confrimButton.startAnimation(pulseAnimation)
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
                    next(tot)
                }
                rejectButton.setOnClickListener {
                    rejectButton.startAnimation(pulseAnimation)
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
                    next(tot)
                }
            }
        }else{
            findViewById<LinearLayout>(R.id.linearLayout_play).visibility = INVISIBLE
            val inflate = LayoutInflater.from(this)
            val item = inflate.inflate(R.layout.win_item, null)
            item.findViewById<TextView>(R.id.win_text).setText( getString(R.string.win) + " $punteggio / $tot")
            if(punteggio < tot.toInt() / 2 || punteggio == 0){
                item.findViewById<ImageView>(R.id.good).visibility = View.GONE
            }else{
                item.findViewById<ImageView>(R.id.notgood).visibility = View.GONE
            }
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

    private fun next(tot: String){
        rejectButton.visibility = View.INVISIBLE
        confrimButton.visibility = View.INVISIBLE
        roundButton.visibility = View.VISIBLE
        if (shuffle){
            val remove : Int
            if(numeriPossibili.size > 1){
                remove = Random.nextInt(numeriPossibili.size - 1)
            }else{
                remove = 0
            }
            i = numeriPossibili.removeAt(remove)
        }else{
            i = numeriPossibili.removeAt(0)
        }
        start(tot)
    }

}