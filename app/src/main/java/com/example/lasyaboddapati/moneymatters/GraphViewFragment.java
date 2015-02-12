package com.example.lasyaboddapati.moneymatters;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lasyaboddapati on 1/30/15.
 * A fragment containing the Graph view.
 */
public class GraphViewFragment extends Fragment {

    public GraphViewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph_view, container, false);
        return rootView;
    }
}
