package safe.girl.just.girl;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import safe.girl.just.person.Server_help;

/**
 * Created by ko on 2016/4/7.
 * ע�����
 */
public class RegisterActivity extends Activity {
    private EditText account;     //�û���
    private EditText password1;   //����
    private EditText password2;   //�ٴ�ȷ������
    private EditText phone;       //�绰
    private Button register;      //ע�ᰴť
    private Button reset;         //���ð�ť
    boolean isTrue = false ;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContext();
    }

    private void setContext() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);
        SysApplication.getInstance().addActivity(this);        //���û����һ��������
        account = (EditText) findViewById(R.id.acc);
        password1 = (EditText) findViewById(R.id.pw1);
        password2 = (EditText) findViewById(R.id.pw2);
        phone = (EditText) findViewById(R.id.phone);

        register = (Button) findViewById(R.id.reg);
        reset = (Button) findViewById(R.id.reset);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {          //ע�ᴥ���¼���ʵ��
                Log.i("net-----","点击函数") ;
                if(inputRight(password1.getText().toString(),password2.getText().toString())
                        && numberRight(phone.getText().toString() )
                        ) {
                    Toast.makeText(RegisterActivity.this, "success", Toast.LENGTH_SHORT).show() ;
                    final Intent result = getIntent();
                    final Bundle bundle = new Bundle();

                    bundle.putString("name", account.getText().toString());
                    bundle.putString("pass", password1.getText().toString());
                    bundle.putString("phone",phone.getText().toString());

                    final String name = account.getText().toString() ;
                    final String password = password1.getText().toString() ;
                    final String phoneNumber = phone.getText().toString() ;
                    result.putExtras(bundle) ;
                    RegisterActivity.this.setResult(0, result);
                    RegisterActivity.this.finish();
                    new Thread() {
                        @Override
                        public void run() {
                            Log.i("网络----", "开始注册");

                            super.run();
                            Log.i("网络----", "发送注册消息");
                            Server_help info=new Server_help();
                            isTrue = info.Register(name,password,phoneNumber) ;
                            if(isTrue){
                                Looper.prepare();
                                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                // 返回登录
                                Log.i("网络----", "注册成功准备跳转界面");
                                result.putExtras(bundle) ;
                                RegisterActivity.this.setResult(0, result);
                                RegisterActivity.this.finish();
                            }else{
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("失败").show();
                                Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }.start();


                }else{
                }

            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {            //���ô����¼���ʵ��
                account.setText("");
                password1.setText("");
                password2.setText("");
                phone.setText("");
            }
        });


    }

    /**
     * 判断是否输入正确
     */
    private boolean inputRight(String p, String a) {
        if(p.equals(a)){
            return true ;
        }else{
            Toast.makeText(RegisterActivity.this,"两次密码输入不一致",Toast.LENGTH_SHORT).show();
            return false ;
        }

    }
    /**
     * 判断是否电话号码重复
     */
    private boolean numberRight(String number) {
        if(number.length() == 11){
            return true ;
        }else{
            Toast.makeText(RegisterActivity.this,"请输入正确的电话号码",Toast.LENGTH_SHORT).show();
        }
        return false ;
    }

}
