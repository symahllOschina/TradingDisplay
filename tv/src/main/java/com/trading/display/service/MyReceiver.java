package com.trading.display.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.trading.display.activity.TVMainActivity;

/**
 * 开机自启动广播
 */
public class MyReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Intent i = new Intent(context, TVMainActivity.class);
            //非常重要，如果缺少的话，程序将在启动时报错
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
