package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.callback.FFMpegCallback
import com.example.myapplication.dialogs.AudioDialog
import com.example.myapplication.dialogs.ProgressDialog
import com.example.myapplication.tools.OutputType
import com.example.myapplication.tools.audio.AudioMerger
import com.example.myapplication.utils.Utils
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.io.File

class ConvertActivity : AppCompatActivity(), FFMpegCallback {


    interface ProgressPublish {
        fun onProgress(progress: String)

        fun onDismiss()
    }

    companion object {
        lateinit var onProgress: ProgressPublish

        fun setProgressListener(onProgress: ProgressPublish) {
            this.onProgress = onProgress
        }
    }

    lateinit var audio2: File
    lateinit var audio3: File

    private lateinit var tvPath1: TextView
    private lateinit var tvPath2: TextView
    private lateinit var btnConvert: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert)

        tvPath1 = findViewById(R.id.tvPath1)
        tvPath2 = findViewById(R.id.tvPath2)
        btnConvert = findViewById(R.id.btnconvert)


        val s1:String = intent.getStringExtra("p1")
        val s2:String = intent.getStringExtra("p2")

        audio2  = File(s1)
        audio3 = File(s2)

        tvPath1.setText(audio2.path)
        tvPath2.setText(audio3.path)

        btnConvert.setOnClickListener {
            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                AudioMerger.with(this!!)
                        .setFile1(audio2)
                        .setFile2(audio3)
                        .setOutputPath(Utils.outputPath + "audio")
                        .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp3")
                        .setCallback(this@ConvertActivity)
                        .merge()

                ProgressDialog.show(supportFragmentManager, AudioMerger.TAG)
            } else {
                showInProgressToast()
            }
        }

    }

    private fun stopRunningProcess() {
        FFmpeg.getInstance(this).killRunningProcesses()
    }

    private fun isRunning(): Boolean {
        return FFmpeg.getInstance(this).isFFmpegCommandRunning
    }

    private fun showInProgressToast(){
        Toast.makeText(this, "Operation already in progress! Try again in a while.", Toast.LENGTH_SHORT).show()
    }

    override fun onProgress(progress: String) {

        //Prints log of progress
        Log.i("TAG", "Running: $progress")

        onProgress.run {
            onProgress(progress)
        }
    }

    override fun onSuccess(convertedFile: File, type: String) {
        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show()

        //Show preview of outputs for after checking type of media
        when {
            type.equals(OutputType.TYPE_AUDIO) -> AudioDialog.show(supportFragmentManager, convertedFile)
        }
    }

    override fun onFailure(error: Exception) {
        error.printStackTrace()
        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
        onProgress.run {
            onDismiss()
        }
    }

    override fun onFinish() {

        onProgress.run {
            onDismiss()
        }
    }

    override fun onNotAvailable(error: Exception) {
        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
        onProgress.run {
            onDismiss()
        }
    }
}
