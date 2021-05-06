package com.example.metronome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import com.example.metronome.knob.RotaryKnobView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), RotaryKnobView.RotaryKnobListener {

    var timeSignature : Int = 0
    var bitsPerMinute :Int = 0
    var timeInterval : Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeSignature = resources.getInteger(R.integer.seekBarStart)
        bitsPerMinute = resources.getInteger(R.integer.BPMStart)
        timeInterval = (60 / bitsPerMinute).toFloat()
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
    }

    override fun onRotate(value: Int) {
        textBPM.text = "$value BPS"
        bitsPerMinute = value
        timeInterval = (60 / value).toFloat()
    }
}