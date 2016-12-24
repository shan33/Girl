package safe.girl.just.girl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by ko on 2016/9/11.
 */
public class ChooseActivity extends Activity {
    Button pattern;
    Button idea;
    Button set,help,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose);
        pattern=(Button)findViewById(R.id.pattern);
        idea=(Button)findViewById(R.id.idea);
        set=(Button)findViewById(R.id.set);
        help=(Button)findViewById(R.id.help);
        back=(Button)findViewById(R.id.back);

        pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        idea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseActivity.this,IdeaActivity.class);
                startActivity(intent);
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseActivity.this,Setting.class);
                startActivity(intent);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseActivity.this,HelpMenu.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               System.exit(0);
            }
        });
    }
}
