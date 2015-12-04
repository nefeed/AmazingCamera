package com.gavin.amazingcamera.base.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.gavin.amazingcamera.base.eventbus.EventBusConfiguration;
import com.gavin.amazingcamera.base.util.TaskUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected String tag = getClass().getSimpleName();

    protected void log(String value) {
        Log.d(tag, value);
    }

    protected void toast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    /*eventbus的门面配置*/
    protected EventBusConfiguration configuration = EventBusConfiguration.KEEP_ALIVE_NONE;

    public BaseActivity() {
        configuration = loadEventBusConfiguration();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configuration = loadEventBusConfiguration();
        if (configuration == EventBusConfiguration.KEEP_ALIVE_ONCREATE_PAIR) {
            registerEventBus();
        }
    }

    protected EventBusConfiguration loadEventBusConfiguration() {
        return EventBusConfiguration.KEEP_ALIVE_NONE;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (configuration == EventBusConfiguration.KEEP_ALIVE_ONSTART_PAIR) {
            registerEventBus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng的框架
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng的框架
//        MobclickAgent.onPause(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (configuration == EventBusConfiguration.KEEP_ALIVE_ONSTART_PAIR) {
            unregisterEventBus();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (configuration == EventBusConfiguration.KEEP_ALIVE_ONCREATE_PAIR) {
            unregisterEventBus();
        }
    }

    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
            Log.d(tag, "registerEventBus");
        }
    }

    private void unregisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
            Log.d(tag, "unregisterEventBus");
        }
    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        if (!TaskUtil.isMoveTaskToBack(this, intent))
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        super.startActivityFromFragment(fragment, intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (!TaskUtil.isMoveTaskToBack(this, intent))
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivity(Intent intent) {
        if (!TaskUtil.isMoveTaskToBack(this, intent))
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        super.startActivity(intent);
    }


    private boolean isFragmentStackNotEmpty() {
        /**
         * 最后一个fragment与activity一起销毁
         */
        return getSupportFragmentManager().getBackStackEntryCount() > 1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(tag, "+ onOptionsItemSelected");

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressedImpl(false);
                return true;
            default:
                Log.d(tag, "- onOptionsItemSelected");
                return super.onOptionsItemSelected(item);
        }

    }

    private void onBackPressedImpl(boolean isForceClose) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            Log.d(tag, "no fragments in backstack.");
            finish();
            Log.d(tag, " finish()");

        } else if (fragments.size() == 0) {
            Log.d(tag, "no fragments in backstack.");
            finish();
            Log.d(tag, " finish()");
        } else {
            String value = "[";
            for (Fragment fragment : fragments) {
                value += "<";
                if (fragment == null) {
                    value += null;
                } else {
                    value += fragment.getClass().getSimpleName();
                }

                value += ">";
            }
            Log.d(tag, "back stack fragments :" + value);
            Log.d(tag, "back stack size:" + fragments.size());

            boolean canBack = true;
            // 非强制情况下,检测回退栈事件
            if (!isForceClose) {
                Fragment curFragment = fragments.get(fragments.size() - 1);
                if (curFragment instanceof IFragmentBackListener) {
                    Log.d(tag, "call " + curFragment.getClass().getSimpleName() + " onBackPressedInterrupt");
                    canBack = ((IFragmentBackListener) curFragment).onBackPressedInterrupt();
                }
            }
            if (isFragmentStackNotEmpty()) {
                if (canBack || isForceClose) {
                    Log.d(tag, " popBackStack()");
                    getSupportFragmentManager().popBackStack();
                } else {
                    Log.d(tag, "cancel popBackStack()");
                }
            } else {
                finish();
                Log.d(tag, " finish()");
            }

        }
        Log.d(tag, "- onOptionsItemSelected");
    }

    public void forceBackPressed() {
        onBackPressedImpl(true);
    }

    @Override
    public void onBackPressed() {
        Log.d(tag, "+ onBackPressedInterrupt");
        onBackPressedImpl(false);
        Log.d(tag, "- onBackPressedInterrupt");
    }

}

