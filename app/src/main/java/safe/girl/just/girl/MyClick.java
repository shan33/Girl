package safe.girl.just.girl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;

import java.io.File;
import java.util.ArrayList;

import safe.girl.just.person.SystemConstant;
import safe.girl.just.person.UserInfo;

/**
 * Created by 许灡珊 on 2016/9/13.
 */
public class MyClick extends AsyncTask<Integer,Integer,String> implements View.OnClickListener,AMap.OnMarkerClickListener{
    private AMap map ;
    private MyMap myMap ;
    private Context context ;
    private View view ;
    private FloatingActionButton floatingActionButton;
//    private Marker marker ;
    private Button location_search,location_reset ;
    private ToggleButton walkOrRide ;
    public AutoCompleteTextView location_arrive ;
    private UserInfo userInfo = new UserInfo();
    private HardHelp hardHelp = new HardHelp() ;
    private boolean firstEmergency = true;
    private boolean onCall=true,onLight=true,onCamera=true,onSound=true ;
    //闪光
    Camera.Parameters parameters = null;
    Camera camera = null ;

    //地图
    private Taxi ride ;
    private Walk walk ;
    //地图定位
    private boolean firstLocation = true;

    Runnable emergency = new Runnable() {
        @Override
        public void run() {
            new Emergency() ;
        }
    } ;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int re = msg.what ;
            if(re == SystemConstant.CHANGE_STYLE){
                publishProgress(SystemConstant.CHANGE_STYLE);
            }else if(re == SystemConstant.RESET){
                publishProgress(SystemConstant.RESET);
            }else if(re == SystemConstant.LOCATION){
                publishProgress(SystemConstant.LOCATION);
            }else if(re == SystemConstant.MARKER_CLICK){
                //发送广播

                if(firstEmergency) {
                    Log.i("求救","marker点击，发送EMERGENC广播") ;
                    context.sendBroadcast(new Intent(SystemConstant.EMERGENCY_ID)) ;
                    publishProgress(SystemConstant.MARKER_CLICK);
                    firstEmergency = false;
                }else{
                   /* if(parameters != null){
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);//关闭
                        camera.setParameters(parameters);
                        camera.stopPreview();*/
                    //}
                    firstEmergency = true ;
                }
            }else if(re == 100){

            }
        }
    };
    public MyClick(View view){
        this.map = MapCenter.map ;
        this.context = MapCenter.context ;
        this.view = view ;
        this.location_arrive = /*(AutoCompleteTextView)view.findViewById(R.id.location_arrive)*/MapCenter.location_arrive ;
        this.location_reset  = /*(Button)view.findViewById(R.id.location_reset)*/ MapCenter.location_reset;
        this.location_search = /*(Button)view.findViewById(R.id.location_search) */MapCenter.location_search;
        this.location_reset.setVisibility(View.INVISIBLE);
        this.location_search.setVisibility(View.INVISIBLE);
        //设置步行模式
        this.walkOrRide = /*(ToggleButton) view.findViewById(R.id.walkOrRide)*/MapCenter.walkOrRide ;

        walkOrRide.setText("步行模式");
        ride = MapCenter.ride ;
        walk = MapCenter.walk;
        myMap = MapCenter.myMap ;
        parameters = HardHelp.parameters ;
        camera = HardHelp.camera ;

    }
    /**button组件按钮*/
    @Override
    public void onClick(View v) {
        int id = v.getId() ;
        switch (id){
            case R.id.location_reset:
                Message message = new Message() ;
                message.what = SystemConstant.RESET ;
                handler.sendMessage(message) ;
//                MapCenter.context.sendBroadcast(new Intent(SystemConstant.RESET));
                break;
            case R.id.location_search:
                Message message1 = new Message() ;
                message1.what = SystemConstant.LOCATION ;
                handler.sendMessage(message1) ;
//                context.sendBroadcast(new Intent(SystemConstant.LOCATION));
                break;
            case R.id.walkOrRide:
                Message message2 = new Message() ;
                message2.what = SystemConstant.CHANGE_STYLE ;
                handler.sendMessage(message2) ;
//                publishProgress(1);
//                MapCenter.context.sendBroadcast(new Intent(SystemConstant.CHANGE_STYLE));
                break;

            case R.id.fab_light:
                publishProgress(3);


                break;
        }
    }
    /**点击marker图标*/
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getIcons().get(0) == MyMap.marker.getIcons().get(0)) {
            Message message = new Message();
            message.what = SystemConstant.MARKER_CLICK;
            handler.sendMessage(message);
        }
        return false;
    }

    /**异步加载*/
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(values[0] == SystemConstant.CHANGE_STYLE){
            changeTOrW();
        }else if(values[0] == SystemConstant.LOCATION){
            if(/*this.loc  ation_arrive.getText().toString() != ""*/
                    /*&&*/ this.location_arrive.getText().toString() != "") {
                    Log.i("路线规划","到达位置： " +location_arrive.getText().toString()) ;
//                Toast.makeText(context,this.location_arrive.getText().toString(),Toast.LENGTH_SHORT).show();
                SystemConstant.helpNPerson = false;
                myMap.searchGo(this.location_arrive.getText().toString());
            }else{
                Toast.makeText(context,"请正确输入目的地址",Toast.LENGTH_SHORT).show();
            }
        }else if(values[0] == SystemConstant.RESET){
            this.location_arrive.setText("");
        }else if(values[0] == SystemConstant.MARKER_CLICK){
            handler.post(emergency) ;
        }else if(values[0] == 3){
        }
    }

    @Override
    protected String doInBackground(Integer... params) {
        return null;
    }
    /**模式切换*/
    private void changeTOrW(){
        if (!this.walkOrRide.isChecked()) {
            walkOrRide.setText("乘车模式");
            this.location_reset.setVisibility(View.VISIBLE);
            this.location_search.setVisibility(View.VISIBLE);
            this.location_arrive.setVisibility(View.VISIBLE);
            this.location_arrive.setCompletionHint("请在此输入您的目的位置");
            //                    修改地图显示
            this.ride.changeShowingWay();

        } else {
            walkOrRide.setText("步行模式");
            //                    修改地图显示
            this.walk.changeShowingWay();
            this.location_reset.setVisibility(View.INVISIBLE);
            this.location_search.setVisibility(View.INVISIBLE);
            this.location_arrive.setVisibility(View.INVISIBLE);
        }
    }


}
