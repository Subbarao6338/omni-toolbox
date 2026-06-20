package com.nature.docs.data.audio

import android.media.MediaRecorder
import java.io.File

object AudioEngine {
    private var recorder: MediaRecorder? = null

    fun startRecording(outputFile: File) {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    fun getAmplitude(): Int {
        return recorder?.maxAmplitude ?: 0
    }
}
