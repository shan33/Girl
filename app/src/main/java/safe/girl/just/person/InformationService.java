
package safe.girl.just.person;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import safe.girl.just.girl.R;

/**
 * Created by 许灡珊 on 2016/7/26.
 */
public class InformationService extends Service {
    private NotificationManager notificationManager;        //消息推送机制
    private Notification notification;                         //消息推送助手
    private int Notification_id = 10;
    private Intent messageIntent;
    private PendingIntent messagePendingIntent;
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    UserInfo userInfo = new UserInfo() ;
    String oldAdd = " ", nowAdd=" " ;
    boolean firstNeigh = true,showHelp = true;
    Date com = new Date() ;
    //传送地理位置信息
    private Intent intent = new Intent();
    Handler handler = new Handler();
    static int i = 1 ;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * 初始化开启线程
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //intent
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        messageIntent = new Intent();
//        messageIntent.putExtra("name","" ) ;
        messagePendingIntent = PendingIntent.getActivity(
                InformationService.this,
                0,
                messageIntent,
                0);

        //开启线程
        handler.post(MyMessage);

        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 消息推送线程
     */
    Runnable MyMessage = new Runnable() {
        @Override
        public void run() {
            try {
//                Log.i("服务器获取求救----","开始休眠") ;
                Thread.sleep(60);     //设置休眠时间点
                Date dateNow = new Date() ;
//                if( (dateNow.getTime()-(com.getTime()+10000*i))<=1000){
//                    Log.i("服务器获取求救----","休眠结束") ;
//                    i++ ;
                    getServerMessage();
//                }
            } catch (Exception e) {
            }
        }
    } ;
    /**
     * 推送消息
     */
    public void getServerMessage() {
        new Thread(){
            @Override
            public void run() {
               // if(i <= 3) {
                    super.run();
                    Log.i("服务器获取求救----", "访问服务器内容");
                    Server_help server_help = new Server_help();
                    Log.i("服务器获取求救传递经纬度----", UserInfo.langtitude  +"\t" + UserInfo.lontitude);
                    list = server_help.seek(UserInfo.langtitude+"", UserInfo.lontitude+"");
                    Log.i("服务器获取求救----", list.size() + "人");
                    if (list.size() >= 1) {
                        for (Map m : list) {
                            userInfo.addNeiborNeed(new NeiborPerson(
                                    (String) m.get("userphon"),
                                    (double) m.get("longitude"),
                                    (double) m.get("latitude"),
                                    (String) m.get("location"))
                            );
                            Log.i("服务器获取求救----", m.get("userphon") + "\t" + m.get("location"));
                        }
                        Looper.prepare();
                        String serverMessage = getAddress();
                        //send有关地理信息
                        intent.setAction(SystemConstant.NEIGGBOR_ID);
                        Bundle bundle = new Bundle();
                        bundle.putString("address", getAddress());
                        intent.putExtras(bundle);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        if (!(serverMessage == null) || !(serverMessage.equals(""))) {
                            notification = new Notification.Builder(InformationService.this)
                                    .setTicker("来自Girl的新消息")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentText(serverMessage)
                                    .setContentIntent(messagePendingIntent)
                                    .setDefaults(Notification.DEFAULT_SOUND)
                                    .setContentIntent(pendingIntent)
                                    .getNotification();
                            Log.i("获取求救信息----", "显示notification");
                            if (showHelp) {
                                notificationManager.notify(Notification_id, notification);
                                sendBroadcast(intent);
                                Notification_id++;
                            }
                            Log.i("获取求救信息----", "发送周围人求救信息，以获取显示");
                            Log.i("周围人帮助", "发送信号");
                            Looper.loop();
                        }
                  //  }else{
                  //      handler.removeCallbacks(MyMessage);

                 //   }
                }
        }}.start();
        handler.postDelayed(MyMessage,100000) ;
        i += 1 ;
    }

    /**
     * 返回地理信息
     */
    private String getAddress() {
        Log.i("获取求救信息----","获取list位置") ;
        if(UserInfo.neiborList != null
                &&
                UserInfo.neiborList.size()!=0) {
            Log.i("服务器获取求救----", UserInfo.neiborList.get(0).getLocation());
            if (UserInfo.neiborList.get(0).getLocation() != null && UserInfo.neiborList.get(0).getLocation() != "") {
                if (firstNeigh) {
                    oldAdd = UserInfo.neiborList.get(0).getLocation();
                } else {
                    nowAdd = UserInfo.neiborList.get(0).getLocation();
                }
                if (nowAdd.equals(oldAdd)) {
                    oldAdd = nowAdd;
                    return "";
//                showHelp = false ;
                } else {
                    oldAdd = nowAdd;
                    showHelp = true;
                    return UserInfo.neiborList.get(0).getLocation();
                }
            }
        }
        Log.i("服务器获取求救----","没有位置信息") ;
        return null ;
    }
}
