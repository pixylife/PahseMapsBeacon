package com.phasemaps.azio.phasemapsbeacon.listview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phasemaps.azio.phasemapsbeacon.R;


public class BeaconListView extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_beacon_list_view, container, false);
    }


}
