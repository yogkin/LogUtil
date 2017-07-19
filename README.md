# Android日志上传框架

当App崩溃的时，把崩溃信息保存到本地的同时，你只需要几句，就可以实现把崩溃信息上传到你想上传的地方，可以是HTTP，也可以选择上传到你的邮箱。
同样，你可以上传你需要记录的信息。
特性介绍  

| 特性|简介|
| ------ | ------ |
|日志自动删除|日志文件大小超过设置缓存大小，自动删除最前面一天的日志|
|自定义上传文件内容|默认crash信息，你可以选择自定义的信息|
|自定义日志保存路径 |默认保存在Android/data/com.xxxx.xxxx/log中|
|支持多种上传方式|目前支持邮件上传与HTTP上传，会一并把文件夹下的所有日志打成压缩包作为附件上传|
|日志加密保存|提供AES，DES两种加密解密方式支持，默认不加密|
|日志按天保存|目前崩溃日志和Log信息是按天保存，你可以继承接口来实现更多的保存样式|
|携带设备与OS信息|在创建日志的时候，会一并记录OS版本号，App版本，手机型号等信息，方便还原崩溃|
|自定义日志上传的时机|默认只在Wifi状态下上传支持，也支持在Wifi和移动网络下上传|
|支持保存Log日志|在打印Log的同时，把Log写入到本地（保存的时候会附带线程名称，线程id，打印时间），还原用户操作路径，为修复崩溃提供

## 依赖添加
在你的项目根目录下的build.gradle文件中加入依赖


``` java
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
添加依赖
``` java
dependencies {
    compile 'com.github.yogkin:LogUtil:1.0.2'
}
```

## 初始化
在自定义Application文件加入以下几行代码即可，你可以设置需要上传的信息，默认为crash信息。如果你需要把自定义记录信息上传到服务器，情况下面代码介绍。
``` java
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
                .setWifiOnly(true)//设置只在Wifi状态下上传，设置为false为Wifi和移动网络都上传
                .setLogLeve(LogUtil.LOG_LEVE_INFO)//设置为日常日志也会上传
                 //.setLogDebugModel(true) //设置是否显示日志信息
                //.setLogContent(LogUtil.LOG_LEVE_CONTENT_NULL)  //设置是否在邮件内容显示附件信息文字
                .setLogSaver(new CrashWriter(getApplicationContext()))//支持自定义保存崩溃信息的样式
                //.setEncryption(new AESEncode()) //支持日志到AES加密或者DES加密，默认不开启
                .init(getApplicationContext());
        initEmailReporter();
    }

    /**
     * 使用EMAIL发送日志
     */
    private void initEmailReporter() {
        EmailReporter email = new EmailReporter(this);
        email.setReceiver("tfish0766@163.com");//收件人
        email.setSender("tfish0766@163.com");//发送人邮箱
        email.setSendPassword("test1234");//邮箱的客户端授权码，注意不是邮箱密码
        email.setSMTPHost("smtp.163.com");//SMTP地址
        email.setPort("465");//SMTP 端口
        LogUtil.getInstance().setUploadType(email);
    }
    
    /**
     * 使用HTTP发送日志
     */
    private void initHttpReporter() {
        HttpReporter http = new HttpReporter(this);
        http.setUrl("http://crashreport.jd-app.com/your_receiver");//发送请求的地址
        http.setFileParam("fileName");//文件的参数名
        http.setToParam("to");//收件人参数名
        http.setTo("你的接收邮箱");//收件人
        http.setTitleParam("subject");//标题
        http.setBodyParam("message");//内容
        LogUtil.getInstance().setUploadType(http);
    }
}

```

## 上传
在任意地方，调用以下方法即可，崩溃发生后，会在下一次App启动的时候使用Service异步打包日志，然后上传日志，发送成功与否，Service都会自动退出释放内存
``` java
LogUtil.getInstance().upload(context);
```


## 保存Log到本地
使用以下方法，打印Log的同时，把Log信息保存到本地（保存的时候会附带线程名称，线程id，打印时间），并且随同崩溃日志一起，发送到特定的邮箱或者服务器上。帮助开发者还原用户的操作路径，更好的分析崩溃产生的原因
``` java
LogWriter.writeLog("czm", "打Log测试！！！！");
```

参考LogReport项目
