package com.czm.library.save.imp;

import com.czm.library.LogUtil;
import com.czm.library.save.ISave;
import com.czm.library.util.Logs;

/**
 * 用于写入Log到本地
 * Created by czm.
 */
public class LogWriter {
    private static LogWriter mLogWriter;
    private static ISave mSave;

    private LogWriter() {
    }


    public static LogWriter getInstance() {
        if (mLogWriter == null) {
            synchronized (LogUtil.class) {
                if (mLogWriter == null) {
                    mLogWriter = new LogWriter();
                }
            }
        }
        return mLogWriter;
    }


    public LogWriter init(ISave save) {
        mSave = save;
        return this;
    }

    public static void writeLog(String tag, String content) {
        Logs.d(tag, content);
        mSave.writeLog(tag, content);
    }
}