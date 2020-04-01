package com.trading.display.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.trading.display.activity.TVMainActivity;

/**
 *  接收服务数据发送到指定Activity
 */
public class AlarmReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmService.class);
        context.startService(i);
    }
}
