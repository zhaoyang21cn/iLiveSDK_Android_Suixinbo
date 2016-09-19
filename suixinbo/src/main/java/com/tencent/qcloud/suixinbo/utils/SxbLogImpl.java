package com.tencent.qcloud.suixinbo.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.tencent.imsdk.MyLinkedBlockingDeque;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 日志输出
 */
public class SxbLogImpl {
    private static volatile Context sContext;

    private static String packageName = "";

    static MyLinkedBlockingDeque<String> logDeque = new MyLinkedBlockingDeque<String>(15000);

    private static final int[] INTERVAL_RETRY_INIT = new int[]{ 1, 2, 4, 8, 16, 29}; //重试时间

    private static AtomicInteger retryInitTimes = new AtomicInteger(0);

    public static final SimpleDateFormat timeFormatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

    private static String logTime = "";

    private static String logPath = "";

    static String nowUsedFile = "";

    static final ReentrantLock lock = new ReentrantLock();

    protected static Object formatterLock = new Object();

    private static long nextHourTime;

    private static long nextSecondMinuteTime;

    static long lastWriterErrorTime = 0 ;

    private static FileWriter writer;

    private static Handler retryInitHandler = new Handler(Looper.getMainLooper());


    /**
     * 初始化日志
     * @param context
     */
    public static void init(Context context){
        sContext = context;
        initRunnable.run();
    }

    /**
     * 将日志写到文件
     * @param log
     */
    private static void writeLogToFile(String log){
        try{
            // 如果SD卡不可用，则不写日志，以免每次都抛出异常，影响性能
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                System.out.println("writeLogToFile not ready");
                return;
            }

            if (null == writer) {
                System.out.println("can not write SxbLog.");
                long now = System.currentTimeMillis();
                if ( lastWriterErrorTime == 0  ) {
                    lastWriterErrorTime = now;
                } else if ( now -  lastWriterErrorTime > 60 * 1000 ){
                    try {
                        initLogFile(System.currentTimeMillis());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    lastWriterErrorTime = now;
                }
            } else {
                long now = System.currentTimeMillis();
                if (now > nextHourTime) {
                    initLogFile(now);
                }
                //加入消息的时候记录时间
                if ( lock.tryLock() ) {
                    try {
                        writer.write(log);
                        writer.flush();
                    } finally {
                        lock.unlock();
                    }
                }  else {
                    if(!insertLogToCacheHead(log)){
                        System.out.println("insertLogToCacheHead failed!");
                    }
                }
            }

        }catch (Throwable e){
            if (e instanceof IOException && e.getMessage().contains("ENOSPC")){
                e.printStackTrace();
            }else{
                try{
                    initLogFile(System.currentTimeMillis());
                }catch (Throwable e1){
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 写日志线程
     */
    static Thread takeThread = new Thread(){
        public void run(){
            while (true){
                try{
                    String log = logDeque.take();
                    if (null != log){
                        writeLogToFile(log);
                    }
                }catch (InterruptedException e){
                    System.out.println("write log file error: "+e.toString());
                }
            }
        }
    };

    private static String getThisHour(long nowCurrentMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(nowCurrentMillis);
        java.text.SimpleDateFormat logFileFormatter = new SimpleDateFormat("yy.MM.dd.HH");
        java.text.SimpleDateFormat timeFormatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        logTime = timeFormatter.format(nowCurrentMillis);
        String thisHourLogName = logFileFormatter.format(calendar.getTime());
        setNextSecond(calendar);
        setNextHour(calendar);
        return thisHourLogName;
    }

    private static void setNextHour(Calendar setSecondedCalendar) {
        setSecondedCalendar.add(Calendar.HOUR_OF_DAY, 1);
        setSecondedCalendar.set(Calendar.MINUTE, 0);
        setSecondedCalendar.set(Calendar.SECOND, 0);
        nextHourTime = setSecondedCalendar.getTimeInMillis();
    }

    private static void setNextSecond(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        nextSecondMinuteTime = calendar.getTimeInMillis() + 1000;
    }

    public static String getLogFileName(String hourTime) {
        return packageName + "_" + hourTime + ".log";
    }

    private static synchronized void checkNextMinuteTime(long currentTimeMillis) {
        if (currentTimeMillis > nextSecondMinuteTime) {
            synchronized (formatterLock) {
                logTime = timeFormatter.format(currentTimeMillis);
                nextSecondMinuteTime = nextSecondMinuteTime+1000;
            }
        }
    }


    /**
     * 初始化日志文件
     * @param nowCurrentTimeMillis
     * @throws IOException
     */
    static synchronized void initLogFile(long nowCurrentTimeMillis) throws IOException {
        logPath = Environment.getExternalStorageDirectory().getPath() + "/tencent/sxblog/" + packageName.replace(".", "/")
                + "/";
        File tmpeFile = new File(logPath);
        if (!tmpeFile.exists()) {
            tmpeFile.mkdirs();
        }
        nowUsedFile = logPath + getLogFileName(getThisHour(nowCurrentTimeMillis));
        try {
            tmpeFile = new File(nowUsedFile);
            if (!tmpeFile.exists()) {
                boolean b = tmpeFile.createNewFile();
                if (null != writer) {
                    writer.write(logTime + "|" + "|D|" + android.os.Build.MODEL+" "+android.os.Build.VERSION.RELEASE+" create newLogFile "+tmpeFile.getName()+" "+b+"\n");
                    writer.flush();
                }
            } else {
                if (null != writer) {
                    writer.write(logTime + "|" + "|E|" + android.os.Build.MODEL+" "+android.os.Build.VERSION.RELEASE+"|newLogFile "+tmpeFile.getName()+" is existed.\n");
                    writer.flush();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        writer = new FileWriter(tmpeFile, true);
    }

    /**
     * 日志初始化Runnable
     */
    public static Runnable initRunnable = new Runnable() {
        @Override
        public void run() {
            if (null == sContext){
                return;
            }

            new Thread("QLVBLogInitThread"){
                @Override
                public void run() {
                    try {
                        try {
                            packageName = sContext.getPackageName();
                        } catch (Exception e) {
                            packageName = "unknown";
                        }
                        initLogFile(System.currentTimeMillis());
                        takeThread.setName("logWriteThread");
                        takeThread.start();
                        retryInitHandler.removeCallbacks(initRunnable);
                    }catch (Exception e){
                        int times = retryInitTimes.get();
                        System.out.println("QLog init post retry " + times + " times, interval " + INTERVAL_RETRY_INIT[times]);
                        retryInitHandler.removeCallbacks(initRunnable);
                        retryInitHandler.postDelayed(initRunnable, INTERVAL_RETRY_INIT[times] * 60000);
                        times++;
                        if(times >= INTERVAL_RETRY_INIT.length){
                            times = 0;
                        }
                        retryInitTimes.set(times);
                    }
                }
            }.start();
        }
    };

    public static void writeLog(String level, String tag, String msg, Throwable tr){
        long now = System.currentTimeMillis();
        if (now >= nextSecondMinuteTime){
            checkNextMinuteTime(now);
        }

        long threadId = Thread.currentThread().getId();
        String message = logTime + "|" + level +"|" + String.valueOf(threadId) + "|" + tag + "|" + msg + "\n";
        if (null != tr){
            message = msg + "\n" + android.util.Log.getStackTraceString(tr) + "\n";
        }
        addLogToCache(message);
}

    /**
     * 添加日志到缓存
     * @param log
     * @return
     */
    private static boolean addLogToCache(String log){
        try{
            logDeque.add(log);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 添加缓冲头部
     * @param log
     * @return
     */
    private static boolean insertLogToCacheHead(String log) {
        try{
            logDeque.addFirst(log);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
