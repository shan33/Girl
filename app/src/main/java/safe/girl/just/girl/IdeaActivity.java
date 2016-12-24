package safe.girl.just.girl;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by ko on 2016/9/13.
 */
public class IdeaActivity extends Activity {
    Button attention;
    Button stratege;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idea);
        attention=(Button)findViewById(R.id.attention);
        stratege=(Button)findViewById(R.id.stratege);

        attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IdeaActivity.this,AttentionActivity.class);
                startActivity(intent);
            }
        });

        stratege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IdeaActivity.this,StrategeActivity.class);
                startActivity(intent);
            }
        });
    }
}
