package safe.girl.just.girl;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 许灡珊 on 2016/9/24.
 */
public class InputTips extends AsyncTask<Integer,Integer,String> implements Inputtips.InputtipsListener,TextWatcher {
    ArrayAdapter<String> adapter = null ;
    private AutoCompleteTextView arrive ;
    private String change = "" ;
    public InputTips(){
        this.arrive = MapCenter.location_arrive ;
    }
    /**
     * 开始进行文字监听
     * */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        change  = s.toString() ;
        publishProgress(0);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void afterTextChanged(Editable s) {
    }
    /**
     * 开始输入提示监听
     * */
    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if (i==1000) {// 正确返回
            Log.i("输入提示： ====","正确返回")  ;
            List<String> listString = new ArrayList<String>();
            for (int j = 0; j <list.size(); j++) {
                listString.add(list.get(j).getName());
            }
            this.adapter = new ArrayAdapter<String>
                    (MapCenter.context, android.R.layout.simple_list_item_1, listString);
            publishProgress(1);
        }else{
//            Toast.makeText(PatternActivity.this,destination +":::" +i+"--code",Toast.LENGTH_SHORT).show() ;
        }
    }

    /**异步加载方法*/
    @Override
    protected void onProgressUpdate(Integer... values) {
        if(values[0] == 1) {    //更新text的提示内容
            arrive.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }else if(values[0] == 0){   //提示输入
            InputtipsQuery inputquery = new InputtipsQuery(
                    change,
                    "");
            Inputtips inputTips = new Inputtips(MapCenter.context, inputquery);
            inputTips.setInputtipsListener(this) ;
            inputTips.requestInputtipsAsyn() ;

        }
    }

    @Override
    protected String doInBackground(Integer... params) {
        return null;
    }
}
