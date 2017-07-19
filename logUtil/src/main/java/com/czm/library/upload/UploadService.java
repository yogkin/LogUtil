package com.czm.library.upload;

import android.app.IntentService;
import android.content.Intent;

import com.czm.library.LogUtil;
import com.czm.library.util.CompressUtil;
import com.czm.library.util.FileUtil;
import com.czm.library.util.Logs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 此Service用于后台发送日志
 * Created by czm.
 */
public class UploadService extends IntentService {

    public static final String TAG = "UploadService";


    /**
     * 压缩包名称的一部分：时间戳
     */
    public final static SimpleDateFormat ZIP_FOLDER_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSS", Locale.getDefault());

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadService() {
        super(TAG);
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 同一时间只会有一个耗时任务被执行，其他的请求还要在后面排队，
     * onHandleIntent()方法不会多线程并发执行，所有无需考虑同步问题
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        final File logfolder = new File(LogUtil.getInstance().getROOT() + "Logs/");
        // 如果Log文件夹都不存在，说明不存在崩溃日志，检查缓存是否超出大小后退出
        if (!logfolder.exists() || logfolder.listFiles().length == 0) {
            Logs.d("Log文件夹都不存在，无需上传");
            return;
        }

        ArrayList<File> crashFileList = new ArrayList<>();

        //判断上传日志等级
        if (checkLogLeve()== LogUtil.LOG_LEVE_ERROR) {
            //只存在log文件，但是不存在崩溃日志，也不会上传
            crashFileList = FileUtil.getCrashList(logfolder);
        }else {
            crashFileList = FileUtil.getLogInfoList(logfolder);
        }



        if (crashFileList.size() == 0) {
            Logs.d(TAG, "只存在log文件，但是不存在崩溃日志，所以不上传");
            return;
        }
        File zipfolder = new File(LogUtil.getInstance().getROOT() + "AlreadyUploadLog/");
        File zipfile = new File(zipfolder, "UploadOn" + ZIP_FOLDER_TIME_FORMAT.format(System.currentTimeMillis()) + ".zip");
        final File rootdir = new File(LogUtil.getInstance().getROOT());
        StringBuilder content = new StringBuilder();

        //创建文件，如果父路径缺少，创建父路径
        zipfile = FileUtil.createFile(zipfolder, zipfile);

        //把日志文件压缩到压缩包中
        if (CompressUtil.zipFileAtPath(logfolder.getAbsolutePath(), zipfile.getAbsolutePath())) {
            Logs.d("把日志文件压缩到压缩包中 ----> 成功");

            if (checkLogContent()== LogUtil.LOG_LEVE_CONTENT) {
                for (File crash : crashFileList) {
                    content.append(FileUtil.getText(crash));
                    content.append("\n");
                }
            }
            LogUtil.getInstance().getUpload().sendFile(zipfile, content.toString(), new ILogUpload.OnUploadFinishedListener() {
                @Override
                public void onSuceess() {
                    Logs.d("日志发送成功！！");
                    FileUtil.deleteDir(logfolder);
                    boolean checkresult = checkCacheSize(rootdir);
                    Logs.d("缓存大小检查，是否删除root下的所有文件 = " + checkresult);
                    stopSelf();
                }

                @Override
                public void onError(String error) {
                    Logs.d("日志发送失败：  = " + error);
                    boolean checkresult = checkCacheSize(rootdir);
                    Logs.d("缓存大小检查，是否删除root下的所有文件 " + checkresult);
                    stopSelf();
                }
            });
        } else {
            Logs.d("把日志文件压缩到压缩包中 ----> 失败");
        }
    }

    /**
     * 检查文件夹是否超出缓存大小，超出则会删除该目录下的所有文件
     *
     * @param dir 需要检查大小的文件夹
     * @return 返回是否超过大小，true为是，false为否
     */

    public boolean checkCacheSize(File dir) {
        long dirSize = FileUtil.folderSize(dir);
        return dirSize >= LogUtil.getInstance().getCacheSize() && FileUtil.deleteDir(dir);
    }



    /**
     * 检测上传日志等级
     * @return 1 设置上传日志信息为所有,0 设置上传日志信息为crash
     */
    public int checkLogLeve() {

        return LogUtil.getInstance().getLogLeve();
    }

    /**
     * 设置是否把信息记录在日志内容中
     * @return 0 设置上传日志内容为空,1 设置上传日志信息为crash
     */
    public int checkLogContent() {

        return LogUtil.getInstance().getLogContent();
    }
}
