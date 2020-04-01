package com.trading.display.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;

/**
 * Android 定时循环任务实现:
 * https://www.cnblogs.com/yunfang/p/6258053.html
 * https://blog.csdn.net/u014733374/article/details/53413970
 */
public class AlarmService extends Service{

    private DownLoadBinder downLoadBinder=new DownLoadBinder();
    /**
     * 回调
     */
    private Callback callback;
    private int num;

    public AlarmService() {
    }

    /**
     * 提供接口回调方法
     * @param callback
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    /**
     * 调用Service都会执行到该方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //这里模拟后台操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("wj","开始执行循环了"+ System.currentTimeMillis());
                num++;
                if(callback!=null){
                    /*
                     * 得到最新数据
                     */
                    callback.getNum(num);
                }

            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60*1000*5; // 这是5分钟的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        System.out.println("=====onBind=====");
        return downLoadBinder;
    }

    /**
     * 内部类继承Binder
     * @author lenovo
     *
     */
    public class DownLoadBinder extends Binder {
        /**
         * 声明方法返回值是MyService本身
         * @return
         */
        public AlarmService getService() {
            return AlarmService.this;
        }
    }

    /**
     * 回调接口
     *
     * @author lenovo
     *
     */
    public static interface Callback {
        /**
         * 得到实时更新的数据
         *
         * @return
         */
        void getNum(int num);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("AlarmService", "on destroy");
    }


}
