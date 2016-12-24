package safe.girl.just.girl;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ko on 2016/4/7.
 * �˳����棬�����н�������
 */
public class Taxi{

    private static AMap amap ;
    public Taxi(AMap aMap){
        this.amap = aMap ;
    }
//    修改地图显示方式
    public static void changeShowingWay(){
        //        创建一个放大级别的AMap对象
        CameraUpdate cu = CameraUpdateFactory.zoomTo(13);
        //        设置地图地图的默认放大级别
        amap.moveCamera(cu);
        //        创建一个改变倾斜角度的aMap对象
        CameraUpdate cu1 = CameraUpdateFactory.changeTilt(10) ;
        amap.moveCamera(cu1) ;
    }
    //    添加线段
    public void addPolylineOptions(PolylineOptions polylineOptions,LatLng old, LatLng one){
        polylineOptions.add(old,one) ;     //添加终点
        polylineOptions.width(15) ;     //设置宽度
        polylineOptions.color(R.color.blue) ;   //设置线条的颜色
        polylineOptions.visible(true) ;        //设置可见性
        polylineOptions.setDottedLine(false) ; //画实线
        polylineOptions.geodesic(true) ;
        amap.addPolyline(polylineOptions) ;
    }
    //    添加marker
    public void addMarker(LatLng one,String address) {
        //        创建一个CircleOptions(用于向地图上添加圆形)
        CircleOptions circleOptions = new CircleOptions()
                .center(one) //设置圆心
                .radius(10)                  //设置圆形的半径
                .strokeWidth(6)               //设置圆形的线条宽度
                .strokeColor(0xff000000);    //设置圆形的线条颜色
        amap.addCircle(circleOptions);
    }
}

