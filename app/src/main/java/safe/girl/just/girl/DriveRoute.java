package safe.girl.just.girl;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import safe.girl.just.person.UserInfo;

/**
 * Created by 许灡珊 on 2016/7/20.
 */
public class DriveRoute extends AsyncTask<Integer,Integer,String> implements RouteSearch.OnRouteSearchListener {
    private RouteSearch routeSearch;                           //查询路线
    private RouteSearch.DriveRouteQuery driveRouteQuery;       //驾车行驶路线
    private LatLonPoint destination, nowAddress;                //地址信息
    private String add ;        //标记位置
    private static AMap amap;
    private Context context;
    private List<DriveStep> steps;


    public DriveRoute(LatLonPoint destination, LatLonPoint nowAddress) {
        this.amap = MapCenter.map;
        this.destination = destination;
        this.nowAddress = nowAddress;
        this.context = MapCenter.context;
        routeSearch = new RouteSearch(context);
        routeSearch.setRouteSearchListener(this);

        /*NavigateArrowOptions navigateArrowOptions = new NavigateArrowOptions() ;
        navigateArrowOptions.add(new LatLng(destination.getLatitude(),destination.getLongitude())) ;
        amap.addNavigateArrow(navigateArrowOptions) ;*/

//        获取路线
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(nowAddress, destination);
        driveRouteQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault,
                null,   //必须经过的多个点
                null,   //必须避开的多个区域
                null); //必须避开的道路
        Log.i("路线规划", "发送道路查找请求");
        routeSearch.calculateDriveRouteAsyn(driveRouteQuery);
    }

    /*驾车路线查询*/
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        Log.i("路线规划", "查找成功");

//        获取路线
        List<DrivePath> drivePaths = driveRouteResult.getPaths();
//        获取每一条路径所包含的多条线段
        steps = drivePaths.get(0).getSteps();

        if (drivePaths.size() >= 2) {
            List<DriveStep> step1 = drivePaths.get(1).getSteps();
            for (DriveStep step : steps) {
                //获取组成路径的多个点
                List<LatLonPoint> points = step.getPolyline();
                List<LatLng> latLngs = new ArrayList<>();
                for (LatLonPoint point : points) {
                    latLngs.add(new LatLng(point.getLatitude(), point.getLongitude()));
                }
                //            向地图添加多个点
                PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngs)
                        .color(0xffff0000)
                        .width(12);
                amap.addPolyline(polylineOptions);
            }
            for (DriveStep step : step1) {
                //获取组成路径的多个点
                List<LatLonPoint> points = step.getPolyline();
                List<LatLng> latLngs = new ArrayList<>();
                for (LatLonPoint point : points) {
                    latLngs.add(new LatLng(point.getLatitude(), point.getLongitude()));
                }
                //            向地图添加多个点
                PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngs)
                        .color(0xffff0000)
                        .width(8);
                amap.addPolyline(polylineOptions);
            }

        } else {

            publishProgress(1);

        }
    }

    /*公交路线查询*/
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    /*步行路线查询*/
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }


    /**异步加载处理*/
    @Override
    protected String doInBackground(Integer... params) {
        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] == 1) {
            Log.i("路线规划", "描点");
            amap.clear();
            for (DriveStep step : steps) {
                //获取组成路径的多个点
                List<LatLonPoint> points = step.getPolyline();
                List<LatLng> latLngs = new ArrayList<>();
                for (LatLonPoint point : points) {
                    latLngs.add(new LatLng(point.getLatitude(), point.getLongitude()));
                }
                //            向地图添加多个点
                PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngs)
                        .color(R.color.grey)
                        .width(12);
                amap.addPolyline(polylineOptions);
                Log.i("路线规划", "描点成功");



                Log.i("路线规划", "添加位置标志");

                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(new LatLng(nowAddress.getLatitude(),nowAddress.getLongitude()));
                markerOptions1.title(UserInfo.nowAddress) ;
//                markerOptions.position(new LatLng(destination.getLatitude(),destination.getLongitude())) ;
                markerOptions1.title("！您现在的位置");
                Marker marker = amap.addMarker(markerOptions1);
                marker.setPosition(new LatLng(nowAddress.getLatitude(),nowAddress.getLongitude()));
                marker.showInfoWindow();       //设置默认显示窗口
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pp));

                MarkerOptions markerOptions2 = new MarkerOptions();
                markerOptions2.position(new LatLng(destination.getLatitude(),destination.getLongitude()));
//                markerOptions.position(new LatLng(destination.getLatitude(),destination.getLongitude())) ;
                markerOptions2.title("！您将到达的位置");
                Marker marker1 = amap.addMarker(markerOptions2);
                marker1.setPosition(new LatLng(destination.getLatitude(),destination.getLongitude()));
                marker1.showInfoWindow();       //设置默认显示窗口
                marker1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.helper));
                Log.i("路线规划", "标志添加成功");
                //new AlertDialog.Builder(context).setMessage("系统为您规划了一条路线，以作为您的初步路线参考")
                  //      .setPositiveButton("我知道了",null) ;

            }
        }
    }
}

