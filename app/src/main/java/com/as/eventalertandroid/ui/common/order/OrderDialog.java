package com.as.eventalertandroid.ui.common.order;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.enums.EventsOrder;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class OrderDialog extends Dialog {

    @BindView(R.id.dialogOrderListView)
    ListView listView;

    private final OrderAdapter adapter;

    public OrderDialog(@NonNull Context context, EventsOrder order) {
        super(context);
        adapter = new OrderAdapter(context, EventsOrder.values(), order);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_order);
        ButterKnife.bind(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> onItemClicked(adapter.getItem(position)));
    }

    public abstract void onItemClicked(EventsOrder selection);

}
