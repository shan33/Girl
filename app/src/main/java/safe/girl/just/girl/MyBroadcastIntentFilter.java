package safe.girl.just.girl;

import android.content.Intent;
import android.content.IntentFilter;

import safe.girl.just.person.SystemConstant;

/**
 * Created by 许灡珊 on 2016/9/26.
 */
public class MyBroadcastIntentFilter {
    protected IntentFilter intentFilter = new IntentFilter();

    public  IntentFilter getIntentFilter() {
        //周围人
        this.intentFilter.addAction(SystemConstant.NEIGGBOR_ID);
        //紧急情况
        this.intentFilter.addAction(SystemConstant.EMERGENCY_ID);
        //没有网络
        this.intentFilter.addAction(SystemConstant.NET_ID);
        //没有电量
        this.intentFilter.addAction(SystemConstant.POWER_ID);
        //没有信号
        this.intentFilter.addAction(SystemConstant.SIGNAL_ID);
        //晃动
        this.intentFilter.addAction(SystemConstant.VIBRATE_ID);
        //打开屏幕
        this.intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        //关闭屏幕
        this.intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        this.intentFilter.addAction("SHOW_HELP");
        return intentFilter;
    }
}
