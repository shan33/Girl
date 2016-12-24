package safe.girl.just.person;

/**
 * Created by 许灡珊 on 2016/9/10.
 * 系统所需的常量
 */
public class SystemConstant {
    public static final String EMERGENCY_ID = "EMERGENCY" ;
    public static final String VIBRATE_ID = "VIBRATING" ;
    public static final String SIGNAL_ID = "NO_SIGNAL" ;
    public static final String POWER_ID = "NO_POWER" ;
    public static final String NET_ID = "NO_NET" ;
    public static  String SETTING_KEY = "HELP" ;

    public static final String NEIGGBOR_ID = "NEIGHBOR" ;  //收取附近消息
    public static final String TIPS = "亲，在某某情况下巴拉巴拉。。。。" ;

    /*map中的消息处理变量*/
    public static final int ROUTE_LOCATION_CHANGE_FIRST = 4 ;
    public static final int ROUTE_LOCATION_CHANGE = 9 ;
    public static final int MARKER_CLICK = 8 ;

    /*修改界面显示*/
    public static final int CHANGE_STYLE = 5 ;
    public static final int RESET = 6 ;
    public static final int LOCATION = 7 ;

    //修改闪光线程的参数
    public static boolean ifRun = true ;
    //修改周边人线程的参数
    public static boolean neighbor = false;
    public static boolean helpNPerson = false ;
}
