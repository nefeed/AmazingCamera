package com.gavin.amazingcamera.base.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gavin.amazingcamera.base.eventbus.ActionBarUpdateEvent;
import com.gavin.amazingcamera.base.eventbus.EventBusConfiguration;

import de.greenrobot.event.EventBus;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public abstract class BaseFragment extends Fragment implements IFragmentBackListener {

    private ActionBarUpdateEvent event = null;

    protected ActionBarUpdateEvent actionUpdateEvent(IActionBarObservable iActionBarObservable) {
        if (event == null) {
            event = new ActionBarUpdateEvent(iActionBarObservable);
        }
        return event;
    }


    protected final String tag = getClass().getSimpleName();
    /*模板方式供应fragment的切换*/
    protected Fragment mSwitchContent = null;
    /*数据恢复及保存*/
    protected Bundle savedState;
    protected final String INTERNAL_BUNDLE_KEY = getClass().getCanonicalName();
    /*eventbus的门面配置*/
    protected EventBusConfiguration configuration = EventBusConfiguration.KEEP_ALIVE_NONE;

    public BaseFragment() {
        configuration = loadEventBusConfiguration();
        log("load configuration:" + configuration);
    }


    protected void log(String value) {
        Log.d(tag, value);
    }

    public void toast(String value) {
        Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();
    }

    protected EventBusConfiguration loadEventBusConfiguration() {
        return EventBusConfiguration.KEEP_ALIVE_NONE;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        log("onAttach$context");
        configuration = loadEventBusConfiguration();
        log("load configuration:" + configuration);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
        if (configuration == EventBusConfiguration.KEEP_ALIVE_ONCREATE_PAIR
                || configuration == EventBusConfiguration.KEEP_ALIVE_ONSTART_PAIR) {
            registerEventBus();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 初始化UI布局
     */
    protected void initUI() {

    }

    /**
     * 初始化事件监听器
     */
    protected void initEvent() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        log("onHiddenChanged:" + isHidden());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        log("setUserVisibleHint:" + isVisibleToUser);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log("onViewCreated");
        initUI();
        initEvent();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log("onActivityCreated");
        if (!restoreStateFromArguments()) {
            onFirstTimeLaunched();
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        log("onViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (configuration == EventBusConfiguration.KEEP_ALIVE_ONSTART_PAIR) {
            registerEventBus();
        }
        log("onStart");
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        log("onResume");
        // Umeng的框架
//        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        log("onPause");
//        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        log("onStop");
        if (configuration == EventBusConfiguration.KEEP_ALIVE_ONSTART_PAIR) {
            unregisterEventBus();
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        log("onLowMemory");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");
        if (configuration == EventBusConfiguration.KEEP_ALIVE_ONCREATE_PAIR) {
            unregisterEventBus();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        log("onDetach");
    }

    protected void onFirstTimeLaunched() {
    }

    /**
     * 请使用{@link #onSaveState(Bundle)}
     *
     * @param outState
     */
    @Override
    @Deprecated
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save State Here
        saveStateToArguments();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Save State Here
        saveStateToArguments();
    }

    private void saveStateToArguments() {
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            if (b != null) {
                b.putBundle(INTERNAL_BUNDLE_KEY, savedState);
            } else {
                Log.w(tag, "strongly recommended to set argument's bundle for fragment, then we can support more exception user cases of fragment.");
            }
        }
    }

    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        if (b != null) {
            savedState = b.getBundle(INTERNAL_BUNDLE_KEY);
        } else {
            Log.w(tag, "strongly recommended to set argument's bundle for fragment, then we can support more exception user cases of fragment.");
        }
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    private void restoreState() {
        if (savedState != null) {
            Log.d(tag, "+ onRestoreState");
            onRestoreState(savedState);
            Log.d(tag, "- onRestoreState");
        }
        initUI();
    }

    protected void onRestoreState(Bundle savedInstanceState) {

    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        Log.d(tag, "+ onSaveState");
        onSaveState(state);
        Log.d(tag, "- onSaveState");
        return state;
    }

    protected void onSaveState(Bundle outState) {

    }

    private final void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
            log("+ registerEventBus");
        }
    }

    private final void unregisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
            log("- unregister");
        }
    }

    protected void switchFragment(int containerId, Fragment to) {
        switchFragment(containerId, this, to);
    }

    /**
     * 默认from的实例会被保留，内部通过hide与show配对来减少对fragment的反复初始化的开销。
     * 若是该Fragment需要更新Activity的Actionbar，则需要重写{@link #onHiddenChanged(boolean)}
     *
     * @param from
     * @param to
     */
    protected void switchFragment(int containerId, Fragment from, Fragment to) {
        switchFragment(containerId, from, to, null, null, true);
    }

    public void switchFragmentWithTag(int containerId, Fragment from, Fragment to, String fragmentTag) {
        switchFragment(containerId, from, to, fragmentTag, null, true);
    }

    public void switchFragmentWithBackStackTag(int containerId, Fragment from, Fragment to, String backStackTag) {
        switchFragment(containerId, from, to, null, backStackTag, true);
    }

    public void switchFragment(int containerId, Fragment from, Fragment to, String tag, String backStackTag, boolean addToBackStack) {
        if (mSwitchContent != to) {
            mSwitchContent = to;
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(from);
            if (!to.isAdded()) {
                ft.add(containerId, to, tag);
            } else {
                ft.show(to);
            }
            if (addToBackStack) {
                ft.addToBackStack(backStackTag);
            }
            ft.commit();
        }
    }

    public void replaceFragmentWithoutBackstack(int containerId, Fragment to) {
        replaceFragment(containerId, to, null, null, false);
    }

    public void replaceFragment(int containerId, Fragment to) {
        replaceFragment(containerId, to, null, null, true);
    }

    public void replaceFragmentWithTag(int containerId, Fragment to, String tag) {
        replaceFragment(containerId, to, tag, null, true);
    }

    public void replaceFragmentWithBackStackTag(int containerId, Fragment to, String backStackTag) {
        replaceFragment(containerId, to, null, backStackTag, true);
    }

    public void replaceFragment(int containerId, Fragment to, String tag, String backStackTag, boolean isAddBackStack) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(containerId, to);
        if (isAddBackStack) {
            ft.addToBackStack(backStackTag);
        }
        ft.commit();
    }

    public void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public boolean onBackPressedInterrupt() {
        return true;
    }

    public void popBackStack() {
        ((BaseActivity) getActivity()).onBackPressed();
    }
}

