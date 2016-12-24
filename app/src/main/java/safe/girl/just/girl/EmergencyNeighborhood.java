package safe.girl.just.girl;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;

/**
 * Created by 许灡珊 on 2016/9/10.
 * 紧急获取周围人的信息
 */
public class EmergencyNeighborhood {
    private Context context;       //设置全局变量
    private View emergency;        //紧急服务
    private AMap amap;             //获取地图

    public EmergencyNeighborhood(Context context, AMap amap) {
        this.context = context;
        this.amap = amap;
        init();
    }
    //        发送至周围人
    private void init() {

//        new AlertDialog.Builder(context).setTitle("已经发送至周围人").show() ;
        Log.i("发送求救------","") ;

    }
}
