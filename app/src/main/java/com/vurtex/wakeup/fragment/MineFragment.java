package com.vurtex.wakeup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vurtex.wakeup.R;
import com.vurtex.wakeup.activity.LoginActivity;

/**
 * @author Vurtex
 */
public class MineFragment extends Fragment {
    private Button mStop;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        mStop = (Button) view.findViewById(R.id.btn_logout);

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        });


        return view;
    }

    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
//            mRefreshLayout.finishRefreshing();
        }
        return true;
    }

}
