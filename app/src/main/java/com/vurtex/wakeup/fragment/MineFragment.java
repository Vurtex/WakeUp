package com.vurtex.wakeup.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vurtex.wakeup.R;
import com.vurtex.wakeup.activity.LoginActivity;
import com.vurtex.wakeup.base.BaseFragment;

import tech.jiangtao.support.kit.callback.DisconnectCallBack;
import tech.jiangtao.support.ui.service.XMPPService;

/**
 * @author Vurtex
 */
public class MineFragment extends BaseFragment {
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
        View view = inflater.inflate(R.layout.fragment_item1_list, container, false);
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
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMPPService.disConnect(new DisconnectCallBack() {
                    @Override
                    public void disconnectFinish() {
                        appPreferences.put("enter", false);
                        appPreferences.put("username", null);
                        appPreferences.put("password", null);
                        LoginActivity.startLogin(getActivity());
                    }
                });
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
