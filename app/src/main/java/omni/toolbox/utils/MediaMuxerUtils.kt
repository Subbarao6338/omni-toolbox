package omni.toolbox.utils

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.annotation.SuppressLint
import java.nio.ByteBuffer

object MediaMuxerUtils {
    @SuppressLint("WrongConstant")
    fun mux(videoPath: String, audioPath: String, outputPath: String) {
        val videoExtractor = MediaExtractor()
        videoExtractor.setDataSource(videoPath)
        val audioExtractor = MediaExtractor()
        audioExtractor.setDataSource(audioPath)

        val muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        videoExtractor.selectTrack(0)
        val videoFormat = videoExtractor.getTrackFormat(0)
        val videoTrackIndex = muxer.addTrack(videoFormat)

        audioExtractor.selectTrack(0)
        val audioFormat = audioExtractor.getTrackFormat(0)
        val audioTrackIndex = muxer.addTrack(audioFormat)

        muxer.start()

        val buffer = ByteBuffer.allocate(1024 * 1024)
        val bufferInfo = MediaCodec.BufferInfo()

        // Video
        while (true) {
            bufferInfo.size = videoExtractor.readSampleData(buffer, 0)
            if (bufferInfo.size < 0) break
            bufferInfo.presentationTimeUs = videoExtractor.sampleTime
            bufferInfo.flags = videoExtractor.sampleFlags
            muxer.writeSampleData(videoTrackIndex, buffer, bufferInfo)
            videoExtractor.advance()
        }

        // Audio
        while (true) {
            bufferInfo.size = audioExtractor.readSampleData(buffer, 0)
            if (bufferInfo.size < 0) break
            bufferInfo.presentationTimeUs = audioExtractor.sampleTime
            bufferInfo.flags = audioExtractor.sampleFlags
            muxer.writeSampleData(audioTrackIndex, buffer, bufferInfo)
            audioExtractor.advance()
        }

        muxer.stop()
        muxer.release()
        videoExtractor.release()
        audioExtractor.release()
    }
}
