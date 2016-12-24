package safe.girl.just.girl;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import safe.girl.just.person.SystemFile;


/**
 * Created by 许灡珊 on 2016/9/10.
 * 调用系统的闪光灯，照相机，振动器，短信功能。
 */
public class HardHelp{
    protected static Context context ;

    //震动
    protected static Vibrator vibrator ;

    //照相机，闪光
    protected static Camera camera = null ;
    protected static Camera.Parameters parameters = null;

    /**
     * 闪光灯*/
    @Deprecated
    protected static void light() {
        //直接开启
        if(camera==null){
            camera = Camera.open();
        }
        camera.startPreview();
        parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启
        camera.setParameters(parameters);
    }
    protected static void lightClose() {
        if(parameters != null){
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);//关闭
            camera.setParameters(parameters);
            camera.stopPreview();
        }

    }

    /**
     * 紧急连续震动*/
    protected static void EmergencyVibrate() {
        long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern,2);           //重复六次上面的pattern
    }

    /**
     * 通知震动*/
    protected static void signalVibrate(){

    }



    /**
     * 判断gps开启状态
     * */

}
