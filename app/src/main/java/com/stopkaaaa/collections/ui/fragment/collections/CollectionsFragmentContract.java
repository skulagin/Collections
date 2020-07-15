package com.stopkaaaa.collections.ui.fragment.collections;

import android.content.Context;

import com.stopkaaaa.collections.base.BaseContract;
import com.stopkaaaa.collections.model.CalculationParameters;
import com.stopkaaaa.collections.model.CalculationResultItem;

import java.util.ArrayList;

public interface CollectionsFragmentContract {
    interface View extends BaseContract.BaseView<Presenter> {
        void setRecyclerAdapterData(ArrayList<CalculationResultItem> list);
        void uncheckStartButton();
        Context getContext();
    }
    interface Presenter extends BaseContract.BasePresenter {
        void onStartButtonClicked (CalculationParameters calculationParameters);
        void setup();
    }
}
