package com.project.androidtest.app.base;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import com.jakewharton.rxbinding2.view.RxView;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import io.reactivex.functions.Action;

public class BaseFrame implements LifecycleObserver, LifecycleOwner {
    private Set<BaseFrame> frames = new HashSet<>();

    private WeakReference<View> weakContentView;
    private WeakReference<Lifecycle> weakLifecycle;
    private LifecycleRegistry registry;

    public BaseFrame(View contentView, Lifecycle lifecycle) {
        weakContentView = new WeakReference<>(contentView);
        weakLifecycle = new WeakReference<>(lifecycle);
        registry = new LifecycleRegistry(this);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return weakLifecycle.get();
    }

    protected View getContentView() {
        return weakContentView.get();
    }

    protected void addFrame(BaseFrame frame) {
        getLifecycle().addObserver(frame);
        frames.add(frame);
    }

    protected void removeFrame(BaseFrame frame) {
        getLifecycle().removeObserver(frame);
        frames.remove(frame);
    }

    protected Context getContext() {
        View contentView = getContentView();
        return contentView == null ? null : contentView.getContext();
    }

    protected void startActivity(Intent intent) {
        getContext().startActivity(intent);
    }

    protected void startActivityForResult(Intent intent, int requestCode) {
        ((Activity) getContext()).startActivityForResult(intent, requestCode);
    }


    public void subscribeItemClick(int viewId, Action action) {
        View contentView = getContentView();
        if (contentView == null) {
            return;
        }

        RxView.clicks(contentView.findViewById(viewId))
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this::getLifecycle)))
                .subscribe(v -> {
                    try {
                        action.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onLifecycleCreate() {
        registry.markState(Lifecycle.State.CREATED);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onLifecycleStart() {
        registry.markState(Lifecycle.State.STARTED);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onLifecycleResume() {
        registry.markState(Lifecycle.State.RESUMED);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onLifecyclePause() {}


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onLifecycleStop() {}


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onLifecycleDestroy() {
        for (BaseFrame frame : frames) {
            getLifecycle().removeObserver(frame);
        }
        frames.clear();
        registry.markState(Lifecycle.State.DESTROYED);
    }
}