package cn.jeterlee.rxhelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dhh.rxlifecycle2.ActivityEvent;
import com.dhh.rxlifecycle2.LifecycleTransformer;
import com.dhh.rxlifecycle2.RxLifecycle;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Rx帮助类
 * <p>
 * 注意的问题：<br>
 * - 1. subscribeOn -- 指定发射事件的线程，填写 Schedulers.io()：I/O操作，线程池管理；
 * observeOn -- 指定消费事件的线程，AndroidSchedulers.mainThread()，主线程；<br>
 * - 2. 对于.compose(...)方法一定要放在.subscribe的上面并且一定不能放在.subscribeOn/.observeOn的前面，这是个大坑！！！<br>
 * - 3. Specifically bind this until onDestroy()，onDestroy时解除订阅，`this.<Long>` is necessary if you're compiling on JDK7 or below.
 * If you're using JDK8+, then you can safely remove it.
 *
 * @author jeterlee
 * @date 2017/11/29 0029
 * @email xqlee120@yeah.net
 */
public class RxHelper {
    private static final String TAG = "RxHelper";
    private Context mContext;

    /**
     * 使用前请先在Activity或Application中注入RxHelper，即注入RxLifecycle。
     * 有两种情况：1. 若定义了BaseActivity，在BaseActivity的onCreate方法里注入RxHelper。
     * 2. 若已继承Application的操作，也可以在onCreate方法中注入。
     *
     * @param context 上下文
     */
    public static void injectRxHelper(@NonNull Context context) {
        if (context instanceof Application) {
            RxLifecycle.injectRxLifecycle((Application) context);
        } else if (context instanceof AppCompatActivity) {
            RxLifecycle.injectRxLifecycle((AppCompatActivity) context);
        } else if (context instanceof Activity) {
            RxLifecycle.injectRxLifecycle((Activity) context);
        } else {
            throw new NullPointerException("RxLifecycle haven't inject...");
        }
    }

    public RxHelper(@NonNull Context context) {
        // RxLifecycle的条件：获取一个能转化成Activity的Context即可。
        if (context instanceof AppCompatActivity || context instanceof Activity) {
            WeakReference<Context> contextWeakReference = new WeakReference<>(context);
            mContext = contextWeakReference.get();
        } else {
            throw new NullPointerException("Context couldn't transform activity, this is, context instanceof activity or fragment...");
        }
    }


    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.with(mContext).<T>bindToLifecycle();
    }

    public <T> LifecycleTransformer<T> bindOnDestroy() {
        return RxLifecycle.with(mContext).bindOnDestroy();
    }

    public <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.with(mContext).bindUntilEvent(event);
    }


    /**
     * 订阅事件
     *
     * @param observable 被观察者(发射事件线程，io线程)
     * @param <T>        泛型参数
     * @return 返回被观察者对象
     */
    public <T> Observable<T> subscribe(Observable<T> observable) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.with(mContext).<T>bindToLifecycle());

        return observable;
    }

    /**
     * 订阅事件
     *
     * @param observable 被观察者(发射事件线程，io线程)
     * @param observer   观察者(消费事件，主线程)
     * @param <T>        泛型参数
     * @return 返回被观察者对象
     */
    public <T> Observable<T> subscribe(Observable<T> observable, Observer<? super Object> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.with(mContext).<T>bindToLifecycle())
                .subscribe(observer);

        return observable;
    }

    /**
     * 订阅事件
     *
     * @param observable 被观察者(发射事件线程，io线程)
     * @param onNext     观察者(消费事件，主线程)，仅仅只关心接收onNext()方法
     * @param <T>        泛型参数
     * @return 返回被观察者对象
     */
    public <T> Observable<T> subscribe(Observable<T> observable, final Consumer<? super T> onNext) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.with(mContext).<T>bindToLifecycle())
                .subscribe(onNext, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                });

        return observable;
    }

    /**
     * 订阅事件
     *
     * @param observable 被观察者(发射事件线程，io线程)
     * @param onNext     观察者(消费事件，主线程)，仅仅只关心接收onNext()方法
     * @param onError    错误
     * @param <T>        泛型参数
     * @return 返回被观察者对象
     */
    public <T> Observable<T> subscribe(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.with(mContext).<T>bindToLifecycle())
                .subscribe(onNext, onError);

        return observable;
    }
}
