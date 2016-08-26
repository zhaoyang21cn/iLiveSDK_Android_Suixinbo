package com.smp.soundtouchandroid;

import com.tencent.ilivesdk.core.ILiveLog;

public class SoundTouch {
    private final String TAG = "SoundTouch";
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    static {
        System.loadLibrary("soundtouch");
    }

    private int channels, samplingRate, bytesPerSample;
    private float tempo;
    private float pitchSemi;
    private float rate;
    private int track;

    public SoundTouch(int track, int channels, int samplingRate, int bytesPerSample, float tempo, float pitchSemi) {
        this.channels = channels;
        this.samplingRate = samplingRate;
        this.bytesPerSample = bytesPerSample;
        this.tempo = tempo;
        this.pitchSemi = pitchSemi;
        this.track = track;
        this.rate = 1.0f;

        setup(track, channels, samplingRate, bytesPerSample, tempo, pitchSemi);
    }

    public int getBytesPerSample() {
        return bytesPerSample;
    }

    public int getChannels() {
        return channels;
    }

    public long getOutputBufferSize() {
        return getOutputBufferSize(track);
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public float getPitchSemi() {
        return pitchSemi;
    }

    public float getTempo() {
        return tempo;
    }

    public float getRate() {
        return rate;
    }

    public int getTrackId() {
        return track;
    }

    public void setBytesPerSample(int bytesPerSample) {
        this.bytesPerSample = bytesPerSample;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public void setSpeech(boolean isSpeech) {
        setSpeech(track, isSpeech);
    }

    public void setPitchSemi(float pitchSemi) {
        this.pitchSemi = pitchSemi;
        setPitchSemi(track, pitchSemi);
    }

    public void setTempo(float tempo) {
        this.tempo = tempo;
        setTempo(track, tempo);
    }

    public void setRate(float rate) {
        this.rate = rate;
        setRate(track, rate);
    }

    public void setRateChange(float rate) {
        this.rate = rate;
        setRateChange(track, rate);
    }

    public void setTempoChange(float tempoChange){
        if (tempoChange < -50 || tempoChange > 100)
            ILiveLog.e(TAG, "Tempo percentage must be between -50 and 100");
        this.tempo = 1.0f + 0.01f * tempoChange;
        setTempoChange(track, tempoChange);
    }

    public void clearBuffer() {
        clearBytes(track);
    }

    public void putBytes(byte[] input) {
        putBytes(track, input, input.length);
    }

    public int getBytes(byte[] output) {
        return getBytes(track, output, output.length);
    }

    // call finish after the last bytes have been written
    public void finish() {
        finish(track, DEFAULT_BUFFER_SIZE);
    }

    private static synchronized native final void clearBytes(int track);

    private static synchronized native final void finish(int track, int bufSize);

    private static synchronized native final int getBytes(int track, byte[] output, int toGet);

    private static synchronized native final void putBytes(int track, byte[] input, int length);

    private static synchronized native final void setup(int track, int channels, int samplingRate, int bytesPerSample, float tempo, float pitchSemi);

    private static synchronized native final void setPitchSemi(int track, float pitchSemi);

    private static synchronized native final long getOutputBufferSize(int track);

    private static synchronized native final void setSpeech(int track, boolean isSpeech);

    private static synchronized native final void setTempo(int track, float tempo);

    private static synchronized native final void setRate(int track, float rate);

    private static synchronized native final void setRateChange(int track, float rateChange);

    private static synchronized native final void setTempoChange(int track, float tempoChange);

}
