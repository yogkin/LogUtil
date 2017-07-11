package com.czm.crashDemo;

import android.app.Application;

import com.czm.library.LogUtil;
import com.czm.library.save.imp.CrashWriter;
import com.czm.library.upload.email.EmailReporter;

/**
 * Created by czm.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initCrashReport();
    }

    private void initCrashReport() {
        LogUtil.getInstance()
                .setCacheSize(30 * 1024 * 1024)//支持设置缓存大小，超出后清空
                .setLogDir(getApplicationContext(), "sdcard/" + this.getString(this.getApplicationInfo().labelRes) + "/")//定义路径为：sdcard/[app name]/
                .setWifiOnly(false)//设置只在Wifi状态下上传，设置为false为Wifi和移动网络都上传
                .setLogSaver(new CrashWriter(getApplicationContext()))//支持自定义保存崩溃信息的样式
                .setLogLeve(LogUtil.LOG_LEVE_INFO)
                //.setLogDebugModel(true) //设置是否显示日志信息
                //.setLogContent(LogUtil.LOG_LEVE_CONTENT_NULL)  //设置是否在邮件内容显示附件信息文字
                //.setEncryption(new AESEncode()) //支持日志到AES加密或者DES加密，默认不开启
                .init(getApplicationContext());
        initEmailReporter();
    }

    /**
     * 使用EMAIL发送日志
     */
    private void initEmailReporter() {

//        Email email = new Email(this,"smtp.163.com");
//        email.setNamePass("betengapp","120120");


        EmailReporter email = new EmailReporter(this);
        email.setReceiver("betengapp@163.com");//收件人
        email.setSender("betengapp@163.com");//发送人邮箱
        email.setSendPassword("bt4009989256");//邮箱密码
        email.setSMTPHost("smtp.163.com");//SMTP地址
        email.setPort("465");//SMTP 端口
        LogUtil.getInstance().setUploadType(email);
    }
}
