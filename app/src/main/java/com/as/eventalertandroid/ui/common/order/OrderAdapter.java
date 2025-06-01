package com.as.eventalertandroid.ui.common.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.enums.id.OrderId;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.model.OrderDTO;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OrderAdapter extends ArrayAdapter<OrderDTO> {

    private final List<OrderDTO> orders;
    private final OrderId orderId;

    public OrderAdapter(@NonNull Context context, List<OrderDTO> orders, OrderId orderId) {
        super(context, R.layout.item_order, orders);
        this.orders = orders;
        this.orderId = orderId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.item_order, parent, false);
            holder.textView = row.findViewById(R.id.itemOrderTextView);
            holder.imageView = row.findViewById(R.id.itemOrderImageView);
            holder.arrowImageView = row.findViewById(R.id.itemOrderArrowImageView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            row = convertView;
        }

        OrderDTO order = orders.get(position);

        holder.textView.setText(order.label);
        ImageHandler.loadImage(holder.imageView, order.imagePath, getContext().getDrawable(R.drawable.item_placeholder));

        if (order.id.name().endsWith("ASCENDING")) {
            holder.arrowImageView.setVisibility(View.VISIBLE);
            holder.arrowImageView.setImageResource(R.drawable.icon_arrow_up);
        } else if (order.id.name().endsWith("DESCENDING")) {
            holder.arrowImageView.setVisibility(View.VISIBLE);
            holder.arrowImageView.setImageResource(R.drawable.icon_arrow_down);
        } else {
            holder.arrowImageView.setVisibility(View.GONE);
        }

        if (order.id == orderId) {
            row.setBackgroundColor(getContext().getColor(R.color.colorItemBackground));
        } else {
            row.setBackgroundColor(0);
        }

        return row;
    }

    @Override
    public OrderDTO getItem(int position) {
        return orders.get(position);
    }

    private static class ViewHolder {
        private TextView textView;
        private ImageView imageView;
        private ImageView arrowImageView;
    }

}
