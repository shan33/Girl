package safe.girl.just.girl;


import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;
/**
 * Created by ko on 2016/5/10.
 * ���ڴ��app�е����л
 */
public class SysApplication extends Application {
    private List<Activity> mList=new LinkedList();   //��Ż������
    private static SysApplication instance;
    private SysApplication(){

    }
    public synchronized  static SysApplication getInstance(){
        if(null==instance){
            instance=new SysApplication();
        }
        return instance;
    }

    public void addActivity(Activity activity){
        mList.add(activity);
    }

    public void exit(){
        try{
            for(Activity activity:mList)
                if (activity != null)
                    activity.finish();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.exit(0);
        }
    }

    public void onLowMemory(){
        super.onLowMemory();
        System.gc();
    }
}
