package com.example.metronome

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
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
import java.lang.Short


class MainActivity : AppCompatActivity(), RotaryKnobView.RotaryKnobListener {

    var timeSignature : Int = 0
    var bitsPerMinute :Int = 0
    var timeInterval : Long = 0
    var soundHz : Int = 0
    private var playSound = false

    private var tone: AudioTrack? = null
    private lateinit var jobTimer : Job
    private lateinit var scopeTimer: CoroutineScope
    private lateinit var scopeDraw: CoroutineScope
    private var imageList = ArrayList<ImageView>()
    private var lighted = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeSignature = resources.getInteger(R.integer.seekBarStart)
        bitsPerMinute = resources.getInteger(R.integer.BPMStart)
        timeInterval = (60000 / bitsPerMinute).toLong()
        textBPM.text = "$bitsPerMinute BPS"
        textMin.text = resources.getInteger(R.integer.seekBarMin).toString()
        textMax.text = resources.getInteger(R.integer.seekBarMax).toString()
        textHz.text = "${resources.getInteger(R.integer.HZStart)}Hz"
        textMaxHz.text = "${resources.getInteger(R.integer.HZMax)}Hz"
        textMinHz.text = "${resources.getInteger(R.integer.HZMin)}Hz"
        soundHz = resources.getInteger(R.integer.HZStart)
        textTimeSignature.text = resources.getInteger(R.integer.seekBarStart).toString()

        knob.listener = this

        seekBarTimeSignature.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
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

        seekBarHz.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textHz.text = "${progress}Hz"
                soundHz = progress
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

    private fun generateTone(freqHz: Double, durationMs: Int): AudioTrack? {
        val sampleRate = getBestSampleRate()
        val count = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(count)
        var i = 0
        while (i < count) {
            //* 0x7FFF
            val sample = (kotlin.math.sin(2 * Math.PI * i * freqHz / sampleRate) * 0x7FFF).toShort()
            //Log.e("sample", sample.toString())
            samples[i] = sample
            i ++
        }
        val track = AudioTrack(AudioManager.STREAM_MUSIC, sampleRate.toInt(),
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC)
        track.write(samples, 0, count)
        return track
    }

    private fun getBestSampleRate(): Double {
        return if (Build.VERSION.SDK_INT >= 17) {
            val am = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val sampleRateString = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
            sampleRateString?.toDouble() ?: 44100.0
        } else {
            44100.0
        }
    }

    private suspend fun timer() {
        var stamp: Long
        while(true) {

            stamp = System.currentTimeMillis()
            //Log.e("timer", Timestamp(System.currentTimeMillis()).toString())

            tone?.stop()
//            tone?.reloadStaticData()
            tone?.play()

            light()

            delay(timeInterval - System.currentTimeMillis() + stamp)
        }
    }

    private suspend fun light(){


        imageList[lighted].setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.soundActive), android.graphics.PorterDuff.Mode.SRC_IN)
        delay(100)
        imageList[lighted].setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.soundNoActive), android.graphics.PorterDuff.Mode.SRC_IN)


        lighted = (lighted + 1)%timeSignature
    }

    private suspend fun draw(){
        if(this::jobTimer.isInitialized) {
            jobTimer.join()
        }
        Log.e("timeSignature", timeSignature.toString())


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
            tone = generateTone(soundHz.toDouble(), 100)
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