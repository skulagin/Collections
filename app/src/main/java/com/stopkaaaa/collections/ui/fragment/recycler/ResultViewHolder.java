package com.stopkaaaa.collections.ui.fragment.recycler;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stopkaaaa.collections.R;
import com.stopkaaaa.collections.dto.CalculationResultItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.itemName)
    TextView itemNameTextView;
    @BindView(R.id.itemTime)
    TextView itemTimeTextView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public ResultViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindItem(CalculationResultItem item) {
        itemNameTextView.setText(item.getTitle());
        if (item.isState()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (!item.getTime().equals(itemTimeTextView.getContext().getString(R.string.empty))) {
            StringBuilder time = new StringBuilder(item.getTime())
                    .append(itemTimeTextView.getContext().getString(R.string.ms));
            itemTimeTextView.setText(time.toString());
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
