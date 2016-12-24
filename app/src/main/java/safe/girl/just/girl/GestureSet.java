package safe.girl.just.girl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by 许灡珊 on 2016/4/14.
 */
public class GestureSet extends Activity implements Spinner.OnItemSelectedListener{
    GestureOverlayView drawGesture;
    private AppFileSet appFileSet;       //文件设置
    private String gstureName = "";             //手势名称
    private String nameSet = "" ;
    private String[] names = {"报警","语音识别","模拟声音","紧急求救","闪光灯"} ;
    private GestureLibrary gestureLibrary ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_gesture);

        appFileSet = new AppFileSet(this);
        drawGesture = (GestureOverlayView) findViewById(R.id.drawGesture);
        drawGesture.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay, final Gesture gesture) {
                final View saveDialog = getLayoutInflater().inflate(R.layout.name_gesture, null);
                ImageView gestureImage = (ImageView) saveDialog.findViewById(R.id.gestureImage);

                //选择相关的选项mmmm
                Spinner list = (Spinner) saveDialog.findViewById(R.id.selectGestureName);
                list.setOnItemSelectedListener(GestureSet.this);

                //图像设置
                final Bitmap bitmap = gesture.toBitmap(400, 400, 10, 0xffff0000);
                gestureImage.setImageBitmap(bitmap);

                //弹出对话框
                new AlertDialog.Builder(GestureSet.this).setView(saveDialog)
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SavePicInLocal(bitmap, getName());


                                /*gesture文件*/

                                GestureLibrary gestureLibrary = GestureLibraries.fromFile(getFile()) ;

                                if(gestureLibrary.load()){
                                                    //加载手势
                                                    Toast.makeText(GestureSet.this,"加载成功",Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(getApplicationContext(),"加载失败",Toast.LENGTH_LONG).show();
                                                }
//                                        添加手势
                                                gestureLibrary.addGesture(gstureName, gesture);
                                                gestureLibrary.save() ;
                                                if(gestureLibrary.save()) {
                                                    Toast.makeText(saveDialog.getContext(), "成功", Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(saveDialog.getContext(),"失败",Toast.LENGTH_LONG).show();
                                                }
                                }
                        }).
                        setNegativeButton("取消", null).show();
            }
        });
    }


    public void SavePicInLocal(Bitmap bitmap, String id) {      //����ͼƬ����
        int i = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ByteArrayOutputStream baos = null;
        String fileName;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();
            String saveDir = appFileSet.getUserInfoFile("pic");
            File dir = new File(saveDir);
            if (!dir.exists()) {
                appFileSet.creatFileDirInUser("pic");
            }
            fileName = saveDir + "/" + id + ".PNG";
            i++;
            File file = new File(fileName);
            file.delete();
            if (!file.exists()) {
                file.createNewFile();
                Log.e("PicDir", file.getPath());
                Toast.makeText(GestureSet.this, fileName, Toast.LENGTH_LONG).show();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent ;
        switch ((int)spinner.getItemIdAtPosition(position)){
            case 0:         //报警
                nameSet = "报警" ;
                break;
            case 1:         //语音识别
                nameSet = "语音识别" ;
                break;
            case 2:         //紧急求救
                nameSet = "紧急求救" ;
                break;
            case 3:         //模拟声音
                nameSet = "模拟声音" ;
                break;
            case 4:         //闪光灯
                nameSet = "闪光灯" ;
                break;
        }
    }

    public String getName(){
        return nameSet ;
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        nameSet = "报警" ;
    }

    /**
     *  gesture文件 */
    public File getFile(){
        File file = new File(Environment.getExternalStorageDirectory(), "gestures") ;

        Toast.makeText(GestureSet.this,"可读： "+file.canRead(),Toast.LENGTH_SHORT).show() ;

        if(file.exists()){

            Toast.makeText(GestureSet.this,"存在： ",Toast.LENGTH_SHORT).show() ;

            gestureLibrary = GestureLibraries.fromFile(file) ;

            Toast.makeText(GestureSet.this,"加载 ： " +gestureLibrary.load(),Toast.LENGTH_SHORT).show() ;
            return file ;
        }else{
            try {
                file.createNewFile();

            }catch (Exception e){}
        }
        return null ;
    }
}

