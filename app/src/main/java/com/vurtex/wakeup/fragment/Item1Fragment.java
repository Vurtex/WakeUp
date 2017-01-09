package com.vurtex.wakeup.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.tuesda.walker.circlerefresh.CircleRefreshLayout;
import com.vurtex.wakeup.R;

/**
 * @author Vurtex
 */
public class Item1Fragment extends Fragment {
    private CircleRefreshLayout mRefreshLayout;
    private ListView mList;
    private Button mStop;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Item1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item1_list, container, false);
        mRefreshLayout = (CircleRefreshLayout) view.findViewById(R.id.refresh_layout);
        mList = (ListView) view.findViewById(R.id.list);
        mStop = (Button) view.findViewById(R.id.stop_refresh);

        String[] strs = {
                "The",
                "Canvas",
                "class",
                "holds",
                "the",
                "draw",
                "calls",
                ".",
                "To",
                "draw",
                "something,",
                "you",
                "need",
                "4 basic",
                "components",
                "Bitmap",
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, strs);
        mList.setAdapter(adapter);

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshLayout.finishRefreshing();
            }
        });

        mRefreshLayout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
                    @Override
                    public void refreshing() {
                        // do something when refresh starts
                    }

                    @Override
                    public void completeRefresh() {
                        // do something when refresh complete
                    }
                });

        return view;
    }


}
