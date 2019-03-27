package com.tigerobo.venturecapital.messageinterccept;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class PingTestActivity extends AppCompatActivity {

    private Button ping;
    private EditText editText;
    private TextView textView;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_test);
        ping = findViewById(R.id.ping);
        editText = findViewById(R.id.edit_url);
        textView = findViewById(R.id.logText);
        ping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ping(editText.getText().toString().trim());
                    }
                });
                thread.start();
            }
        });
    }

    private StringBuilder stringBuilder;
    private int count = 0;
    private int MAX_COUNT = 6;

    private void ping(String pingIp) {
        stringBuilder = new StringBuilder("");
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ping " + pingIp);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("ping不通");
                }
            });
            InputStreamReader r = new InputStreamReader(process.getInputStream());
            LineNumberReader returnData = new LineNumberReader(r);
            String line = "";
            while ((line = returnData.readLine()) != null && count++ < MAX_COUNT) {
                stringBuilder.append(line).append("\n");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(stringBuilder.toString());
                    }
                });
            }
            if (stringBuilder.toString().contains("100% loss")) {
                stringBuilder.append("与 ").append(pingIp).append(" 连接不畅通.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(stringBuilder.toString());
                    }
                });
            } else {
                stringBuilder.append("与 ").append(pingIp).append(" 连接畅通.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(stringBuilder.toString());
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
