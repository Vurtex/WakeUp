/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vurtex.wakeup.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.redpacketsdk.constant.RPConstant;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.flowingdrawer_core.ElasticDrawer;
import com.flowingdrawer_core.FlowingDrawer;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.vurtex.wakeup.Constant;
import com.vurtex.wakeup.DemoHelper;
import com.vurtex.wakeup.R;
import com.vurtex.wakeup.base.BaseActivity;
import com.vurtex.wakeup.db.InviteMessgeDao;
import com.vurtex.wakeup.db.UserDao;
import com.vurtex.wakeup.fragment.MenuListFragment;
import com.vurtex.wakeup.runtimepermissions.PermissionsManager;
import com.vurtex.wakeup.runtimepermissions.PermissionsResultAction;
import com.vurtex.wakeup.ui.ChatActivity;
import com.vurtex.wakeup.ui.ContactListFragment;
import com.vurtex.wakeup.ui.ConversationListFragment;
import com.vurtex.wakeup.ui.GroupsActivity;
import com.vurtex.wakeup.ui.SettingsFragment;

import java.util.List;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity {

    protected static final String TAG = "MainActivity";
    // textview for unread message count
    private TextView unreadLabel;
    // textview for unread event message
    private TextView unreadAddressLable;

    public boolean isConflict = false;
    private Button[] mTabs;
    private ContactListFragment contactListFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    private boolean isExceptionDialogShow = false;
    public Toolbar toolbar;
    private InviteMessgeDao inviteMessgeDao;

    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;
    private FlowingDrawer mDrawer;
    private BroadcastReceiver internalDebugReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        //make sure activity will not in background if user is logged into another device or removed
        if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
            DemoHelper.getInstance().logout(false, null);
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setContentView(R.layout.activity_main);
        // runtime permission for android 6.0, just require all permissions here for simple
        requestPermissions();
        initView();
        //umeng api
        MobclickAgent.updateOnlineConfig(this);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);

        showExceptionDialogFromIntent(getIntent());

        inviteMessgeDao = new InviteMessgeDao(this);
        UserDao userDao = new UserDao(this);

        conversationListFragment = new ConversationListFragment();
        contactListFragment = new ContactListFragment();
        SettingsFragment settingFragment = new SettingsFragment();
        fragments = new Fragment[]{conversationListFragment, contactListFragment, settingFragment};

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
                .add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
                .commit();

        //register broadcast receiver to receive the change of group from DemoHelper
        registerBroadcastReceiver();


        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
        //debug purpose only
        registerInternalDebugReceiver();

        updateCheck();
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getExceptionMessageId(String exceptionType) {
        if (exceptionType.equals(Constant.ACCOUNT_CONFLICT)) {
            return R.string.connect_conflict;
        } else if (exceptionType.equals(Constant.ACCOUNT_REMOVED)) {
            return R.string.em_user_remove;
        } else if (exceptionType.equals(Constant.ACCOUNT_FORBIDDEN)) {
            return R.string.user_forbidden;
        }
        return R.string.Network_error;
    }

    /**
     * show the dialog when user met some exception: such as login on another device, user removed or user forbidden
     */
    private void showExceptionDialog(String exceptionType) {
        isExceptionDialogShow = true;
        DemoHelper.getInstance().logout(false, null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (exceptionBuilder == null)
                    exceptionBuilder = new AlertDialog.Builder(MainActivity.this);
                exceptionBuilder.setTitle(st);
                exceptionBuilder.setMessage(getExceptionMessageId(exceptionType));
                exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        exceptionBuilder = null;
                        isExceptionDialogShow = false;
                        finish();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                exceptionBuilder.setCancelable(false);
                exceptionBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }
        }
    }

    private void showExceptionDialogFromIntent(Intent intent) {
        EMLog.e(TAG, "showExceptionDialogFromIntent");
        if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)) {
            showExceptionDialog(Constant.ACCOUNT_CONFLICT);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false)) {
            showExceptionDialog(Constant.ACCOUNT_REMOVED);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_FORBIDDEN, false)) {
            showExceptionDialog(Constant.ACCOUNT_FORBIDDEN);
        }
    }


    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        intentFilter.addAction(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
                updateUnreadAddressLable();
                if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                } else if (currentTabIndex == 1) {
                    if (contactListFragment != null) {
                        contactListFragment.refresh();
                    }
                }
                String action = intent.getAction();
                if (action.equals(Constant.ACTION_GROUP_CHANAGED)) {
                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
                //red packet code : 处理红包回执透传消息
                if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                }
                //end of red packet code
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * debug purpose only, you can ignore this
     */
    private void registerInternalDebugReceiver() {
        internalDebugReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHelper.getInstance().logout(false, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                finish();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                    }
                });
            }
        };
        IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
        registerReceiver(internalDebugReceiver, filter);
    }

    /**
     * TODO 检测版本并更新
     */
    public void updateCheck() {
    }

    /**
     * init views
     */
    private void initView() {
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
        unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
        mTabs = new Button[3];
        mTabs[0] = (Button) findViewById(R.id.btn_conversation);
        mTabs[1] = (Button) findViewById(R.id.btn_address_list);
        mTabs[2] = (Button) findViewById(R.id.btn_setting);
        // select first tab
        mTabs[0].setSelected(true);
        setupToolbar();
        setupMenu();
    }

    protected void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });
    }

    private void setupMenu() {
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }

//        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
//            @Override
//            public void onDrawerStateChange(int oldState, int newState) {
//                if (newState == ElasticDrawer.STATE_CLOSED) {
//                    Log.i("MainActivity", "Drawer STATE_CLOSED");
//                }
//            }
//
//            @Override
//            public void onDrawerSlide(float openRatio, int offsetPixels) {
//                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
//            }
//        });
    }

    /**
     * on tab clicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                toolbar.setTitle(R.string.session);
                index = 0;
                break;
            case R.id.btn_address_list:
                toolbar.setTitle(R.string.address_book);
                index = 1;
                break;
            case R.id.btn_setting:
                toolbar.setTitle(R.string.setting);
                index = 2;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // set current tab selected
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    public static void startMain(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (exceptionBuilder != null) {
            exceptionBuilder.create().dismiss();
            exceptionBuilder = null;
        }

        unregisterBroadcastReceiver();

        try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
    }

    /**
     * update unread message count
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            unreadLabel.setText(String.valueOf(count));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * update the total unread count
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
                    unreadAddressLable.setVisibility(View.VISIBLE);
                } else {
                    unreadAddressLable.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * get unread event notification count, including application, accepted, etc
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
        return unreadAddressCountTotal;
    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMessageCount();
        for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!isConflict && !isCurrentAccountRemoved) {
            updateUnreadLabel();
            updateUnreadAddressLable();
        }

        // unregister this event listener when this activity enters the
        // background
        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.pushActivity(this);

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message : messages) {
                DemoHelper.getInstance().getNotifier().onNewMsg(message);
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //red packet code : 处理红包回执透传消息
            for (EMMessage message : messages) {
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                final String action = cmdMsgBody.action();//获取自定义action
                if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                    RedPacketUtil.receiveRedPacketAckMessage(message);
                }
            }
            //end of red packet code
            refreshUIWithMessage();
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                updateUnreadLabel();
                if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                }
            }
        });
    }

    public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {
        }

        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
                            username.equals(ChatActivity.activityInstance.toChatUsername)) {
                        String st10 = getResources().getString(R.string.have_you_removed);
                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
                                .show();
                        ChatActivity.activityInstance.finish();
                    }
                }
            });
        }

        @Override
        public void onContactInvited(String username, String reason) {
        }

        @Override
        public void onFriendRequestAccepted(String username) {
        }

        @Override
        public void onFriendRequestDeclined(String username) {
        }
    }

    private void unregisterBroadcastReceiver() {
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private AlertDialog.Builder exceptionBuilder;
    private ConversationListFragment conversationListFragment;

    // user account was removed
    private boolean isCurrentAccountRemoved = false;

    /**
     * check if current user account was remove
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showExceptionDialogFromIntent(intent);
    }

}
