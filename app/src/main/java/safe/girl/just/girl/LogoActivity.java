package safe.girl.just.girl;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by ko on 2016/4/7.
 * logo����
 */
public class LogoActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.logo);
        SysApplication.getInstance().addActivity(this);  //���û������������
        final Intent it=new Intent(LogoActivity.this,LoginActivity.class);
        ImageView imageView=(ImageView)findViewById(R.id.logo);
        final AnimationDrawable anim=(AnimationDrawable) imageView.getDrawable();   //������Դ
        ViewTreeObserver.OnPreDrawListener preDrawListener=new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {     //�����Ĳ���
                anim.start();
                return true;
            }
        };
        imageView.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
        anim.stop();
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                startActivity(it);
            }
        };
        timer.schedule(task,1000*5);
    }
}
