package safe.girl.just.person;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 许灡珊 on 2016/8/4.
 */
public class VService extends Service implements SensorEventListener{
    private static final int LOW_INTERNAL = 30 ;           //摇晃检测时间
    private static final int HIGH_INTERNAL = 100 ;           //摇晃检测时间
    boolean first = true ;                              //判断是否第一次检测


    //判断手机晃动
    private SensorManager sensorManager ;
    Sensor sensor ;

    //上一次检测的时间
    long lastTime ;
    float lastX,lastY,lastZ ;

    @Override
    public void onCreate() {
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE) ;
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       Log.i("晃动--------","开始") ;
//        Toast.makeText(this,"startCommend",Toast.LENGTH_SHORT).show();
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL) ;
        lastTime = System.currentTimeMillis() ;
//        lastX = lastY = lastZ = System.currentTimeMillis() ;
        return super.onStartCommand(intent, flags, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    void sendVibrate(){
        Intent V = new Intent(SystemConstant.VIBRATE_ID) ;
        Log.i("晃动--------【","发送广播") ;
        sendBroadcast(V);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float nowX,nowY,nowZ ;
        nowX = event.values[0] ;
        nowY = event.values[1] ;
        nowZ = event.values[2] ;

        long nowTime = System.currentTimeMillis() ;

        if(first) {
            this.lastTime = nowTime;
            lastX = nowX ;
            lastY = nowY ;
            lastZ = nowZ ;
            first = false ;
        }else {

            long timeInternal = nowTime - lastTime;

            if (timeInternal < LOW_INTERNAL ) {
                this.lastTime = nowTime;
                lastX = nowX;
                lastY = nowY;
                lastZ = nowZ;
            } else if(timeInternal > HIGH_INTERNAL){
                double time = Math.sqrt((nowX - lastX) * (nowX - lastX) + (nowY - lastY) * (nowY - lastY) + (nowZ - lastZ) * (nowZ - lastZ))
                        / timeInternal * 10000;
                Log.i("晃动--------【",""+time) ;
                if (time > 200) {
                    Log.i("晃动--------【",""+time) ;
                    sendVibrate();
                    this.lastTime = nowTime;
                    lastX = nowX;
                    lastY = nowY;
                    lastZ = nowZ;
                }
            }
        }
    }

}
