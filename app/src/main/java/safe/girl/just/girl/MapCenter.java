package safe.girl.just.girl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.services.geocoder.GeocodeSearch;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import safe.girl.just.person.InformationService;
import safe.girl.just.person.SystemConstant;
import safe.girl.just.person.SystemFile;
import safe.girl.just.person.UserInfo;
import safe.girl.just.person.VService;

/**
 * Created by 许灡珊 on 2016/9/12.
 */
public class MapCenter extends Activity  /*implements AMapLocationListener*/  {
    /**界面组件*/
    //taxi界面下的规划路线，重置
    protected static Button location_search,location_reset ;
    protected static ToggleButton walkOrRide ;
    protected static AutoCompleteTextView  location_arrive ;
    public static Context context ;
    protected static FloatingActionButton light,camera,call,sound ;
    /**地图组件*/
    protected static AMap map = null;                  //地图
    protected GeocodeSearch search ;      //查找组件
    protected AMapLocationClientOption option ;
    protected AMapLocationClient client ; //客户
    protected MapView mapView ;           //地图显示
    //定位所需
    protected AMapLocation location ;     //当前位置
    //自定义map修改类
    protected static MyMap myMap ;
    private KeyguardManager keyguardManager ;       //锁屏管理
    private PowerManager powerManager  ;            //电量管理
    /**项目内容处理*/
    UserInfo userInfo = new UserInfo() ;
    MyBroadcast myBroadcast = new MyBroadcast() ;
    MyClick click ;
    MediaPlayer mPlayer ;
    //打开硬件
    boolean onSound=true,onCall=true,onLight=true ;
    boolean onSoundF=true,onCallF=true,onLightF=true,onCameraF=true,onLogin=true ;

    Bundle saved = new Bundle();
    /**记录模式情况*/
    private boolean wORr ;
    protected static Walk walk ;         //步行的类
    protected static Taxi ride ;         //乘车的类
    SystemConstant systemConstant = new SystemConstant() ;

    ShowHelp showHelp = new ShowHelp() ;


    /**消息处理*/
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int m = msg.what ;
            if(m == 1){
            }else if(m== 2){
            }else if(m == SystemConstant.ROUTE_LOCATION_CHANGE_FIRST){
                //        创建一个设置经纬度的CameraUpdate
                CameraUpdate cu = CameraUpdateFactory.changeLatLng(UserInfo.address);
                //        更新地图的显示区域
                map.moveCamera(cu);
            }else if(m == SystemConstant.ROUTE_LOCATION_CHANGE){
                //        创建一个设置经纬度的CameraUpdate
                CameraUpdate cu = CameraUpdateFactory.changeLatLng(UserInfo.address);
                //        更新地图的显示区域
                map.moveCamera(cu);
//                follow();
            }
        }
    };


    /**线程处理*/

    /**广播通知*/
    MyBroadcastIntentFilter boradcastFilter = new MyBroadcastIntentFilter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getBaseContext() ;
        systemConstant.ifRun = true ;
        setContentView(R.layout.gmap);

        this.mapView = (MapView)findViewById(R.id.map) ;
        mapView.onCreate(savedInstanceState);
        Log.i("销毁进程","设置为true") ;

    }
    /**组件获取*/
    void setView(){

        light = (FloatingActionButton)findViewById(R.id.fab_light) ;
        sound = (FloatingActionButton)findViewById(R.id.fab_sound) ;
        call = (FloatingActionButton)findViewById(R.id.fab_call) ;
        camera = (FloatingActionButton)findViewById(R.id.fab_camera) ;
        this.location_arrive = (AutoCompleteTextView)findViewById(R.id.location_arrive) ;
        this.location_reset = (Button)findViewById(R.id.location_reset) ;
        this.location_search = (Button)findViewById(R.id.location_search) ;
        //设置步行模式
        this.walkOrRide = (ToggleButton) findViewById(R.id.walkOrRide) ;
        walkOrRide.setText("步行模式");
        //初始化map
        initMap();
        //开启服务
        startService(new Intent(MapCenter.this,VService.class)) ;
        startService(new Intent(MapCenter.this,TipService.class)) ;
    }
    /**初始化map*/
    protected void initMap(){
        if(this.map == null){
            map = mapView.getMap() ;
            this.myMap = new MyMap() ;
            myMap.init(map);
            walk = new Walk(map) ;         //步行的类
            ride = new Taxi(map) ;         //步行的类
            //初始化Mymap中的步行和乘车的类
            myMap.initWR();
            View view = getLayoutInflater().inflate(R.layout.content_gmap,null) ;
            this.click = new MyClick(/*,map*/view) ;
            initMapOther();
        }
    }
    /**设置有关地图查找*/
    protected void initMapOther(){
        client = myMap.client ;
        option = myMap.option ;
        client.setLocationOption(option) ;
        client.setLocationListener(this.myMap);

        client.startLocation() ;
        search = myMap.search ;
//        search.setOnGeocodeSearchListener(myMap);
    }
    /**添加监听函数*/
    protected void addListener(){
        this.location_reset.setOnClickListener(this.click);
        this.location_search.setOnClickListener(this.click);
        this.walkOrRide.setOnClickListener(this.click);
        this.map.setOnMarkerClickListener(this.click);
        InputTips inputTips = new InputTips() ;
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLightF) {
                    new AlertDialog.Builder(MapCenter.this).
                            setMessage("这个会打开闪光灯哟~ \n\n为了你的安全我们一直在努力！").
                            setNegativeButton("暂时不用", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).
                            setPositiveButton("好的，我试试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (onLight) {
                                        HardHelp.light();   //打开闪光灯
                                        onLight = false;
                                    } else {
                                        HardHelp.lightClose();  //关闭闪光灯
                                        onLight = true;
                                    }
                                }
                            }).
                            setCancelable(false).
                            show();
                    onLightF=false ;
                } else {
                    if (onLight) {
                        HardHelp.light();   //打开闪光灯
                        onLight = false;
                    } else {
                        HardHelp.lightClose();  //关闭闪光灯
                        onLight = true;
                    }
                }
            }
        }) ;
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCallF) {
                    new AlertDialog.Builder(MapCenter.this).
                            setMessage("这个会出现警铃哟~ \n\n为了你的安全我们一直在努力！").
                            setNegativeButton("暂时不用", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).
                            setPositiveButton("好的，我试试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String state = Environment.getExternalStorageState();
                                    if (onCall) {
                                        mPlayer = MediaPlayer.create(context, R.raw.alarm_bell);
                                        mPlayer.start();
                                        onCall = false;
                                    } else {
                                        mPlayer.stop();
                                        onCall = true;
                                    }
                                }
                            }).
                            setCancelable(false).
                            show();
                    onCallF=false;
                }else{
                    if (onCall) {
                        mPlayer = MediaPlayer.create(context, R.raw.alarm_bell);
                        mPlayer.start();
                        onCall = false;
                    } else {
                        mPlayer.stop();
                        onCall = true;
                    }
                }
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSoundF) {
                    new AlertDialog.Builder(MapCenter.this).
                            setMessage("这个会随机产生模拟声音哟~ \n\n为了你的安全我们一直在努力！").
                            setNegativeButton("暂时不用", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).
                            setPositiveButton("好的，我试试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //随机数
                                    Random rand = new Random();
                                    int i = rand.nextInt(10);
                                    if (onSound) {
                                        if (i <= 2) {
                                            mPlayer = MediaPlayer.create(context, R.raw.one);
                                            mPlayer.start();
                                            onSound = false;
                                        } else if (i >= 3 && i <= 6) {
                                            mPlayer = MediaPlayer.create(context, R.raw.two);
                                            mPlayer.start();
                                            onSound = false;
                                        } else if (i >= 7 && i <= 10) {
                                            mPlayer = MediaPlayer.create(context, R.raw.thre);
                                            mPlayer.start();
                                            onSound = false;
                                        }
                                    } else {
                                        mPlayer.stop();
                                        onSound = true;
                                    }
                                }
                            }).
                            setCancelable(false).
                            show();
                    onSoundF=false;
                }else{
                    Random rand = new Random();
                    int i = rand.nextInt(10);
                    if (onSound) {
                        if (i <= 2) {
                            mPlayer = MediaPlayer.create(context, R.raw.one);
                            mPlayer.start();
                            onSound = false;
                        } else if (i >= 3 && i <= 6) {
                            mPlayer = MediaPlayer.create(context, R.raw.two);
                            mPlayer.start();
                            onSound = false;
                        } else if (i >= 7 && i <= 10) {
                            mPlayer = MediaPlayer.create(context, R.raw.thre);
                            mPlayer.start();
                            onSound = false;
                        }
                    } else {
                        mPlayer.stop();
                        onSound = true;
                    }
                }

            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCameraF) {
                    new AlertDialog.Builder(MapCenter.this).
                            setMessage("打开照相机可以拍照司机的车牌号哟~ 拍完之后还可以分享的哟~\n\n为了您的安全，我们一直在继续").
                            setNegativeButton("暂时不用", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).
                            setPositiveButton("好的，我试试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String state = Environment.getExternalStorageState();
                                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                        startActivityForResult(intent, 0);
                                    } else {
                                        Toast.makeText(MapCenter.this, "please to check if there is SD card!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).
                            setCancelable(false).
                            show();
                    onCameraF=false;
                }else{
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(intent, 0);
                    } else {
                        Toast.makeText(MapCenter.this, "please to check if there is SD card!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        this.location_arrive.addTextChangedListener(inputTips);
        addBroadcast();
    }
    /**添加广播接收*/
    protected void addBroadcast() {
//        IntentFilter intentFilterOne = boradcastFilter.getIntentFilter() ;
//        registerReceiver(myBroadcast, intentFilterOne);
        startService(new Intent(this, TipService.class)) ;
        startService(new Intent(this, InformationService.class)) ;
//        startService(new Intent(context, VService.class)) ;
        registerReceiver(myBroadcast,new MyBroadcastIntentFilter().getIntentFilter()) ;
        registerReceiver(showHelp,new IntentFilter("SHOW_HELP")) ;
//        registerReceiver(tipBroadcast,new MyBroadcastIntentFilter().getIntentFilter()) ;
    }
    /**获取传递和紧急处理数据*/
    private void getInfo(){
        //        获取用户有关数据
        Bundle data = getIntent().getExtras() ;
        userInfo.setName(data.getString("name"));
        userInfo.setPassword(data.getString("password"));
        userInfo.setNumber(data.getString("phone"));
        new AlertDialog.Builder(MapCenter.this).setMessage("welcome用户--" +UserInfo.number).show() ;
    }
    /**判断GPS开启状态*/
    public final void gPSIsOPen() {
        LocationManager locationManager  = (LocationManager) getSystemService(Context.LOCATION_SERVICE);         // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);         // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）         boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps){
            new AlertDialog.Builder(MapCenter.this).setTitle("您的GPS并未开启，开启后准确度更高哦~").
                    setNegativeButton("算了吧", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).
                    setPositiveButton("现在去开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);

                        }
                    }).
                    setCancelable(false).
                    show();
        }
//        startService(new Intent(this, VService.class)) ;
    }
    public final void tips(){
       /* new AlertDialog.Builder(MapCenter.this).
                setMessage("点击步行模式可以切换模式，然后您可以输入目的地址获取一条来自高德的规划路线哟~然后有一个参照的底哟，" +
                        "还要记得拍下车牌号或者司机哟~可以分享哟~").setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AlertDialog.Builder(MapCenter.this).
                        setMessage("点击地图的marker可以求救哟，或者剧烈摇晃您的手机也是可以的~").setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                 }).
                        setCancelable(false).
                        show() ;
            }
        }).
                setCancelable(false).
                show() ;*/
    }
    /**高德地图必须重写的方法*/
    int i =0;
    @Override

    protected void onDestroy() {
        super.onDestroy() ;
        if(i == 0) {
            unregisterReceiver(showHelp);
            unregisterReceiver(myBroadcast);
//            systemConstant.ifRun = false;
            this.finish();
        }else{
        }
        mapView.onDestroy();
        Log.i("销毁进程","设置为false") ;
        Log.i("界面","destory" +i) ;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        i = 0;
        Log.i("界面","resume") ;

    }

    @Override
    public void onBackPressed() {
        systemConstant.ifRun = false ;
        Log.i("销毁进程","返回按键设置为false") ;
        super.onBackPressed();
        i = 0 ;
        this.onDestroy();
        mapView.onDestroy();
        //startActivity(new Intent(this,ChooseActivity.class));

       // this.finish();
        Log.i("界面","backpress") ;

    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onDestroy();
        //i = 1 ;
        //this.onDestroy();

        // startActivity(new Intent(MapCenter.this,ChooseActivity.class));
        Log.i("界面","pause") ;



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    /**周围人的问题*/

    private void SavePicInLocal(Bitmap bitmap) {      //保存图片函数
        int i=0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();
            String saveDir = SystemFile.picture;
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = saveDir + "/" + System.currentTimeMillis()+ ".PNG";
            i++;
            File file = new File(fileName);
            file.delete();
            if (!file.exists()) {
                file.createNewFile();
                Log.e("PicDir", file.getPath());
                Toast.makeText(MapCenter.this, fileName, Toast.LENGTH_LONG).show();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        try{
            if(requestCode!=0)
                return;
            super.onActivityResult(requestCode, resultCode, data);
            Uri uri=data.getData();
            if(uri!=null){
                photo=BitmapFactory.decodeFile(uri.getPath());
            }
            if(photo==null){
                Bundle bundle=data.getExtras();
                if(bundle!=null){
                    photo=(Bitmap)bundle.get("data");
                    SavePicInLocal(photo);
                    new AlertDialog.Builder(this).setTitle("是否分享所拍图片至有关位置").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shareMultipalImages() ;
                        }
                    }).setNegativeButton("取消",null).show() ;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void shareMultipalImages(){      //分享图片至第三方平台的函数
        ArrayList<Uri> uriList=new ArrayList<Uri>();
        String path= Environment.getExternalStorageDirectory()+"/Girl/info/pic";
        uriList.add(Uri.fromFile(new File(path)));
        Intent shareIntent=new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "share to"));
    }
    private class ShowHelp extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction() ;
                Bundle b = intent.getExtras();
                String[] add = new String[10];
                String addr;
                for (int i = 1; i < 5; i++) {
                    add[i] = b.getString("" + i);
                    addr = add[i];
                    Log.i("求救周围人显示", "准备显示" + addr);
                    SystemConstant.helpNPerson = true;
                    myMap.searchGo(addr);
                }
//                SystemConstant.helpNPerson = false;
                //SystemConstant.helpNPerson = true ;
                //myMap.searchGo(add);

        }
    }

    @Override
    protected void onStart() {
        i =0;
        super.onStart();
        setView() ;
        getInfo();
        addListener();
        gPSIsOPen();
        if(onLogin) {
            tips();
            onLogin=false ;
        }
       /* setView(saved);
        getInfo();
        addListener();*/
        Log.i("界面","start") ;

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        this.map.moveCamera(CameraUpdateFactory.changeLatLng(UserInfo.address));
        //setView();
//        getInfo();
//        addListener();

        Log.i("界面","restart") ;
    }
}



