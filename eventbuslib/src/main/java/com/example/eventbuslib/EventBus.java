package com.example.eventbuslib;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * @author: lilinjie
 * @date: 2019-05-25
 * @description: 手写EventBus
 */
public class EventBus {

    private final Map<Object, List<SubscribeMethod>> mSubscribeMethodCache;
    private Handler mHandler;

    private EventBus() {
        mSubscribeMethodCache = new HashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static EventBus getInstance() {
        return EventBusHolder.INSTANCE;
    }

    private static class EventBusHolder {
        private static final EventBus INSTANCE = new EventBus();
    }


    /**
     * register the subscriber to EventBus
     *
     * @param subscriber
     */
    public void register(Object subscriber) {
        synchronized (this) {
            if (mSubscribeMethodCache.get(subscriber) == null) {
                List<SubscribeMethod> subscribeMethodList = findSubscribeMethods(subscriber);
                mSubscribeMethodCache.put(subscriber, subscribeMethodList);
            }
        }

    }

    /**
     * find subscribe method from subscriber
     *
     * @param subscriber
     * @return
     */
    private List<SubscribeMethod> findSubscribeMethods(Object subscriber) {
        List<SubscribeMethod> subscribeMethodList = new ArrayList<>();
        Class<?> clazz = subscriber.getClass();
        while (clazz != null) {
            if (clazz.getName().startsWith("java.") || clazz.getName().startsWith("javax.") || clazz.getName().startsWith("android.")) {
                break;
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (subscribe == null) {
                    continue;
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    EventBusLog.log("@Subscribe method only accept one parameter!");
                    continue;
                }
                ThreadMode threadMode = subscribe.threadMode();
                SubscribeMethod subscribeMethod = new SubscribeMethod(method, parameterTypes[0], threadMode);
                subscribeMethodList.add(subscribeMethod);
            }
            clazz = clazz.getSuperclass();
        }
        return subscribeMethodList;

    }

    /**
     * unregister the subscriber from EventBus
     *
     * @param subscriber
     */
    public void unregister(Object subscriber) {
        synchronized (this) {
            mSubscribeMethodCache.remove(subscriber);
        }
    }

    /**
     * send the event to subscriber
     *
     * @param event
     */
    public void post(final Object event) {
        Set<Object> key = mSubscribeMethodCache.keySet();
        for (final Object subscriber : key) {
            List<SubscribeMethod> subscribeMethodList = mSubscribeMethodCache.get(subscriber);
            if (subscribeMethodList != null) {
                for (final SubscribeMethod subscribeMethod : subscribeMethodList) {
                    if (subscribeMethod.getType().isAssignableFrom(event.getClass())) {
                        ThreadMode threadMode = subscribeMethod.getThreadMode();
                        switch (threadMode) {
                            case MAIN:
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    invokeMethod(subscriber, subscribeMethod.getMethod(), event);
                                } else {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            invokeMethod(subscriber, subscribeMethod.getMethod(), event);
                                        }
                                    });
                                }
                                break;
                            case BACKGROUND:
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invokeMethod(subscriber, subscribeMethod.getMethod(), event);
                                    }
                                });
                                break;
                            default:

                        }
                    }
                }
            }
        }

    }

    /**
     * invoke subscribe method by reflect
     *
     * @param subscriber
     * @param method
     * @param event
     */
    private void invokeMethod(Object subscriber, Method method, Object event) {
        try {
            method.invoke(subscriber, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
