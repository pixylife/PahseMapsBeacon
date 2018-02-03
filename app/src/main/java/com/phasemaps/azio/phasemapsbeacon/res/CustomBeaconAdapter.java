package com.phasemaps.azio.phasemapsbeacon.res;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.phasemaps.azio.phasemapsbeacon.R;
import com.phasemaps.azio.phasemapsbeacon.model.Beacon;

import java.util.List;

/**
 * Created by DELL on 6/8/2017.
 */

public class CustomBeaconAdapter extends BaseAdapter{
    private Context context;
    private List<Beacon> listItem;

    public CustomBeaconAdapter(Context context, List<Beacon> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    public CustomBeaconAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            LayoutInflater systemService = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            currentView = systemService.inflate(R.layout.fragment_beacon_list_view, null);
        }

        Beacon b =listItem.get(position);

        TextView idText = (TextView) currentView.findViewById(R.id.idtextView);

        idText.setText(b.getIdBeacon().toString());
        TextView nameText = (TextView) currentView.findViewById(R.id.nametextView);
        nameText.setText(b.getSubject());


        return currentView;    }
}
