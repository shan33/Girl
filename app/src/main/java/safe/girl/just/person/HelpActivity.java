package safe.girl.just.person;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import safe.girl.just.girl.R;
import safe.girl.just.girl.SysApplication;

/**
 * Created by ko on 2016/4/7.
 * ��������
 */
public class HelpActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
      SysApplication.getInstance().addActivity(this);
    }
}
