package safe.girl.just.girl;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

import safe.girl.just.person.SystemConstant;

/**
 * Created by 许灡珊 on 2016/7/18.
 */
public class TipService extends Service {
    /**变量*/
    private static final int LOW_POWER = 1 ;
    private static final int LOW_SIGNAL = 1 ;
    private static final int NO_NET = 1 ;
    TelephonyManager telephonyManager ;                              //手机信号网络
    BattaryBroadcast battaryBroadcast = new BattaryBroadcast() ;    //电池信号

    long timeNow = System.currentTimeMillis() ;

    /**低电量发送广播线程*/
    Runnable low_power = new Runnable() {
        @Override
        public void run() {
            sendBroadcast(new Intent(SystemConstant.POWER_ID));
        }
    };
    /**没网*/
    Runnable no_net = new Runnable() {
        @Override
        public void run() {
            sendBroadcast(new Intent(SystemConstant.NET_ID));
        }
    };

    Runnable low_signal = new Runnable() {
        @Override
        public void run() {
            sendBroadcast(new Intent(SystemConstant.SIGNAL_ID));
        }
    };

    Message message = new Message() ;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what ;
            if(i == LOW_POWER){
                handler.post(low_power) ;
            }else if(i == NO_NET){
                handler.post(no_net) ;
            }else if(i == LOW_SIGNAL){
                handler.post(low_signal) ;
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        /*设置电话信号监听*/
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE) ;
        telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        /*注册电量，网络监听*/
        registerReceiver(battaryBroadcast,intentFilter()) ;
        net() ;
    }


//    设置intentFilter
    private IntentFilter intentFilter(){
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED) ; //电量变化
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("SIGNAL") ;
        return intentFilter ;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private class BattaryBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
           /* *//*网络连接*//*
            ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager.getActiveNetworkInfo() == null) {

            }*/

            /*电量过低*/
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int current = intent.getExtras().getInt("level") ;   //当前电量
                int total = intent.getExtras().getInt("scale") ;       //总的电量
                /*try {
                    Thread.sleep(50000) ;*/
                        long time = System.currentTimeMillis() ;

                        int percent = (current * 100) / total;
                        if (percent <= 20) {
                            if((time-timeNow) == 60000) {
                                Log.i("电量过低------------------", "低电量");
                                Message message1 = new Message();
                                message1.copyFrom(message);
                                message1.what = LOW_POWER;
                                handler.sendMessage(message1);
                                timeNow = time ;
                            }
                        }

              /*  }catch (Exception e){}*/
            /*没有信号*/
            }else if(action.equals("SIGNAL")) {
                Message message1 = new Message();
                message1.copyFrom(message);
                message1.what = LOW_SIGNAL ;
                handler.sendMessage(message1) ;
            }

        }
    }


    /* 开始PhoneState监听 */
    private class MyPhoneStateListener extends PhoneStateListener {
        /* 从得到的信号强度,每个tiome供应商有更新 */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (signalStrength.getGsmSignalStrength() <= 1) {
                sendBroadcast(new Intent("SIGNAL"));
            }else{
            }
        }
    }


  /*判断网络连接情况*/
    private boolean isNetworkAvailable() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
    private void net(){
        if(!isNetworkAvailable()){
            Message message1 = new Message() ;
            message1.copyFrom(message);
            message1.what = NO_NET ;
            handler.sendMessage(message1) ;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(battaryBroadcast);
    }
}
