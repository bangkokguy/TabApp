package bangkokguy.development.android.tabapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bangkokguy on 11/16/17.
 *
 */

class ListViewAdapter extends BaseAdapter {

    ArrayList<String> list;
    Activity activity;
    LayoutInflater inflater;

    private int numberOfEntries;

    ListViewAdapter(Activity activity, ArrayList<String> list) {
        super();
        this.activity = activity;
        this.list = list;
        this.numberOfEntries = 3;
        inflater = activity.getLayoutInflater();
    }

    void appendItem (String secondColumn) {
        list.add(numberOfEntries++, secondColumn);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return numberOfEntries;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate and init the view which is used to prototype the list columns
        if (convertView==null)
            convertView = inflater.inflate(R.layout.log_entries_columns, null);
        TextView firstColumn = convertView.findViewById(R.id.name);
        TextView secondColumn = convertView.findViewById(R.id.gender);

        if (!(list==null)) {
            firstColumn.setText(Integer.toString(position));
            secondColumn.setText(list.get(position));
        }
        return convertView;
    }
}