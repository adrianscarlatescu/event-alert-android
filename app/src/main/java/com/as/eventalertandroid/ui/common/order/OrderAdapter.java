package com.as.eventalertandroid.ui.common.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.enums.Order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OrderAdapter extends ArrayAdapter<Order> {

    private Order[] orders;
    private Order oldOrder;

    public OrderAdapter(@NonNull Context context, @NonNull Order[] orders, Order oldOrder) {
        super(context, R.layout.item_order, orders);
        this.orders = orders;
        this.oldOrder = oldOrder;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.item_order, parent, false);
            viewHolder.textView = row.findViewById(R.id.itemOrderTextView);
            viewHolder.imageView = row.findViewById(R.id.itemOrderImageView);
            viewHolder.arrowImageView = row.findViewById(R.id.itemOrderArrowImageView);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            row = convertView;
        }

        viewHolder.textView.setText(orders[position].getName());
        viewHolder.imageView.setImageResource(orders[position].getIcon());
        viewHolder.arrowImageView.setImageResource(orders[position].getArrow());

        if (orders[position] == oldOrder) {
            row.setBackgroundColor(getContext().getColor(R.color.colorItemBackground));
        } else {
            row.setBackgroundColor(0);
        }

        return row;
    }

    @Nullable
    @Override
    public Order getItem(int position) {
        return orders[position];
    }

    private static class ViewHolder {
        private TextView textView;
        private ImageView imageView;
        private ImageView arrowImageView;
    }

}
