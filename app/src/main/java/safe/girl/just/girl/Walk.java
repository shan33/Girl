package safe.girl.just.girl;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

/**
 * Created by 许灡珊 on 2016/7/13.
 */
public class Walk{
    private static AMap amap ;
    public Walk(AMap aMap){
        this.amap = aMap ;
    }

    /**
     * .修改地图显示方式
     */
    public static void changeShowingWay(){
        //        创建一个放大级别的AMap对象
        CameraUpdate cu = CameraUpdateFactory.zoomTo(20);
        //        设置地图地图的默认放大级别
        amap.moveCamera(cu);
        //        创建一个改变倾斜角度的aMap对象
        CameraUpdate cu1 = CameraUpdateFactory.changeTilt(15) ;
        amap.moveCamera(cu1) ;
    }

    /**
     *  添加线段
     */
    public void addPolylineOptions(PolylineOptions polylineOptions,LatLng old,LatLng one){
        polylineOptions.add(old,one) ;     //添加终点
        polylineOptions.width(15) ;     //设置宽度
        polylineOptions.color(R.color.blue) ;   //设置线条的颜色
        polylineOptions.visible(true) ;        //设置可见性
        polylineOptions.setDottedLine(false) ; //画实线
        polylineOptions.geodesic(true) ;
        amap.addPolyline(polylineOptions) ;
    }

    /**
     *  添加marker
     */
    public void addMarker(LatLng one,String address){
               //        创建一个CircleOptions(用于向地图上添加圆形)
        CircleOptions circleOptions = new CircleOptions()
                .center(one) //设置圆心
                .radius(12)                  //设置圆形的半径
                .strokeWidth(6)               //设置圆形的线条宽度
                .strokeColor(0xff000000);    //设置圆形的线条颜色
        amap.addCircle(circleOptions);
    }
}
