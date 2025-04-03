package com.as.eventalertandroid.ui.main.home.list;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.EventDTO;
import com.as.eventalertandroid.ui.common.event.EventDetailsFragment;
import com.as.eventalertandroid.ui.main.MainActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeListFragment extends Fragment implements EventAdapter.ClickListener {

    @BindView(R.id.homeNoResultsTextView)
    TextView noResultsTextView;
    @BindView(R.id.homeListRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator_fade)
    Drawable separator;

    private static final int FEW_ITEMS_THRESHOLD = 20;

    private Unbinder unbinder;
    private EventAdapter adapter;
    private ItemsRequestListener itemsRequestListener;
    private final Set<Integer> itemsCheckSet = new HashSet<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new EventAdapter(getContext());
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    return;
                }
                LinearLayoutManager manager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                if (manager == null) {
                    return;
                }
                int lastVisibleItem = manager.findLastVisibleItemPosition();
                int totalItems = adapter.getItemCount();
                if (totalItems > 0 &&
                        totalItems - lastVisibleItem <= FEW_ITEMS_THRESHOLD &&
                        !itemsCheckSet.contains(totalItems)) {
                    itemsCheckSet.add(totalItems);
                    itemsRequestListener.onNewItemsRequest();
                }
            }
        });

        if (adapter.getItemCount() == 0) {
            noResultsTextView.setVisibility(View.VISIBLE);
        } else {
            noResultsTextView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(EventAdapter source, EventDTO event) {
        EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
        eventDetailsFragment.setEvent(event);
        ((MainActivity) requireActivity()).setFragment(eventDetailsFragment);
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    public void setEvents(List<EventDTO> events) {
        if (events.isEmpty()) {
            noResultsTextView.setVisibility(View.VISIBLE);
        } else {
            noResultsTextView.setVisibility(View.GONE);
        }
        itemsCheckSet.clear();
        adapter.setEvents(events);
    }

    public void addEvents(List<EventDTO> events) {
        adapter.addEvents(events);
    }

    public void setOnItemsRequestListener(ItemsRequestListener itemsRequestListener) {
        this.itemsRequestListener = itemsRequestListener;
    }

    public interface ItemsRequestListener {
        void onNewItemsRequest();
    }

}
