package safe.girl.just.girl;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import safe.girl.just.person.NeiborPerson;
import safe.girl.just.person.Server_help;
import safe.girl.just.person.SystemConstant;
import safe.girl.just.person.SystemFile;
import safe.girl.just.person.UserInfo;

/**
 * Created by 许灡珊 on 2016/9/10.
 * 对没电量，没信号，没网络的监听
 */
public class MyBroadcast extends BroadcastReceiver{
    private KeyguardManager keyguardManager ;       //锁屏管理
    private PowerManager powerManager  ;            //电量管理
    Vibrator vibrator ;
    List<NeiborPerson> list = new ArrayList<>() ;
    @Override
    public void onReceive(Context context, Intent intent) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
        String action = intent.getAction() ;
        if(action.equals(SystemConstant.EMERGENCY_ID)){
            //紧急情况
            long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern,-1) ;           //重复两次上面的pattern
            Log.i("广播处理 （紧急）-----------","接受紧急情况") ;
            sendServer(UserInfo.number,UserInfo.lontitude+"",UserInfo.langtitude+"",UserInfo.nowAddress
                    ,context);
            vibrator.cancel();
        }else if(action.equals(SystemConstant.NEIGGBOR_ID)){
            Log.i("广播处理-----------","收到周围人消息") ;
            Log.i("紧急求救----------","广播收到周围人的消息") ;

        }else if(action.equals(SystemConstant.NET_ID)){
            //没有网络
            long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern,-1) ;           //重复两次上面的pattern
            Log.i("广播处理-----------","没有网络") ;
//            Toast.makeText(context,"没有网络",Toast.LENGTH_SHORT).show() ;
            showHelp("没有网络",context);
            vibrator.cancel();

        }else if(action.equals(SystemConstant.POWER_ID)){
            //没有电量
            long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern,-1) ;           //重复两次上面的pattern
            Log.i("广播处理-----------","没有电量") ;
//            HardHelp.signalVibrate();
            showHelp("没有电量",context) ;
            vibrator.cancel();

        }else if(action.equals(SystemConstant.SIGNAL_ID)){
            //没有信号
            long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern,-1) ;           //重复两次上面的pattern
            showHelp("没有电量",context) ;

//            HardHelp.signalVibrate();
            vibrator.cancel();

        }else if(action.equals(SystemConstant.VIBRATE_ID)){
            //晃动
            long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern,-1) ;           //重复两次上面的pattern
            if(SystemConstant.SETTING_KEY.equals("WORDS")){
                new BaiduVoice(context) ;
            }else{
                //发送紧急广播
                new Emergency() ;
            }
            vibrator.cancel();

        }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            Log.i("广播处理-----------","打开屏幕") ;
            keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE) ;
            //暗屏
            KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock("") ;
            lock.disableKeyguard() ;
            Intent start = new Intent(context,GestureStart.class) ;
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
            context.startActivity(start) ;
        }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
            powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE) ;
            //保持屏幕锁屏
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"my") ;
            wakeLock.acquire() ;
        }

    }



    protected void showHelp(String data,Context context){
        new AlertDialog.Builder(context).setMessage("目前-您的手机："+data+"\n"
            +"注意--请注意走在周围人多的地方,注意自身财产和人身安全\n您可以点击悬浮按钮启动相应的闪光灯等应用")
                .setPositiveButton("我知道了",null)
                .show() ;
    }

    /**发送服务器*/
    protected void sendServer(final String number, final String lon, final String lan, final String address, final Context context){
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                boolean re = false;
                Server_help server_help = new Server_help();
                re = server_help.help(lan, lon,address,number);
                if (re) {
                    Log.i("紧急求救", "求救成功");
                      Toast.makeText(context, "求救成功", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("紧急求救", "求救失败");
                    Toast.makeText(context, "求救失败", Toast.LENGTH_LONG).show();
                }
                //获取周围人的值显示
                List<Map<String, Object>> list = server_help.searchround(lan, lon);
                Log.i("紧急求救","lan = " +lan +"\t长度" +list.size()) ;
                Log.i("周围人", "help");
                Intent intent = new Intent("SHOW_HELP");
                Bundle bundle = new Bundle();
                bundle.putString("1","四川省成都市双流区白家市场");
                bundle.putString("2","四川省成都市双流区四川大学江安校区东门");

                if (list.size()>=2) {
                    for (int i = 3; i < 5; i++) {
                        if (list.get(i).get("location").equals("") || list.get(i).get("location") == null) {
                            bundle.putString("" + i, " ");
                        }else {
                            Log.i("紧急求救", list.get(i).get("location").toString());
                            bundle.putString("" + i, list.get(i).get("location").toString());
                        }
                    }

                }else{
                    bundle.putString("1","四川省成都市双流区四川大学望江校区");
                    bundle.putString("4","四川省成都市双流区四川大学江安校区西园21舍");
                }
                    intent.putExtras(bundle);
                    context.sendBroadcast(intent);
                    SystemConstant.helpNPerson = true;
                    Log.i("求救周围人显示", "发送广播");
                    Looper.loop();
//                }
            }
        }.start();


    }

}
