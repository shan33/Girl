package safe.girl.just.girl;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import safe.girl.just.person.Server_help;
import safe.girl.just.person.SystemConstant;
import safe.girl.just.person.SystemFile;
import safe.girl.just.person.UserInfo;
import safe.girl.just.person.VService;

/**
 * Created by 许灡珊 on 2016/9/13.
 */
public class MyMap extends AsyncTask<Integer,Integer,String> implements
        GeocodeSearch.OnGeocodeSearchListener,AMapLocationListener{
    AMap map;
    Context context;
    protected GeocodeSearch search;      //查找组件
    protected AMapLocationClientOption option;
    protected AMapLocationClient client; //客户
    protected Walk walk;
    protected Taxi ride;
    protected   String helpAdd ;       //求救位置
    protected  String searchAdd ;

    //更新定位
    private UserInfo userInfo = new UserInfo();
    private LatLng oldLatLng;
    private boolean wORr = true;
    private boolean help_person = true;
    public static Marker marker = null;
    public static List<Marker> markers = new ArrayList<>() ;

    //地图定位
    private LatLonPoint firstPoint = null;
    private LatLonPoint toPoint = null;
    private boolean firstLocation = true;
    private LatLng androidPoint = null ;
    private LatLng gaodePoint = null ;
    private PolylineOptions polylineOptions = new PolylineOptions();           //设置步行的路线点
    //处理助手
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    public MyMap() {
        this.map = MapCenter.map;
        this.context = MapCenter.context;
        client = new AMapLocationClient(context);
        option = new AMapLocationClientOption();
        initOption();
        search = new GeocodeSearch(context);
        client.setLocationOption(option);
        search.setOnGeocodeSearchListener(this);
    }

    /**
     * 初始化map
     */
    public void init(AMap map) {
        map.setMapType(AMap.MAP_TYPE_NAVI);
//        创建一个放大级别的AMap对象
        CameraUpdate cu = CameraUpdateFactory.zoomTo(20);
//        设置地图地图的默认放大级别
        map.moveCamera(cu);
//        创建一个改变倾斜角度的aMap对象
        CameraUpdate cu1 = CameraUpdateFactory.changeTilt(15);
        map.moveCamera(cu1) ;
    }

    /**
     * 初始化walk和ride
     */
    public void initWR() {
        this.walk = MapCenter.walk;
        this.ride = MapCenter.ride;
    }

    /**
     * 解析位置
     */
    public void searchGo(String loc) {
        Log.i("路线规划","开始规划" +loc) ;
        //helpAdd = loc ;
        searchAdd = loc ;
        GeocodeQuery query = new GeocodeQuery(loc, "中国");
        this.search.getFromLocationNameAsyn(query);
    }
    public void searchGo(String[] loc) {
        Log.i("周围人帮助","开始规划" +loc.length) ;
        for(int i=0;i<loc.length;i++) {
            //Log.i("周围人帮助",loc[i]) ;
            searchGo(loc[i]);
//            GeocodeQuery query = new GeocodeQuery(loc[i], "中国");
//            helpAdd = loc[i];
//            this.search.getFromLocationNameAsyn(query);
        }
//        SystemConstant.helpNPerson = false ;
    }

    //根据地理位置名称获取经纬度位置
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//        map.clear();
        GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
        //        获取解析目的位置的经纬度点
        toPoint = address.getLatLonPoint();
        Log.i("路线规划","经纬度： "+ searchAdd+"  "+toPoint.getLatitude() +"//" +toPoint.getLongitude()) ;
        publishProgress(1);

    }

    //根据经纬度获取地理位置信息
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }
    /**
     * 根据控件的选择，重新设置定位参数
     */
    private void initOption() {
        // 设置定位模式为高精度模式
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否需要显示地址信息
        option.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        option.setGpsFirst(false);
        // 设置是否开启缓存
        option.setLocationCacheEnable(false);
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        option.setOnceLocationLatest(false);
        // 设置发送定位请求的时间间隔
        option.setInterval(Long.valueOf(1000));
        option.setHttpTimeOut(500);
//        设置不止一次定位
        option.setOnceLocation(false);
//        GPSOn() ;

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //获取经纬度点
        LatLng newLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        gaodePoint = newLatlng ;
        if((newLatlng.latitude!=0 && newLatlng.longitude!=0
               /* && sameLocation()*/ ) ) {
            userInfo.setAddress(newLatlng);
            userInfo.setLangtitude(newLatlng.latitude);
            userInfo.setLontitude(newLatlng.longitude);
            userInfo.setNowAddress(aMapLocation.getAddress());
            //发送经度纬度到服务器
            publishProgress(0);
            Log.i("定位-----------------", "跟随路线");
        }
    }

    /**
     * 异步加载函数
     */
    @Override
    protected String doInBackground(Integer... params) {

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int i = values[0];
        if (i == 0) {
            Log.i("定位-----------------", "绘制跟随路线");
            super.onProgressUpdate(values);
            CameraUpdate cameraUpdate1 = CameraUpdateFactory.changeLatLng(UserInfo.address);
            if(marker != null) {
                marker.remove();
            }
            this.marker = map.addMarker(new MarkerOptions().
                    position(UserInfo.address)
                    .title(UserInfo.nowAddress));
            marker.setPosition(UserInfo.address) ;
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pp));
            map.moveCamera(cameraUpdate1);
            //发送服务器
            Log.i("定位-----------------", UserInfo.number +"--"+UserInfo.langtitude+"--"+UserInfo.lontitude);
            sendToServer(UserInfo.number,UserInfo.lontitude,UserInfo.langtitude,UserInfo.nowAddress);

            if (firstLocation) {
                context.registerReceiver(new NeightBroad(),new IntentFilter(SystemConstant.NEIGGBOR_ID)) ;
                oldLatLng = UserInfo.address;
                firstPoint = new LatLonPoint(UserInfo.langtitude, UserInfo.lontitude);
                userInfo.setFromAddress(UserInfo.nowAddress);
                firstLocation = false;
               /* MarkerOptions markerOptions = new MarkerOptions() ;
                markerOptions.position(UserInfo.address) ;
                markerOptions.title(UserInfo.nowAddress) ;*/
                //marker = map.addMarker(markerOptions) ;
               // markers.add(marker) ;
                marker.showInfoWindow() ;       //设置默认显示窗口
                option.setHttpTimeOut(100000);      //10秒更新定位
                client.setLocationOption(option);

            } else {
                follow();
            }
        } else if (i == 1) {
            Log.i("路线规划","准备绘路线") ;
            route();
        }else if( i==2){
//            goForNeighbor() ;
        }
    }

    /**
     * 跟踪位置
     */
    private void follow() {
        option.setInterval(50000);
        client.setLocationOption(option);
        LatLng newlatLng = UserInfo.address;
        if (oldLatLng != newlatLng) {
            marker.remove();
            if (wORr) {
                marker.setPosition(newlatLng);
//                walk.addMarker(newlatLng, UserInfo.nowAddress);
                walk.addPolylineOptions(polylineOptions, oldLatLng, newlatLng);
            } else {
                marker.setPosition(newlatLng);
//                ride.addMarker(newlatLng, UserInfo.nowAddress);
                ride.addPolylineOptions(polylineOptions, oldLatLng, newlatLng);
            }
            if(marker != null){
                marker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions() ;
            markerOptions.position(newlatLng) ;
            markerOptions.title(UserInfo.nowAddress) ;
            marker = map.addMarker(markerOptions) ;
            markers.add(marker) ;
            marker.setPosition(UserInfo.address) ;
            marker.showInfoWindow() ;       //设置默认显示窗口
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pp));
            oldLatLng = newlatLng;
        }
    }
    /**
     * 规划路线
     */
    private void route() {
        LatLng n = new LatLng(UserInfo.langtitude, UserInfo.lontitude);
        LatLng t = new LatLng(toPoint.getLatitude(), toPoint.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(toPoint.getLatitude(), toPoint.getLongitude()));
        int loca =(int) AMapUtils.calculateLineDistance(n, t);
        Random rand = new Random() ;
        int i = rand.nextInt(10) ;
        //          规划路线
//        if(this.help_person) {
            if (SystemConstant.helpNPerson) {
                map.addPolyline(new PolylineOptions().
                        add(n, t)
                        .geodesic(true).color(R.color.red).width(10)
                );
                Log.i("求救,周围人帮助", "周围人显示"+searchAdd);
                if(loca < 500){
                    helpAdd = "四川省成都市双流区四川大学江安校区西园21舍" ;
                }else if(loca>1500 && loca<2000){
                    helpAdd = "四川省成都市双流区四川大学江安校区东门" ;
                }else if(loca > 10000){
                    helpAdd = "四川省成都市双流区四川大学望江校区" ;
                }else{
                    helpAdd = "四川省成都市双流区白家市场" ;
                }
                markerOptions.title("周围某人A位置:" + searchAdd + "\n距离您" + (int) loca/*Integer.parseInt(new String(loca+""))*/ + "米");
                Log.i("求救,周围人帮助", "周围某人A位置:" + helpAdd + "\n距离您" + (int) loca);
                Marker marker = map.addMarker(markerOptions);
                marker.setPosition(t);
                marker.showInfoWindow();       //设置默认显示窗口
                if (i >= 0 && i <= 3) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.helper1));
                } else if (i>=4 && i<=7) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.helper2));
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.helper3));
                }
                Log.i("求救,周围人帮助", "周围人setIcon");

            }
            this.help_person = false ;
//            }

            if (!SystemConstant.helpNPerson && !SystemConstant.neighbor) {
                    Log.i("路线规划", "到达函数");
                    new DriveRoute(
                            toPoint,
                            firstPoint);
            }else if((!SystemConstant.helpNPerson && SystemConstant.neighbor)) {
                map.clear();
                Log.i("周围人帮助", "标记位置");
                //添加线段
                map.addPolyline(new PolylineOptions().
                        add(n, t)
                        .geodesic(true).color(R.color.red).width(10)
                );
                markerOptions.title("求救位置: " + helpAdd + "\n距离您的位置约" + (int)(loca) + "米");
                Marker marker = map.addMarker(markerOptions);
                marker.setPosition(t);
                marker.showInfoWindow();       //设置默认显示窗口
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.call_110));
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(n).include(t)
                        .build();
                // 移动地图，所有marker自适应显示。LatLngBounds与地图边缘30像素的填充区域
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
                Log.i("路线规划","到达函数") ;
                Log.i("周围人帮助", "修改周围人标记为false");
                SystemConstant.neighbor = false;
                Log.i("周围人帮助", "修改周围人标记成功");
            }
    }
    //服务器设置
    protected void sendToServer(final String number,final double lon,final double lan,final String address){
        new Thread(){
                @Override
                public void run() {
                    boolean re ;
                    super.run();
                    Server_help info=new Server_help();
                    re = info.gps(number,lon,lan,address) ;
                    if(re) {
                        Log.i("定位-----------------", "更新定位成功");

                    }else
                        Log.i("定位-----------------", "更新定位失败");

                }
            }.start();
    }
    //gps变化

    private class NeightBroad extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SystemConstant.NEIGGBOR_ID)){
                final String add = intent.getExtras().getString("address") ;
                helpAdd = add ;
                new AlertDialog.Builder(context).
                        setMessage("求救信息点位于: "+add).
                        setCancelable(true).
                        setPositiveButton("看看标记", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SystemConstant.neighbor = true ;
                                SystemConstant.helpNPerson = false;
                                searchGo(add);
                            }
                        }).setNegativeButton("算了吧", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).
                        show();
            }

        }
    }

}

