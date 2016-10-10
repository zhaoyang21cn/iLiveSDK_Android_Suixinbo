package com.tencent.tdemofm;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * WAV文件读取类
 */
public class WaveFileReader {
    static public int LENCHUNKDESCRIPTOR = 4;
    static public int LENCHUNKSIZE = 4;
    static public int LENWAVEFLAG = 4;
    static public int LENFMTSUBCHUNK = 4;
    static public int LENSUBCHUNK1SIZE = 4;
    static public int LENAUDIOFORMAT = 2;
    static public int LENNUMCHANNELS = 2;
    static public int LENSAMPLERATE = 2;
    static public int LENBYTERATE = 4;
    static public int LENBLOCKLING = 2;
    static public int LENBITSPERSAMPLE = 2;
    static public int LENDATASUBCHUNK = 4;
    static public int LENSUBCHUNK2SIZE = 4;

    public static String CHUNKDESCRIPTOR = "RIFF";
    public static String WAVEFLAG = "WAVE";
    public static String FMTSUBCHUNK = "fmt ";
    public static String DATASUBCHUNK = "data";

    private String filename = null;
    private byte[] data = null;

    private int len = 0;

    private String chunkdescriptor = null;
    private long chunksize = 0;
    private String waveflag = null;
    private String fmtsubchunk = null;
    private long subchunk1size = 0;
    private int audioformat = 0;
    private int numchannels = 0;
    private long samplerate = 0;
    private long byterate = 0;
    private int blockalign = 0;
    private int bitspersample = 0;
    private String datasubchunk = null;
    private long subchunk2size = 0;
    private FileInputStream fis = null;
    private BufferedInputStream bis = null;

    private boolean issuccess = false;

    public WaveFileReader(String filename) {

        this.initReader(filename);
    }

    // 判断是否创建wav读取器成功
    public boolean isSuccess() {
        return issuccess;
    }

    // 获取每个采样的编码长度，8bit或者16bit
    public int getBitPerSample() {
        return this.bitspersample;
    }

    // 获取采样率
    public long getSampleRate() {
        return this.samplerate;
    }

    // 获取声道个数，1代表单声道 2代表立体声
    public int getNumChannels() {
        return this.numchannels;
    }

    // 获取数据长度，也就是一共采样多少个
    public int getDataLen() {
        return this.len;
    }

    // 获取数据
    public byte[] getData() {
        return this.data;
    }

    private void initReader(String filename) {
        this.filename = filename;
        Log.v("ILVB-DBG", "initReader->filename:"+filename);

        try {
            fis = new FileInputStream(this.filename);
            bis = new BufferedInputStream(fis);

            this.chunkdescriptor = readString(LENCHUNKDESCRIPTOR);
            if (!chunkdescriptor.endsWith("RIFF"))
                throw new IllegalArgumentException("RIFF miss, " + filename + " is not a wave file.");

            this.chunksize = readLong();
            this.waveflag = readString(LENWAVEFLAG);
            if (!waveflag.endsWith("WAVE"))
                throw new IllegalArgumentException("WAVE miss, " + filename + " is not a wave file.");

            this.fmtsubchunk = readString(LENFMTSUBCHUNK);
            if (!fmtsubchunk.endsWith("fmt "))
                throw new IllegalArgumentException("fmt miss, " + filename + " is not a wave file.");

            this.subchunk1size = readLong();
            this.audioformat = readInt();
            this.numchannels = readInt();
            this.samplerate = readLong();
            this.byterate = readLong();
            this.blockalign = readInt();
            this.bitspersample = readInt();

            if (subchunk1size > 0x10){  // 跳过附加信息
                readString((int)subchunk1size-0x10);
            }
            this.datasubchunk = readString(LENDATASUBCHUNK);
            if (datasubchunk.endsWith("fact")){
                subchunk2size = readLong();
                readString((int)subchunk2size);
                this.datasubchunk = readString(LENDATASUBCHUNK);
            }
            if (!datasubchunk.endsWith("data")) {
                throw new IllegalArgumentException("data miss, " + filename + " is not a wave file.");
            }
            this.subchunk2size = readLong();

            len = (int)subchunk2size;
            this.data = new byte[len];
            bis.read(this.data);
            issuccess = true;
        } catch (Exception e) {
            Log.e("ILVB-DBG", "read wav file failed:" + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (fis != null)
                    fis.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private String readString(int len) {
        byte[] buf = new byte[len];
        try {
            if (bis.read(buf) != len)
                throw new IOException("no more data!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buf);
    }

    private int readInt() {
        byte[] buf = new byte[2];
        int res = 0;
        try {
            if (bis.read(buf) != 2)
                throw new IOException("no more data!!!");
            res = (buf[0] & 0x000000FF) | (((int) buf[1]) << 8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private long readLong() {
        long res = 0;
        try {
            long[] l = new long[4];
            for (int i = 0; i < 4; ++i) {
                l[i] = bis.read();
                if (l[i] == -1) {
                    throw new IOException("no more data!!!");
                }
            }
            res = l[0] | (l[1] << 8) | (l[2] << 16) | (l[3] << 24);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private byte[] readBytes(int len) {
        byte[] buf = new byte[len];
        try {
            if (bis.read(buf) != len)
                throw new IOException("no more data!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }
}