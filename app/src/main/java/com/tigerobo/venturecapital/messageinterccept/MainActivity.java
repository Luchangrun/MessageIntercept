package com.tigerobo.venturecapital.messageinterccept;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import st.lowlevel.storo.Storo;

public class MainActivity extends AppCompatActivity implements SMSBroadcastReceiver.OnReceiveSMSListener, ParamsView.OnDeleteListener {

    private SMSBroadcastReceiver mSMSBroadcastReceiver = new SMSBroadcastReceiver();
    private EditText editText;
    private EditText editParams;
    private TextView verifyCode;
    private TextView hint;
    private Button start;
    private Button stop;
    private Button addParams;
    private LinearLayout paramsLl;
    private OkHttpClient okHttpClient;
    private MyJobService myJobService;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ping:
                Intent intent = new Intent(this, PingTestActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_url);
        editParams = findViewById(R.id.edit_params);
        verifyCode = findViewById(R.id.verifyCode);
        hint = findViewById(R.id.hint);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        addParams = findViewById(R.id.add_params);
        paramsLl = findViewById(R.id.params_ll);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        } else {
            mSMSBroadcastReceiver.setOnReceiveSMSListener(this);
            // 注册广播
            IntentFilter intentFilter = new IntentFilter(SMSBroadcastReceiver.SMS_RECEIVED_ACTION);
            // 设置优先级
            intentFilter.setPriority(1000);
            registerReceiver(mSMSBroadcastReceiver, intentFilter);
        }
        String requestUrl = PreferencesHelper.getRequestUrl(this);
        final String paramsName = PreferencesHelper.getParamsName(this);
        if (requestUrl != null && !requestUrl.equals("")) {
            editText.setText(requestUrl);
        }
        if (paramsName != null && !paramsName.equals("")) {
            editParams.setText(paramsName);
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
                } else {
                    unregisterReceiver(mSMSBroadcastReceiver);
                    mSMSBroadcastReceiver.setOnReceiveSMSListener(MainActivity.this);
                    // 注册广播
                    IntentFilter intentFilter = new IntentFilter(SMSBroadcastReceiver.SMS_RECEIVED_ACTION);
                    // 设置优先级
                    intentFilter.setPriority(1000);
                    registerReceiver(mSMSBroadcastReceiver, intentFilter);
                    Toast.makeText(MainActivity.this, "开始监听", Toast.LENGTH_SHORT).show();
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterReceiver(mSMSBroadcastReceiver);
                Toast.makeText(MainActivity.this, "停止监听", Toast.LENGTH_SHORT).show();
            }
        });
        addParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParamsView paramsView = new ParamsView(MainActivity.this);
                paramsView.setListener(MainActivity.this);
                paramsLl.addView(paramsView);
            }
        });
        myJobService = new MyJobService();
        myJobService.startJobSheduler();
        List<RequestParams.RequestParam> paramsCache = getParamsCache();
        if (paramsCache != null && paramsCache.size() > 0) {
            for (int i = 0; i < paramsCache.size(); i++) {
                ParamsView paramsView = new ParamsView(MainActivity.this);
                paramsView.setListener(MainActivity.this);
                paramsView.setName(paramsCache.get(i).getParamsName());
                paramsView.setValue(paramsCache.get(i).getParamsValue());
                paramsLl.addView(paramsView);
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    private List<RequestParams.RequestParam> params = new ArrayList<>();
    private RequestParams requestParams = new RequestParams();

    @Override
    public void onReceived(String message) {
        Log.e("SMSCODE", message);
        params.clear();
        verifyCode.setText(message);
        StringBuilder url = new StringBuilder(editText.getText().toString() + "?" + editParams.getText().toString() + "=" + message);
        for (int i = 0; i < paramsLl.getChildCount(); i++) {
            if (paramsLl.getChildAt(i) instanceof ParamsView) {
                ParamsView child = (ParamsView) paramsLl.getChildAt(i);
                if (child.getEditName() != null && !child.getEditName().equals("") &&
                        child.getEditValue() != null && !child.getEditValue().equals("")) {
                    url.append("&").append(child.getEditName()).append("=").append(child.getEditValue());
                    params.add(new RequestParams.RequestParam(child.getEditName(), child.getEditValue()));
                }
            }
        }
        Request request = new Request.Builder()
                .url(url.toString())
                .get()
                .build();
        PreferencesHelper.setRequestUrl(this, editText.getText().toString());
        PreferencesHelper.setParamsName(this, editParams.getText().toString());
        requestParams.setParams(params);
        hint.setText("请求开始!");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hint.setText("请求错误!  " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hint.setText("请求成功! \n" + response.toString());
                    }
                });
            }
        });
        trySaveParams();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mSMSBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mSMSBroadcastReceiver.setOnReceiveSMSListener(this);
                    // 注册广播
                    IntentFilter intentFilter = new IntentFilter(SMSBroadcastReceiver.SMS_RECEIVED_ACTION);
                    // 设置优先级
                    intentFilter.setPriority(Integer.MAX_VALUE);
                    registerReceiver(mSMSBroadcastReceiver, intentFilter);
                } else {
                    Toast.makeText(MainActivity.this, "你没有启动权限，无法获取短信信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                saveParams(requestParams);
                break;
            default:
        }
    }

    @Override
    public void onDelete(ParamsView paramsView) {
        if (paramsView.getParent() instanceof LinearLayout) {
            paramsLl.removeView(paramsView);
        }
    }

    private List<RequestParams.RequestParam> getParamsCache() {
        RequestParams params = Storo.get("params", RequestParams.class).execute();
        return params == null ? null : params.getParams();
    }

    private void saveParams(RequestParams params) {
        Storo.put("params", params).execute();
    }

    private void trySaveParams() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        } else {
            saveParams(requestParams);
        }
    }
}
