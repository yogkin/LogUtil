package com.czm.crashDemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.czm.library.LogUtil;
import com.czm.library.save.imp.LogWriter;
import com.czm.library.util.FileUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpListener();

    }


    private void setUpListener() {
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.getInstance().upload(getApplicationContext());
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogWriter.writeLog(TAG, "打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "打Log测试！！！！打Log测试！！！！" +
                        "" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "" +
                        "打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！打Log测试！！！！" +
                        "" +
                        "");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.deleteDir(new File(LogUtil.getInstance().getROOT()));
            }
        });
    }
}
