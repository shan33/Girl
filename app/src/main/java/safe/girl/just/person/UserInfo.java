package safe.girl.just.person;

import android.media.Image;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 许灡珊 on 2016/9/10.
 */
public class UserInfo {

    /**通用参数*/
            /**用户基本信息*/
    public static String name = null;          //用户的姓名
    public static String password = null;       //用户的密码
    public static String number = null;       //用户的手机号
    public static Image photo = null;           //用户头像设置
//    public static Integer credit = null;        //用户的信用等级

            /**手机硬件*/
    public static String gps = null;           //用户手机gps信号
    public static String power = null;        //用户现有的手机电量
    public static boolean isNet = false;         //用户联网判断
//    public static String blueTooth = null;      //用户的蓝牙信息
    public static String signal = null;         //手机信号问题
            /**地理信息*/
    public static String fromAddress = null;   //用户的出发地
    public static String toAddress = null;     //用户的到达地
    public static LatLng address = null ;     //用户的到达地
    public static LatLonPoint firstLatlng = null ;     //用户的现在位置经纬度
    public static String  nowAddress = null;    //用户现在地址信息
    public static double langtitude,lontitude = 0;       //用户的经纬度

    /**周围人参数*/
    public static  String  alwaysAddress ;    //周围人不变的地址
    public static List<NeiborPerson> neiborList = new ArrayList<>() ;

    /**用户信息设置*/
                /**基本信息*/
    //    设置用户的名称
    public static void setName(String name){
        UserInfo.name=name ;
    }
    //    设置用户的密码
    public static void setPassword(String password){
        UserInfo.password=password ;
    }
    //    设置用户的电话号码
    public static void setNumber(String number) {
        UserInfo.number = number;
    }
    //    设置用户的头像
    public static void setImageAboutUser(Image image){
        UserInfo.photo=image ;
    }

                /**硬件信息*/
    //    电量
    public static void setPower(String power) {
        UserInfo.power = power;
    }
    //    信号
    public static void setSignal(String signal) {
        UserInfo.signal = signal;
    }
    //    网络
    public static void setIsNet(boolean isNet) {
        UserInfo.isNet = isNet;
    }
                /**地理信息*/
    //    起始点
    public static void setFromAddress(String fromAddress){
        UserInfo.fromAddress=fromAddress ;
    }
    //    去向地址
    public static void setToAddress(String toAddress){
        UserInfo.toAddress=toAddress ;
    }
    //    现在位置
    public static void setNowAddress(String nowAddress) {
        UserInfo.nowAddress = nowAddress;
    }
    //    经度
    public static void setLangtitude(double langtitude) {
        UserInfo.langtitude = langtitude;
    }
    //    维度
    public static void setLontitude(double lontitude) {
        UserInfo.lontitude = lontitude;
    }
    //    经纬度
    public static void setAddress(LatLng address) {
        UserInfo.address = address;
    }
    //  首次定位经纬度
    public static void setFirstLatlng(LatLonPoint firstLatlng) {
        UserInfo.firstLatlng = firstLatlng;
    }
    //添加周围求救
    public static void addNeiborNeed(NeiborPerson neiborPerson){
        neiborList.add(neiborPerson);
    }
    /**周围人不变的地理位置*/
    public static void setAlwaysAddress(String alwaysAddress) {
        UserInfo.alwaysAddress = alwaysAddress;
    }
}


