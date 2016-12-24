package safe.girl.just.girl;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import safe.girl.just.person.Contact;

/**
 * Created by 许灡珊 on 2016/7/23.
 */

/*联系人设置*/
public class ContactChoose{

    //联系人
    private List<String> listPhone = new ArrayList();
    private List<String> reallistPhone /*= new ArrayList()*/;
    ;

    boolean[] checkedItem ;

    private Context context ;
    private AppFileSet appFileSet;
    StringBuffer showInfo = new StringBuffer() ;

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

    public ContactChoose(Context context,AppFileSet appFileSet){
        Log.i("选择联系人---------------", "初始化") ;
        this.context = context ;
        this.appFileSet = appFileSet ;
    }


    /**自定义设置联系人列表*/

         //    设置联系人列表
    public void setPhone() {
        getPhoneContacts();
        //        将checkedItem设置成为false
        checkedItem = new boolean[listPhone.size()] ;
        for(int i=0;i<listPhone.size();i++){
            checkedItem[i] = false ;
        }
        showXML();
    }
        //显示界面
        public void showXML(){
            DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener =
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedItem[which] = isChecked ;
                        }
                    } ;
            new AlertDialog.Builder(context).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    reallistPhone = new ArrayList();
                    for (int i = 0; i < checkedItem.length; i++) {
                        if (checkedItem[i]) {
                            reallistPhone.add(listPhone.get(i) + "\n");
                            showInfo.append(listPhone.get(i) + "\n");
                        }
                    }
                    if (showInfo.length() >= 1) {
                        new AlertDialog.Builder(context).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setTitle("选择的联系人----").setMessage(showInfo.toString()).show();
                        addToFile();
                    }else{
                        new AlertDialog.Builder(context).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setTitle("未选择联系人----").setMessage(showInfo.toString()).show();
                    }
                }
            }).setTitle("请选择紧急情况下需要发送短信的联系人----").setMultiChoiceItems(listPhone.toArray(new String[listPhone.size()]),checkedItem,multiChoiceClickListener).show() ;
        }

         //获取手机联系人
    public void getPhoneContacts() {
        Log.i("选择联系人---------------", "设置联系人列表") ;
        ContentResolver resolver = context.getContentResolver();
        try {
            // 获取手机联系人
            Log.i("选择联系人---------------", "获取手机联系人") ;

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONES_PROJECTION, null, null, null);
            if (phoneCursor != null) {

                while (phoneCursor.moveToNext()) {

                    // 得到手机号码
                    String phoneNumber = phoneCursor
                            .getString(PHONES_NUMBER_INDEX);
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;

                    // 得到联系人名称
                    String contactName = phoneCursor
                            .getString(PHONES_DISPLAY_NAME_INDEX);
                    Contact mContact = new Contact(contactName, phoneNumber + "");
                    listPhone.add(mContact.toString());
                }
                Log.i("选择联系人---------------", ""+listPhone.size()) ;
                phoneCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        this.setPhone();
    }

        //    将联系人添加到本地文件
    public void addToFile(){
        String personFile = appFileSet.getUserInfoFile("contacts.txt") ;
        File file = new File(personFile) ;
        if(file.exists()){
            appFileSet.writeContactsInfo(reallistPhone,file) ;
            showInfo.delete(0,showInfo.length()-1) ;
        }else{
            appFileSet.createUserFile("/contacts.txt");
            appFileSet.writeContactsInfo(reallistPhone,file) ;
            showInfo.delete(0,showInfo.length()-1) ;
        }
    }



}
