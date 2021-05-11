package com.example.metronome

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.metronome.knob.RotaryKnobView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), RotaryKnobView.RotaryKnobListener {

    var timeSignature : Int = 0
    var realTimeSignature : Int = 0
    var bitsPerMinute :Int = 0
    var timeInterval : Long = 0
    var playSound = false

    private val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    private lateinit var jobTimer : Job
    private lateinit var scopeTimer: CoroutineScope
    private lateinit var scopeDraw: CoroutineScope
    private var imageList = ArrayList<ImageView>()
    private var lighted = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeSignature = resources.getInteger(R.integer.seekBarStart)
        realTimeSignature  = resources.getInteger(R.integer.seekBarStart)
        bitsPerMinute = resources.getInteger(R.integer.BPMStart)
        timeInterval = (60000 / bitsPerMinute).toLong()
        textBPM.text = "$bitsPerMinute BPS"
        textMin.text = resources.getInteger(R.integer.seekBarMin).toString()
        textMax.text = resources.getInteger(R.integer.seekBarMax).toString()
        textTimeSignature.text = resources.getInteger(R.integer.seekBarStart).toString()

        knob.listener = this

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textTimeSignature.text = progress.toString()
                timeSignature = progress

                scopeDraw = CoroutineScope(Dispatchers.Main)
                scopeDraw.launch {
                    draw()
                }

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        for(i in 1..2){
            val imageView = ImageView(this)
            imageList.add(imageView)
            imageView.setImageResource(R.drawable.sound_view)
            rowFirst.addView(imageView)
        }
        for(i in 1..2){
            val imageView = ImageView(this)
            imageList.add(imageView)
            imageView.setImageResource(R.drawable.sound_view)
            rowSecond.addView(imageView)
        }

    }


    override fun onRotate(value: Int) {
        textBPM.text = "$value BPS"
        bitsPerMinute = value
        Log.e("value",  value.toString())
        timeInterval = (60000 / value).toLong()
        Log.e("timeInterval",  timeInterval.toString())
    }

    private suspend fun timer() {
        var stamp: Long
        while(true) {

            stamp = System.currentTimeMillis()
            //Log.e("timer", Timestamp(System.currentTimeMillis()).toString())
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 100)

            light()

            delay(timeInterval - System.currentTimeMillis() + stamp)
        }
    }

    private suspend fun light(){


        imageList[lighted].setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.soundActive), android.graphics.PorterDuff.Mode.SRC_IN)
        delay(100)
        imageList[lighted].setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.soundNoActive), android.graphics.PorterDuff.Mode.SRC_IN)


        lighted = (lighted + 1)%realTimeSignature
    }

    private suspend fun draw(){
        if(this::jobTimer.isInitialized) {
            jobTimer.join()
        }
        Log.e("timeSignature", timeSignature.toString())
        realTimeSignature = timeSignature


        while (timeSignature > imageList.size){
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.sound_view)
            if(imageList.size%2 == 1){
                imageList.add(imageView)
                rowSecond.addView(imageView)
            } else{
                imageList.add(imageList.size / 2 ,imageView)
                rowFirst.addView(imageView)
            }
        }

        while (timeSignature < imageList.size){
            if(imageList.size%2 == 1){
                rowFirst.removeView(imageList[imageList.size / 2])
                imageList.removeAt(imageList.size / 2)
            } else{
                rowSecond.removeView(imageList.last())
                imageList.removeAt(imageList.lastIndex)
            }
        }

    }

    fun playSound(view: View) {
        if(playSound){
            playSound = false
            jobTimer.cancel()

            for(image in imageList) {
                image.setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.soundNoActive), android.graphics.PorterDuff.Mode.SRC_IN)
            }

            playButton.setImageResource(R.drawable.play_arrow)
            //Log.e("scopeTimer", scopeTimer.toString())
        }
        else{
            lighted = 0
            playSound = true
            jobTimer = Job()
            scopeTimer = CoroutineScope(Dispatchers.Main + jobTimer)
            scopeTimer.launch {
                timer()
            }
            playButton.setImageResource(R.drawable.play_stop)
        }

    }
}