package com.example.metronome

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.metronome.knob.RotaryKnobView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), RotaryKnobView.RotaryKnobListener {

    var timeSignature : Int = 0
    var bitsPerMinute :Int = 0
    var timeInterval : Long = 0

    private val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    private val job = Job()
    private val uiscope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeSignature = resources.getInteger(R.integer.seekBarStart)
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

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        uiscope.launch {
            timer()
        }
    }


    override fun onRotate(value: Int) {
        textBPM.text = "$value BPS"
        bitsPerMinute = value
        Log.e("value",  value.toString())
        timeInterval = (60000 / value).toLong()
        Log.e("timeInterval",  timeInterval.toString())
    }

    private suspend fun timer(){
        while(true) {

            delay(timeInterval)
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
        }
    }
}