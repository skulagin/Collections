package com.stopkaaaa.collections.ui.fragment.mapcollection;

import com.stopkaaaa.collections.base.BaseContract;
import com.stopkaaaa.collections.dto.CalculationParameters;
import com.stopkaaaa.collections.dto.CalculationResultItem;
import com.stopkaaaa.collections.model.Calculator;
import com.stopkaaaa.collections.model.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapCollectionPresenter implements BaseContract.BasePresenter {

    private final BaseContract.BaseView collectionsFragmentContractView;
    private Supplier collectionSupplier;
    private Calculator calculator;
    private final BlockingQueue<Runnable> calculationQueue;
    private final ThreadPoolExecutor calculationThreadPool;

    public MapCollectionPresenter(
            BaseContract.BaseView collectionsFragmentContractView,
            Supplier collectionSupplier,
            Calculator calculator) {
        this.collectionsFragmentContractView = collectionsFragmentContractView;
        this.collectionSupplier = collectionSupplier;
        this.calculator = calculator;
        this.calculationQueue = new LinkedBlockingQueue<Runnable>();
        this.calculationThreadPool = new ThreadPoolExecutor(1, 1,
                50, TimeUnit.MILLISECONDS, calculationQueue);
    }

    @Override
    public void setup() {
        collectionsFragmentContractView.setData(collectionSupplier.getTaskList());
    }

    @Override
    public int getSpanCount() {
        return collectionSupplier.getSpanCount();
    }

    @Override
    public void onCalculationLaunch(CalculationParameters calculationParameters) {
        if (calculationParameters == null) {
            collectionsFragmentContractView.uncheckStartButton();
            return;
        }
        if (calculationParameters.isChecked()) {
            final boolean amountValid = calculationParameters.isAmountValid();
            if (!amountValid) {
                collectionsFragmentContractView.invalidCollectionSize();
            }
            final boolean threadsValid = calculationParameters.isThreadsValid();
            if (!threadsValid) {
                collectionsFragmentContractView.invalidThreadsAmount();
            }
            if (amountValid && threadsValid) {
                collectionsFragmentContractView.showProgressBar(true);
                startCalculation(calculationParameters);
            } else {
                collectionsFragmentContractView.uncheckStartButton();
            }
        } else {
            // stop calculation
            collectionsFragmentContractView.showProgressBar(false);
        }

    }

    public void startCalculation(final CalculationParameters calculationParameters) {
        calculationThreadPool.setCorePoolSize(calculationParameters.getThreads());
        calculationThreadPool.setMaximumPoolSize(calculationParameters.getThreads());
        final Scheduler scheduler = Schedulers.from(calculationThreadPool);

        final List<CalculationResultItem> tasks = collectionSupplier.getTaskList();
        final List<CalculationResultItem> calculationResultItems = new ArrayList<>(tasks);
        final int size = calculationParameters.getAmount();

        final Observable<CalculationResultItem> calculationResultItemObservable =
                Observable.fromIterable(tasks)
                .flatMap(task -> Observable.just(task)
                .map(item -> calculator.calculate(item, size))
                .subscribeOn(scheduler));
        final Observer<CalculationResultItem> observer = new Observer<CalculationResultItem>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull CalculationResultItem calculationResultItem) {
                collectionsFragmentContractView.updateItem(tasks.indexOf(calculationResultItem), calculationResultItem.getTime());
                    calculationResultItems.remove(calculationResultItem);
                if (calculationResultItems.isEmpty()) {
                    collectionsFragmentContractView.uncheckStartButton();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        };
        calculationResultItemObservable.subscribe(observer);
    }
}