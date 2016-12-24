package safe.girl.just.girl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import safe.girl.just.person.InformationService;
import safe.girl.just.person.SystemConstant;

/**
 * Created by 许灡珊 on 2016/8/20.
 */
public class Setting extends Activity implements View.OnClickListener {
    //xml组件
    private Button gestureSet, contacts,init,restart;     //手势，联系人button
    private ListView showPeople ;
    private AppFileSet appFileSet;
    private String[] names = {"报警", "打开照相机", "紧急求救", "模拟声音", "闪光灯"};
    SystemConstant systemConstant = new SystemConstant() ;

    //震动选项
    public static boolean helpStyle = true ;           //默认选项为语音选择
    List<String> list  = new ArrayList<>() ;
    //联系人获取
    private ContactChoose contactChoose ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set);

        //            获取界面组件
        gestureSet = (Button) findViewById(R.id.gesture_setting);
        contacts = (Button) findViewById(R.id.contacts_setting);
        init = (Button)findViewById(R.id.init_setting) ;
        restart = (Button)findViewById(R.id.restart) ;
        showPeople = (ListView) findViewById(R.id.showPeople);


        gestureSet.setOnClickListener(this);
        contacts.setOnClickListener(this);
        init.setOnClickListener(this);
        restart.setOnClickListener(this);

        appFileSet = new AppFileSet(this);
        contactChoose = new ContactChoose(Setting.this,appFileSet) ;

        //获取联系人
//        getContacts() ;
        //开启周边人求助服务
//        Intent service = new Intent(Setting.this, InformationService.class) ;
//        startService(service) ;
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction("HELP");
//        registerReceiver(vb.getVb(), intentFilter) ;
    }



    //    按钮监听
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //            手势设置按钮
            case R.id.gesture_setting:
                startActivity(new Intent(Setting.this,GestureBuilderActivity.class));
                break;
            //            联系人设置
            case R.id.contacts_setting:
                new AlertDialog.Builder(Setting.this).setMessage
                        ("baby,请选择需要的联系人,默认留下的号码为求救的电话号码哟~")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contactChoose.setPhone() ;
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show() ;


                break;
            //           离线地图下载管理
            //          初始化设置，震动响应选择
            case R.id.init_setting:
                setVibrate();
                break;
            //          刷新联系人列表
            case R.id.restart:
                getContacts();
                break;
        }
    }


    /**
     * 选择震动响应
     * */
    private void setVibrate(){
        DialogInterface.OnClickListener on = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    helpStyle = false ;
                    systemConstant.SETTING_KEY = "WORDS" ;
                    Log.i("修改设置------","words") ;
                    //设置剧烈晃动为紧急求救
//                    sendBroadcast(new Intent(SystemConstant.SETTING_KEY));
                }else{
                    systemConstant.SETTING_KEY = "HELP" ;
                    Log.i("修改设置------","help") ;
                }
            }
        } ;
        new AlertDialog.Builder(this).setTitle("当手机产生剧烈晃动现象时候，请选择您要发出的信号的选项： ")
                .setSingleChoiceItems(new String[]{"语音选项（此选项下建议用户尽量不锁屏）","紧急求救"},2,on)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("取消",null)
                .setCancelable(false)
                .show() ;
    }

    /** 从文件获取联系人并设置
     * */
    public void getContacts() {
        String path = appFileSet.getUserInfoFile("contacts.txt");
        File file = new File(path);
        if (file.exists()) {
            //输入
            Toast.makeText(this,"文件存在",Toast.LENGTH_SHORT).show() ;
            try{
                //打开文件
                FileInputStream inputStream = new FileInputStream(file) ;
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream) ;
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader) ;
                String str = "";
                List<String> list  = new ArrayList<>() ;
                while((str=bufferedReader.readLine()) != null){
                    list.add(str) ;
                    Log.i("联系人：-------" , str) ;
                }
                //关闭文件
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Setting.this,android.R.layout.simple_list_item_1,list) ;
                this.showPeople.setAdapter(adapter) ;
                inputStream.close() ;
                inputStreamReader.close() ;
                bufferedReader.close() ;
            }catch (Exception e){

            }
        }else{
            Toast.makeText(this,"文件不存在",Toast.LENGTH_SHORT).show() ;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
