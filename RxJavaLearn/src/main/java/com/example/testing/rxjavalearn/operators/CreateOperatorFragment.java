package com.example.testing.rxjavalearn.operators;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.testing.rxjavalearn.R;
import com.example.testing.rxjavalearn.util.LogUtil;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * auther: baiiu
 * time: 16/6/6 06 22:03
 * description: 创建操作符
 */
public class CreateOperatorFragment extends BaseFragment {

    @BindView(R.id.bt_create) Button bt_create;


    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //        intClick();

        //        Observable<Long> defer = defer();
        //        Observable<Long> just = just();
        //        longClick(just);

        //        from();

        //        interval();

        //        repeat();

        startWith();

        //        timer();


    }

    /**
     * Returns an Observable that emits a specified item before it begins to emit items emitted by the source
     * Observable.
     */
    private void startWith() {
        //先发射8
        Observable.just(6)
                .startWith(8)
                //.compose(bindToLifecycle())
                .subscribe(LogUtil::d);

    }

    /**
     * Timer会在指定时间后发射一个数字0，注意其也是运行在computation Scheduler
     */
    private void timer() {
        RxView.clicks(bt_create)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .flatMap(aVoid -> Observable.timer(10, TimeUnit.SECONDS))
                .subscribe(LogUtil::d, e -> Logger.d(e.toString()));
    }


    /**
     * Repeat作用在Observable上,会对其重复发射count次
     */
    private void repeat() {
        RxView.clicks(bt_create)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                //.compose(bindToLifecycle())
                .flatMap(aVoid -> Observable.just(6)
                        .repeat(5))
                .subscribe(LogUtil::d, e -> LogUtil.e("error", e));
    }

    /**
     * 间隔一定时间发送一个数字,从0开始.本身运行在 Schedulers.computation() 线程内
     */
    private void interval() {
        Subscription subscribe = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> bt_create.setText("" + aLong), e -> Logger.e(e.toString()));

        RxView.clicks(bt_create)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                //.compose(bindToLifecycle())
                .subscribe(aVoid -> subscribe.unsubscribe(), e -> Logger.e(e.toString()));
    }


    /**
     * from一个一个发送数据,just发送整个集合.
     */
    private void from() {
        RxView.clicks(bt_create)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                //.compose(bindToLifecycle())
                .flatMap(aVoid -> {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < 10; ++i) {
                        list.add("item" + i);
                    }
                    return Observable.from(list);
                })
                .subscribe(Logger::d, e -> Logger.e(e.toString()));
    }


    //======================================================================================
    //======================================================================================
    private void longClick(Observable<Long> observable) {
        RxView.clicks(bt_create)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                //.compose(bindUntilEvent(FragmentEvent.DESTROY))
                .flatMap(aVoid -> observable)
                .subscribe(LogUtil::d);
    }

    /**
     * defer 为每一个观察者创建一个*新的*Observable
     */
    private Observable<Long> defer() {
        return Observable.defer(() -> Observable.just(System.currentTimeMillis()));
    }

    private Observable<Long> just() {
        return Observable.just(System.currentTimeMillis());
    }


    //======================================================================================
    //======================================================================================

    private void intClick() {
        RxView.clicks(bt_create)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                //.compose(bindUntilEvent(FragmentEvent.DESTROY))
                //                .flatMap(aVoid -> createOperator())
                .flatMap(aVoid -> rangeOperator())
                .subscribe(new Subscriber<Integer>() {
                    @Override public void onCompleted() {
                        Logger.d("onCompleted");
                    }

                    @Override public void onError(Throwable e) {
                        Logger.d("onError " + e.toString());
                    }

                    @Override public void onNext(Integer integer) {
                        Logger.d("" + integer);
                    }
                });

    }

    /**
     * range操作符,发射从start开始的count个数
     */
    private Observable<Integer> rangeOperator() {
        return Observable.range(4, 8);
    }


    /**
     * create操作符
     */
    private Observable<Integer> createOperator() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    for (int i = 0; i < 3; ++i) {
                        int temp = new Random().nextInt(10);

                        if (temp > 5) {
                            subscriber.onError(new Throwable("integer bigger than 8"));
                        } else {
                            subscriber.onNext(temp);
                        }
                    }

                    subscriber.onCompleted();

                }
            }
        });
    }

}
