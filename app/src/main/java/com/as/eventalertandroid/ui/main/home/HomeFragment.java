package com.as.eventalertandroid.ui.main.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.enums.id.OrderId;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.EventFilterDTO;
import com.as.eventalertandroid.net.service.EventService;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.as.eventalertandroid.ui.common.filter.FilterFragment;
import com.as.eventalertandroid.ui.common.filter.FilterOptions;
import com.as.eventalertandroid.ui.common.order.OrderDialog;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.as.eventalertandroid.ui.main.home.list.HomeListFragment;
import com.as.eventalertandroid.ui.main.home.map.HomeMapFragment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends Fragment implements FilterFragment.ValidationListener {

    @BindView(R.id.homeInfoTotalEventsTextView)
    TextView infoTotalEventsTextView;
    @BindView(R.id.homeInfoTotalEventsDisplayedTextView)
    TextView infoTotalEventsDisplayedTextView;
    @BindView(R.id.homeInfoPagesTextView)
    TextView infoPagesTextView;
    @BindView(R.id.homeMapMenuLinearLayout)
    LinearLayout mapMenuLinearLayout;
    @BindView(R.id.homeItemFilterLinearLayout)
    LinearLayout filterLinearLayout;
    @BindView(R.id.homeItemListLinearLayout)
    LinearLayout listLinearLayout;
    @BindView(R.id.homeItemMapLinearLayout)
    LinearLayout mapLinearLayout;
    @BindView(R.id.homeItemSortLinearLayout)
    LinearLayout sortLinearLayout;

    private static final long FADE_DURATION = 150L;

    private Unbinder unbinder;
    private HomeTab homeTab = HomeTab.MAP;
    private HomeMapFragment mapFragment;
    private HomeListFragment listFragment;
    private FilterOptions filterOptions;
    private OrderId orderId = OrderId.BY_DATE_DESCENDING;
    private int mapPage;
    private int listPage;
    private int totalPages;
    private long totalEvents;
    private long totalEventsDisplayed;
    private long lastChangeTime;
    private final EventFilterDTO eventFilter = new EventFilterDTO();
    private final EventService eventService = RetrofitClient.getInstance().create(EventService.class);
    private final Session session = Session.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapFragment = new HomeMapFragment();
        listFragment = new HomeListFragment();
        listFragment.setOnItemsRequestListener(() -> {
            listPage++;
            searchListItems();
        });

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.homeContentFrameLayout, mapFragment);
        ft.add(R.id.homeContentFrameLayout, listFragment);
        ft.commit();

        filterOptions = new FilterOptions(
                1000,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                session.getTypes().stream().map(typeDTO -> typeDTO.id).collect(Collectors.toSet()),
                session.getSeverities().stream().map(severityDTO -> severityDTO.id).collect(Collectors.toSet()),
                session.getStatuses().stream().map(statusDTO -> statusDTO.id).collect(Collectors.toSet())
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        // Initial fragment
        show(homeTab);
        updateInfoViews();

        if (homeTab == HomeTab.LIST) {
            mapMenuLinearLayout.setVisibility(View.GONE);
            infoTotalEventsDisplayedTextView.setVisibility(View.GONE);
            infoPagesTextView.setVisibility(View.GONE);
            listLinearLayout.setVisibility(View.GONE);
            mapLinearLayout.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.homeItemFilterLinearLayout)
    void onItemFilterClicked() {
        if (!session.isUserLocationSet()) {
            Toast.makeText(requireContext(), R.string.message_location_not_set, Toast.LENGTH_SHORT).show();
            return;
        }
        mapFragment.requireView().setVisibility(View.GONE); // Avoid map flick

        FilterFragment filterFragment = new FilterFragment();
        filterFragment.setFilterOptions(filterOptions);
        filterFragment.setOnValidationListener(this);
        ((MainActivity) requireActivity()).setFragment(filterFragment);
    }

    @Override
    public void onValidateClicked(FilterFragment source, FilterOptions filterOptions) {
        this.filterOptions = filterOptions;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::requestNewSearch, 400);
    }

    @OnClick(R.id.homeItemMapLinearLayout)
    void onItemMapClicked() {
        if (isTimeElapsed()) {
            show(HomeTab.MAP);
            fade(mapLinearLayout, listLinearLayout);
            fadeIn(mapMenuLinearLayout);
            fadeIn(infoTotalEventsDisplayedTextView);
            fadeIn(infoPagesTextView);
        }
    }

    @OnClick(R.id.homeItemListLinearLayout)
    void onItemListClicked() {
        if (isTimeElapsed()) {
            show(HomeTab.LIST);
            fade(listLinearLayout, mapLinearLayout);
            fadeOut(mapMenuLinearLayout);
            fadeOut(infoTotalEventsDisplayedTextView);
            fadeOut(infoPagesTextView);
        }
    }

    @OnClick(R.id.homeItemSortLinearLayout)
    void onItemSortClicked() {
        OrderDialog orderDialog = new OrderDialog(requireContext(), orderId) {
            @Override
            public void onItemClicked(OrderId selection) {
                if (orderId == selection) {
                    dismiss();
                    return;
                }
                orderId = selection;
                dismiss();
                if (totalEvents > 1) {
                    requestNewSearch();
                } else {
                    Toast.makeText(requireContext(), R.string.message_order_not_applied, Toast.LENGTH_SHORT).show();
                }
            }
        };
        orderDialog.show();
    }

    @OnClick(R.id.homeMapItemLocationLinearLayout)
    void onMapItemLocationClicked() {
        mapFragment.focusOnUserLocation();
    }

    @OnClick(R.id.homeMapItemPreviousLinearLayout)
    void onMapItemPreviousClicked() {
        if (totalPages <= 1) {
            return;
        }
        if (mapPage == 0) {
            mapPage = totalPages - 1;
        } else {
            mapPage--;
        }
        searchMapItems();
    }

    @OnClick(R.id.homeMapItemNextLinearLayout)
    void onMapItemNextClicked() {
        if (totalPages <= 1) {
            return;
        }
        if (mapPage + 1 == totalPages) {
            mapPage = 0;
        } else {
            mapPage++;
        }
        searchMapItems();
    }

    private void requestNewSearch() {
        eventFilter.radius = filterOptions.getRadius();
        eventFilter.startDate = filterOptions.getStartDate();
        eventFilter.endDate = filterOptions.getEndDate();
        eventFilter.typeIds = filterOptions.getTypeIds();
        eventFilter.severityIds = filterOptions.getSeverityIds();
        eventFilter.statusIds = filterOptions.getStatusIds();
        eventFilter.latitude = session.getUserLatitude();
        eventFilter.longitude = session.getUserLongitude();

        mapPage = 0;
        listPage = 0;

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        eventService.getEventsByFilter(eventFilter, Constants.PAGE_SIZE, 0, orderId)
                .thenAccept(response ->
                        progressDialog.dismiss(() ->
                                requireActivity().runOnUiThread(() -> {
                                    if (response.totalElements == 0) {
                                        Toast.makeText(requireContext(), R.string.message_no_events_found, Toast.LENGTH_SHORT).show();
                                    }

                                    totalEvents = response.totalElements;
                                    totalEventsDisplayed = response.content.size();
                                    totalPages = response.totalPages;
                                    updateInfoViews();

                                    mapFragment.updateView(response.content);

                                    listFragment.setEvents(new ArrayList<>(response.content));
                                    listFragment.scrollToTop();
                                })
                        )
                )
                .exceptionally(throwable -> {
                    progressDialog.dismiss();
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

    private void searchMapItems() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        eventService.getEventsByFilter(eventFilter, Constants.PAGE_SIZE, mapPage, orderId)
                .thenAccept(response ->
                        progressDialog.dismiss(() ->
                                requireActivity().runOnUiThread(() -> {
                                    totalEvents = response.totalElements;
                                    totalEventsDisplayed = response.content.size();
                                    totalPages = response.totalPages;
                                    updateInfoViews();

                                    mapFragment.updateView(response.content);
                                })))
                .exceptionally(throwable -> {
                    progressDialog.dismiss();
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

    private void searchListItems() {
        eventService.getEventsByFilter(eventFilter, Constants.PAGE_SIZE, listPage, orderId)
                .thenAccept(response ->
                        requireActivity().runOnUiThread(() -> listFragment.addEvents(response.content)))
                .exceptionally(throwable -> {
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

    private void updateInfoViews() {
        infoTotalEventsTextView.setText(String.format(getString(R.string.info_item_total_events), totalEvents));
        infoTotalEventsDisplayedTextView.setText(String.format(getString(R.string.info_item_total_events_displayed), totalEventsDisplayed));
        infoPagesTextView.setText(String.format(getString(R.string.info_item_pages), totalPages == 0 ? 0 : mapPage + 1, totalPages));
    }

    private boolean isTimeElapsed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastChangeTime > 1000) {
            lastChangeTime = currentTime;
            return true;
        }
        return false;
    }

    private void fade(View fromView, View toView) {
        fromView.animate().alpha(0).setDuration(FADE_DURATION)
                .withEndAction(() ->
                        toView.animate().alpha(1).setDuration(FADE_DURATION)
                                .withStartAction(() -> {
                                    fromView.setVisibility(View.GONE);
                                    toView.setAlpha(0);
                                    toView.setVisibility(View.VISIBLE);
                                })
                );
    }

    private void fadeOut(View view) {
        view.animate().alpha(0).setDuration(FADE_DURATION)
                .withEndAction(() -> view.setVisibility(View.GONE));
    }

    private void fadeIn(View view) {
        view.animate().alpha(1).setDuration(FADE_DURATION)
                .withStartAction(() -> {
                    view.setAlpha(0);
                    view.setVisibility(View.VISIBLE);
                });
    }

    private void show(HomeTab homeTab) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.hide(homeTab == HomeTab.MAP ? listFragment : mapFragment);
        ft.show(homeTab == HomeTab.MAP ? mapFragment : listFragment);
        ft.commit();
        this.homeTab = homeTab;
    }

    private enum HomeTab {
        MAP, LIST
    }

}
