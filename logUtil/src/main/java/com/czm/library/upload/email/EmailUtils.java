package com.czm.library.upload.email;

import android.content.Context;

import com.czm.library.upload.ILogUpload;

import java.io.File;

public class EmailUtils {



	public static void sendMailForJavaByBoLun(Context context,String title, String body, File file, ILogUpload.OnUploadFinishedListener onUploadFinishedListener) {
		try{
			Email themail = new Email(context,"smtp.163.com");
			themail.setNeedAuth(true);
			themail.setSubject(title);
			themail.setBody(body);
			themail.setTo("betengapp@163.com");
			// 填写邮件发件人信息描述。
			themail.setFrom(new String("service"));
			// 填写发件人邮箱与密码
			themail.setNamePass("betengapp", "120120");
			themail.sendFile(file,body,onUploadFinishedListener);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
