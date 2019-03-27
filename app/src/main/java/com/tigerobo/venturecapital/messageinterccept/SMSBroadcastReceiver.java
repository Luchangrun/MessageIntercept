package com.tigerobo.venturecapital.messageinterccept;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSBroadcastReceiver";
    private OnReceiveSMSListener mOnReceiveSMSListener;
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                // 短信号码
                String sender = smsMessage.getDisplayOriginatingAddress();
                Log.d(TAG, sender + "");
                //短信内容
                String content = smsMessage.getDisplayMessageBody();
                // 筛选
                if (content.contains("验证码") && content.contains("企名片") && mOnReceiveSMSListener != null) {
                    Pattern pattern = Pattern.compile("[^0-9]");
                    Matcher matcher = pattern.matcher(content);
                    if (matcher.find()) {
                        mOnReceiveSMSListener.onReceived(matcher.replaceAll(""));
                    }
                    abortBroadcast();
                }
            }
        }

    }

    /**
     * 回调接口
     */
    public interface OnReceiveSMSListener {
        void onReceived(String message);
    }


    public void setOnReceiveSMSListener(OnReceiveSMSListener onReceiveSMSListener) {
        mOnReceiveSMSListener = onReceiveSMSListener;
    }
}