package safe.girl.just.girl;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import safe.girl.just.person.InformationService;
import safe.girl.just.person.Server_help;
import safe.girl.just.person.SystemFile;

/**
 * Created by ko on 2016/3/17.
 * ��½����
 */
public class LoginActivity extends Activity{
    private EditText accountEdit;             //�˺�
    private EditText passwordEdit;            //��
    // ��
    private Button login;         //��½��ť
    private Button register;      //ע�ᰴť
    private CheckBox rememberpass;   //�Ƿ��ס����Ŀ�
    private TextView login_auto ;
    private String name,password,phone ;         //获取有关的用户名和密码
    boolean nameTrue = false;

    @Override
    protected void onCreate(Bundle savedInsatnceState){
        super.onCreate(savedInsatnceState);
        setContentView(R.layout.login);
        SysApplication.getInstance().addActivity(this);
        accountEdit=(EditText)findViewById(R.id.account);
        passwordEdit=(EditText)findViewById(R.id.password);
        rememberpass=(CheckBox)findViewById(R.id.remember_pass);
        login=(Button)findViewById(R.id.login);
        register=(Button)findViewById(R.id.register);
        login_auto = (TextView)findViewById(R.id.login_auto) ;

        userInfo() ;
        button();
    }

    /**Button 监听*/
    protected void button(){
        //        Button监听
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //            初始化电话和密码as
                phone = accountEdit.getText().toString() ;
                password = passwordEdit.getText().toString() ;
                if(phone.length() == 11){
                    autoLogin() ;
                    //通过电话查找用户名
                    findName(phone,password) ;
                }else{
                    Toast.makeText(LoginActivity.this,"用户名有错",Toast.LENGTH_SHORT).show();
                }
                //自动登录

                /*Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                Bundle userDate = new Bundle() ;
                userDate.putString("name","") ;
                userDate.putString("password",password) ;
                userDate.putString("phone",phone) ;
                intent.putExtras(userDate) ;
                SystemFile f = new SystemFile() ;
                startActivity(intent);*/
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    /**注册获取的结果*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0 && requestCode == 0){
            Bundle bundle = data.getExtras() ;
            name = bundle.getString("name") ;
            password = bundle.getString("pass") ;
            phone = bundle.getString("phone") ;
            accountEdit.setText(phone) ;
            passwordEdit.setText(password);

        }
    }


    /**自动登录设置*/
    private void autoLogin(){
        if(rememberpass.isChecked()){
            SystemFile.writeUserInfoNoAdding(phone+"\n"+password,SystemFile.user);
        }
    }

    /**获取用户名*/
    private void userInfo(){
        File file = new File(SystemFile.user) ;
        if(file.exists() && file.length()!=0){
            try {
                FileInputStream i = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(i) ;
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader) ;

                this.accountEdit.setText(bufferedReader.readLine()) ;
                this.passwordEdit.setText(bufferedReader.readLine()) ;

                bufferedReader.close();
                inputStreamReader.close();
                i.close();
            }catch (Exception e){

            }
        }
    }

    /** 服务器查找用户名*/
    protected void findName(final String phone, String pass){
        final String userName = phone ;
        final String userPass = pass ;
        new Thread(){
            @Override
            public void run() {
                super.run();
                Server_help info=new Server_help();
                nameTrue =info.Login(userName,userPass);
                Looper.prepare();
                if(nameTrue){
                    Intent intent=new Intent(LoginActivity.this,MapCenter.class);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Bundle userDate = new Bundle() ;
                    userDate.putString("name","") ;
                    userDate.putString("password",password) ;
                    userDate.putString("phone",phone) ;
                    intent.putExtras(userDate) ;
                    SystemFile f = new SystemFile() ;
                    startActivity(intent);
                    Looper.loop();
                }else{
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }.start();
//        return nameTrue ;
    }

}
