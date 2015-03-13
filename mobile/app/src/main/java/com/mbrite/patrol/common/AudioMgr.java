package com.mbrite.patrol.common;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.IOException;

public class AudioMgr {

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    public void startRecording(String fileName) throws IOException {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mRecorder.prepare();
        mRecorder.start();
    }

    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void startPlaying(String fileName, MediaPlayer.OnCompletionListener listener)
            throws IOException {
        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(fileName);
        mPlayer.prepare();
        mPlayer.setOnCompletionListener(listener);
        mPlayer.start();
    }

    public void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stopRecording();
        stopPlaying();
    }
}
