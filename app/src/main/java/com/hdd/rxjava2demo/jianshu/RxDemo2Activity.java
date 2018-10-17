package com.hdd.rxjava2demo.jianshu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hdd.rxjava2demo.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 给初学者的RxJava2.0教程(二) https://www.jianshu.com/p/8818b98c44e2
 *
 * 线程控制：
 */
public class RxDemo2Activity extends AppCompatActivity {
    private static final String TAG = "RxDemo2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 上下游默认是在同一个线程工作
         * 在主线程中分别创建上游和下游,
         */
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        /**
         * subscribe()有多个重载的方法,带有一个Consumer参数的方法表示下游只关心onNext事件
         */
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.d(TAG, "onNext: " + integer);
            }
        };
        //------------------------------
        //然后将他们连接在一起
        observable.subscribe(consumer);
        //------------------------------
        /**
         * 上下游默认是在同一个线程工作.
         * 这样肯定是满足不了我们的需求的, 我们更多想要的是这么一种情况, 在子线程中做耗时的操作, 然后回到主线程中来操作UI,
         *
         * 要达到这个目的, 我们需要先改变上游发送事件的线程, 让它去子线程中发送事件,
         * 然后再改变下游的线程, 让它去主线程接收事件. 通过RxJava内置的线程调度器可以很轻松的做到这一点
         */
        observable.subscribeOn(Schedulers.newThread()) //subscribeOn() 指定的是上游发送事件的线程
                .observeOn(AndroidSchedulers.mainThread()) //observeOn() 指定的是下游接收事件的线程.【 每调用一次observeOn() 线程便会切换一次,】
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(mainThread), current thread is: " + Thread.currentThread().getName());
                    }
                }) //doOnNext运行在下游，该方法用来在消费者真正处理事件之前做一下其他处理
                .subscribe(consumer);
        /**
         * 在RxJava中, 已经内置了很多线程选项供我们选择, 例如有
         Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
         Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
         Schedulers.newThread() 代表一个常规的新线程
         AndroidSchedulers.mainThread() 代表Android的主线程
         */

        /**
         * 常用场景
         .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
         .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
         */
    }


}
