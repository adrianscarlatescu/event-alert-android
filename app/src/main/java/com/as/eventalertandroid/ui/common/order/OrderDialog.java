package com.as.eventalertandroid.ui.common.order;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.enums.id.OrderId;
import com.as.eventalertandroid.net.model.OrderDTO;

import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class OrderDialog extends Dialog {

    @BindView(R.id.dialogOrderListView)
    ListView listView;

    private final OrderAdapter adapter;

    public OrderDialog(@NonNull Context context, OrderId orderId) {
        super(context);
        List<OrderDTO> orders = Session.getInstance().getOrders();
        adapter = new OrderAdapter(context, orders, orderId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_order);
        ButterKnife.bind(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> onItemClicked(adapter.getItem(position).id));
    }

    public abstract void onItemClicked(OrderId selection);

}
