package safe.girl.just.person;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by 许灡珊 on 2016/9/10.
 * 创建app有关的文件类
 */
public class SystemFile {
            /**目录*/
    public static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() ;
    public static final String appPathWithSD = path+"/Girl" ;             //系统目录地址
    public static final String OtherPath = appPathWithSD +"/" +"Other" ;       //文件中添加其余文件
    public static final String userInfo = appPathWithSD +"/" +"info" ;            //文件中添加用户信息文件
    public static final String picture = userInfo +"/" +"pic" ;            //相片存储位置
    public static final String map = userInfo +"/" +"map" ;            //相片存储位置

            /**文件*/
    public static final String contacts = userInfo +"/" +"contacts.txt" ;            //联系人信息
    public static final String user = userInfo +"/" +"user.txt" ;            // 本地记住用户信息
    public MyFile myFile;
    public SystemFile(){
        myFile = new MyFile() ;
    }




    //    创建文件目录
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
    //    创建文件
    public void createFile(String fileName){
        try{
            File file = new File(fileName ) ;
            file.createNewFile() ;
        }catch (Exception e){
            Log.i("fileDirFailed", "creatFileDir: Fail ");
        }
    }


    //    追加形式写入数据
    public static void writeUserInfo(String info,String name){
        try {
            FileOutputStream outputStream = new FileOutputStream(userInfo+"/"+name,true);
            outputStream.write(info.getBytes());
            outputStream.close();
        }catch (Exception e){

        }
    }
    //    不以追加形式写入数据
    public static void writeUserInfoNoAdding(String info,String name){
        try {
            FileOutputStream outputStream = new FileOutputStream(name);
            outputStream.write(info.getBytes());
            outputStream.close();
        }catch (Exception e){
        }
    }
    //    写入联系人数据
    public static void writeContactsInfo(List<String> list, File file){
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





    private class MyFile{
        public MyFile(){
            creatSystemFileDir();
            creatSystemFile();
        }

        /*创建系统所需目录*/
        void creatSystemFileDir(){
            File file = new File(appPathWithSD);
            file.mkdirs();
            if (file.exists()) {
                creatFileDir(OtherPath) ;        //创建手势文件夹
                creatFileDir(userInfo);            //添加用户信息文件夹
                creatFileDir(picture);            //添加用户信息文件夹
                creatFileDir(map);            //添加离线地图文件夹
            }
        }

        /*创建系统所需文件*/
        void creatSystemFile(){
            File file = new File(userInfo) ;
            if (file.exists()) {
                createFile(contacts) ;        //创建联系人文件
                createFile(user) ;            //本地记住用户信息
            }
        }
    }
}
