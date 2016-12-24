package safe.girl.just.girl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 许灡珊 on 2016/7/20.
 */
//    checkItem
class ViewHolder {
    public TextView name;
    public CheckBox checked;

}
//    选择联系人adapter
public class CheckAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<String> listPhone;
    private ArrayList<Boolean> checkedItem;

    public CheckAdapter(Context context, List<String> list, ArrayList<Boolean> check) {
        this.mInflater = LayoutInflater.from(context);
        this.listPhone = list;
        checkedItem = check;
    }

    public int getCount() {
        return listPhone.size();
    }

    public Object getItem(int position) {
        return listPhone.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final int p = position;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.people_list, null);
            holder.name = (TextView) convertView.findViewById(R.id.check_name);
            holder.checked = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String s = (String) listPhone.get(position);
        holder.name.setText(s);
        holder.checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedItem.set(p, true);
                } else {
                    //update the status of checkbox to unchecked
                    checkedItem.set(p, false);
                }
            }
        });
        return convertView;
    }
//    设置为全部false / true
    public void getTrue(){

    }

}