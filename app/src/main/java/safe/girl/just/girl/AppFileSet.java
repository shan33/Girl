package safe.girl.just.girl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 许灡珊 on 2016/4/14.
 */
//创建系统文件
public class AppFileSet {
     public static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() ;
     public static final String appPathWithSD = path+"/Girl" ;             //系统目录地址
     //static final String appPathWithoutSD =path+"/Girl" ;
     public static final String gesturePath = appPathWithSD +"/" +"Other" ;       //文件中添加其余文件
     public static final String userInfo = appPathWithSD +"/" +"info" ;            //文件中添加用户信息文件

     private Context context ;  //定义上下文

    public AppFileSet(Context context){
        this.context = context ;
    }

    //创建系统文件
    public void createFile(boolean ifSDcard) {

        //        创建系统所需目录
        File file = new File(appPathWithSD);
        file.mkdirs();
        if (file.exists()) {
            Toast.makeText(context, "创建成功！", Toast.LENGTH_LONG).show();
            this.creatFileDir(gesturePath) ;        //创建手势文件夹
            this.creatFileDir(userInfo);            //添加用户信息文件夹
        }
    }

    //    创建文件目录,文件名，文件路径在user中
    public void creatFileDirInUser(String fileName){
        try{
            File file = new File(userInfo +"/" +fileName) ;
            file.mkdirs() ;

        }catch (Exception e){
            Log.i("fileDirFailed", "creatFileDir: Fail ");
        }
    }

    //    创建文件目录,文件名，文件路径在总文件
    public void creatFileDir(String fileName){
        try{
            File file = new File(fileName) ;
            if(!file.exists()){
                file.mkdirs() ;
            }
        }catch (Exception e){
            Log.i("fileDirFailed", "creatFileDir: Fail ");
        }
    }



    //    创建其他有关文件
    public void createFileInGesture(String user){
        try{
            File file = new File(gesturePath + "/" + user);
            if( ! file.exists()){
                file.createNewFile() ;
                file.setReadable(true) ;
                file.setWritable(true) ;
            }
        }catch (Exception e){
            Log.i("fileDirFailed", "creatFileDir: Fail ");
        }
    }


    //    创建用户文件
    public void createUserFile(String user){
        try{
            File file = new File(userInfo +"/" +user ) ;
            file.createNewFile() ;
        }catch (Exception e){
            Log.i("fileDirFailed", "creatFileDir: Fail ");
        }
    }

    //    追加形式写入数据
    public void writeUserInfo(String info,String name){
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(getUserInfoFile(name)),true);
            outputStream.write(info.getBytes());
            outputStream.close();
        }catch (Exception e){

        }
    }

    //    不以追加形式写入数据
    public void writeUserInfoNoAdding(String info,String name){
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(getUserInfoFile(name)));
            outputStream.write(info.getBytes());
            outputStream.close();
        }catch (Exception e){

        }
    }

    //    写入联系人数据
    public void writeContactsInfo(List<String> list,File file){
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            String[] array = list.toArray(new String[list.size()]) ;
            for(int i=0;i<array.length;i++){
                outputStream.write(array[i].getBytes());
            }
            outputStream.close() ;
        }catch (Exception e){

        }
    }

    //    获取文件登录需要数据
    public void getData(File file,String split){
        if(file.exists()){

        }else{
            Log.i("File","文件不存在") ;
        }
    }


    //    获取用户文件
    public String getUserInfoFile(String user){
        return userInfo +"/" +user ;
    }

    //    获取app文件夹
    public String getAppFile(){
        return this.appPathWithSD ;
    }

    //   获取用户文件夹位置
    public String getUserInfo(){
        return userInfo ;
    }

    //获取其余文件夹位置
    public String getGesturePath() {
        return gesturePath;
    }
}
