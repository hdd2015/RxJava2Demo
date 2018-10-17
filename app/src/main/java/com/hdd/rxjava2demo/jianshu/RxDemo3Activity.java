package com.hdd.rxjava2demo.jianshu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hdd.rxjava2demo.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 给初学者的RxJava2.0教程(三) https://www.jianshu.com/p/128e662906af
 *
 * 操作符
 */
public class RxDemo3Activity extends AppCompatActivity {

    private static final String TAG = "RxDemo3Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 最简单的变换操作符map:
         它的作用就是对上游发送的每一个事件应用一个函数, 使得每一个事件都按照指定的函数去变化.
         */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                //通过Map, 可以将上游发来的事件转换为任意的类型, 可以是一个Object, 也可以是一个集合
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });

        /**
         * FlatMap:
         * 上游每发送一个事件, flatMap都将创建一个新的水管, 然后发送转换之后的新的事件, 下游接收到的就是这些新的水管发送的数据.
         * 这里需要注意的是, flatMap并不保证事件的顺序, 也就是图中所看到的, 并不是事件1就在事件2的前面
         */
        //subscribeOn() 指定的是上游发送事件的线程, observeOn() 指定的是下游接收事件的线程.

        /**
         * FlatMap应用：
         * 需求：嵌套的网络请求, 首先需要去请求注册, 待注册成功回调了再去请求登录的接口.
         * 解决：登录和注册接口返回的都是一个上游Observable, 而我们的flatMap操作符的作用就是把一个Observable转换为另一个Observable
         */
        /*.observeOn(Schedulers.io()) //回到IO线程去发起登录请求
        .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {//->把一个Observable转换为另一个Observable
                    @Override
                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
                        return api.login(new LoginRequest());
                    }
                })*/

    }
}
