package safe.girl.just.girl;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.amap.api.maps.AMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import safe.girl.just.person.SystemConstant;
import safe.girl.just.person.SystemFile;
import safe.girl.just.person.UserInfo;

/**
 * Created by 许灡珊 on 2016/9/10.
 */
public class Emergency {
    private Context context;       //设置全局变量
    private AMap amap;             //获取地图
    //群发短信
    private static SmsManager smsManager = SmsManager.getDefault() ;     //群发短信
    private static List<String> list = new ArrayList();
    private int num = 0 ;

    /*初始化*/
    public Emergency() {

        this.context = MapCenter.context ;
        this.amap = MapCenter.map ;
        this.handler.post(flashLight) ;
        this.handler.post(flashClose) ;
        this.handler.post(sendSms) ;
        this.handler.post(sound) ;

        init();
    }

    /**线程*/
        /**
         * 发送短信*/
    Runnable sendSms = new Runnable(){
        @Override
        public void run() {
                getContacts() ;
                for(int i=0;i< list.size();i++){
                    smsManager.sendTextMessage(
                            list.get(i),
                            null,
                            "我现在在： ---（ " + UserInfo.nowAddress+" ） 遇到意外，请快来帮我！" +
                                    "\n\n---Girl",
                            null,
                            null);
                }
                Toast.makeText(context.getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show() ;
            }
            /*从文件获取联系人并设置*/
        void getContacts() {
            String path = SystemFile.contacts;
            File file = new File(path);
            if (file.exists()) {
                //输入
                try{
                    //打开文件
                    FileInputStream inputStream = new FileInputStream(file) ;
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream) ;
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader) ;
                    String str = "" ;
                    while((str=bufferedReader.readLine()) != null){
                        String[] s = str.split("-") ;       //获取电话号码
                        list.add(s[1]);
                    }
                    //关闭文件
                    inputStream.close();
                    inputStreamReader.close();
                    bufferedReader.close();
                }catch (Exception e){
                }
            }else{
            }
    }
    } ;

    /**
     * 模拟声音*/
    Runnable sound = new Runnable() {
        @Override
        public void run() {
            Random rand = new Random() ;
            int i = rand.nextInt(10) ;
            MediaPlayer mPlayer ;
            /*if(i <= 2) {
                mPlayer = MediaPlayer.create(context, R.raw.one);
                mPlayer.start();
            }else if(i>=3&&i<=6){*/
                mPlayer = MediaPlayer.create(context, R.raw.two);
                mPlayer.start();
            /*}else if(i>=7&&i<=10){
                mPlayer = MediaPlayer.create(context, R.raw.thre);
                mPlayer.start();
            }*/
            handler.post(sound) ;
        }
    } ;
        /**
         * 闪光灯开启*/
    Runnable flashLight = new Runnable() {
        @Override
        public void run() {
            if(SystemConstant.ifRun) {
                if(num >= 8){
                    stop() ;
                }else {
                    HardHelp.light();
                    num += 1;
                }
            }
            try {
                Thread.sleep(200);

            }catch (Exception e){}
            try {
                Thread.sleep(100);
            }catch (Exception e){}
            handler.post(flashLight) ;
        }
    } ;
        /**
         * 闪光灯关闭*/
    Runnable flashClose = new Runnable() {
        @Override
        public void run() {
            try {
                if(num >= 8){
                    stop() ;
                }else {
                    Thread.sleep(100);
                    HardHelp.lightClose();
                    handler.post(flashClose) ;
                }

            }catch (Exception e){}

        }
    } ;

     /**
      * 震动*/
        /**
         *通知附近人*/

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(!SystemConstant.ifRun){
                stop();
            }

        }
    } ;
    public static void init(){

    }
    public void stop(){
        handler.removeCallbacks(flashLight) ;
        handler.removeCallbacks(flashClose) ;
        handler.removeCallbacks(sound);
        handler.removeCallbacks(sendSms);


    }
}
