package com.as.eventalertandroid.ui.common.filter;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.net.model.SeverityDTO;
import com.as.eventalertandroid.net.model.StatusDTO;
import com.as.eventalertandroid.net.model.TypeDTO;
import com.as.eventalertandroid.ui.common.filter.severity.SeveritiesSelectorFragment;
import com.as.eventalertandroid.ui.common.filter.status.StatusesSelectorFragment;
import com.as.eventalertandroid.ui.common.filter.type.TypesSelectorFragment;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.google.android.flexbox.FlexboxLayout;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FilterFragment extends Fragment implements
        TypesSelectorFragment.ValidationListener,
        SeveritiesSelectorFragment.ValidationListener,
        StatusesSelectorFragment.ValidationListener {

    @BindView(R.id.filterRadiusEditText)
    EditText radiusEditText;
    @BindView(R.id.filterStartDateEditText)
    EditText startDateEditText;
    @BindView(R.id.filterEndDateEditText)
    EditText endDateEditText;
    @BindView(R.id.filterTypesFlexbox)
    FlexboxLayout typesFlexbox;
    @BindView(R.id.filterSeveritiesFlexbox)
    FlexboxLayout severitiesFlexbox;
    @BindView(R.id.filterStatusesFlexbox)
    FlexboxLayout statusesFlexbox;

    private Unbinder unbinder;
    private DatePickerDialog startDatePicker;
    private DatePickerDialog endDatePicker;
    private Integer radius;
    private Set<TypeDTO> selectedTypes;
    private Set<SeverityDTO> selectedSeverities;
    private Set<StatusDTO> selectedStatuses;
    private LocalDate startDate;
    private LocalDate endDate;
    private ValidationListener validationListener;
    private final Session session = Session.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        unbinder = ButterKnife.bind(this, view);

        radiusEditText.setText(String.valueOf(radius));
        radiusEditText.setSelection(radiusEditText.getText().length());
        startDateEditText.setText(startDate.format(Constants.defaultDateTimeFormatter));
        endDateEditText.setText(endDate.format(Constants.defaultDateTimeFormatter));

        startDatePicker = new DatePickerDialog(requireContext(),
                (startDateView, year, month, dayOfMonth) -> {
                    startDate = LocalDate.of(year, (month + 1), dayOfMonth);
                    startDateEditText.setText(startDate.format(Constants.defaultDateTimeFormatter));
                }, startDate.getYear(), startDate.getMonthValue() - 1, startDate.getDayOfMonth());

        endDatePicker = new DatePickerDialog(requireContext(),
                (startDateView, year, month, dayOfMonth) -> {
                    endDate = LocalDate.of(year, (month + 1), dayOfMonth);
                    endDateEditText.setText(endDate.format(Constants.defaultDateTimeFormatter));
                }, endDate.getYear(), endDate.getMonthValue() - 1, endDate.getDayOfMonth());

        if (selectedTypes.isEmpty()) {
            selectedTypes = new HashSet<>(session.getTypes());
        }
        if (selectedSeverities.isEmpty()) {
            selectedSeverities = new HashSet<>(session.getSeverities());
        }

        int typesSize = session.getTypes().size();
        if (selectedTypes.size() == typesSize) {
            addType(String.format(getString(R.string.all_types), typesSize));
        } else {
            selectedTypes.forEach(type -> addType(type.label));
        }

        int severitiesSize = session.getSeverities().size();
        if (selectedSeverities.size() == severitiesSize) {
            addSeverity(String.format(getString(R.string.all_severities), severitiesSize));
        } else {
            selectedSeverities.forEach(severity -> addSeverity(severity.label));
        }

        int statusesSize = session.getStatuses().size();
        if (selectedStatuses.size() == statusesSize) {
            addStatus(String.format(getString(R.string.all_statuses), statusesSize));
        } else {
            selectedStatuses.forEach(status -> addStatus(status.label));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onValidateClicked(TypesSelectorFragment source, Set<TypeDTO> selectedTypes) {
        this.selectedTypes = selectedTypes;
    }

    @Override
    public void onValidateClicked(SeveritiesSelectorFragment source, Set<SeverityDTO> selectedSeverities) {
        this.selectedSeverities = selectedSeverities;
    }

    @Override
    public void onValidateClicked(StatusesSelectorFragment source, Set<StatusDTO> selectedStatuses) {
        this.selectedStatuses = selectedStatuses;
    }

    public void setFilterOptions(FilterOptions filterOptions) {
        radius = filterOptions.getRadius();

        selectedTypes = Session.getInstance().getTypes().stream()
                .filter(type -> filterOptions.getTypeIds().contains(type.id))
                .collect(Collectors.toSet());

        selectedSeverities = Session.getInstance().getSeverities().stream()
                .filter(severity -> filterOptions.getSeverityIds().contains(severity.id))
                .collect(Collectors.toSet());

        selectedStatuses = Session.getInstance().getStatuses().stream()
                .filter(status -> filterOptions.getStatusIds().contains(status.id))
                .collect(Collectors.toSet());

        startDate = LocalDate.of(
                filterOptions.getStartDate().getYear(),
                filterOptions.getStartDate().getMonth(),
                filterOptions.getStartDate().getDayOfMonth()
        );

        endDate = LocalDate.of(
                filterOptions.getEndDate().getYear(),
                filterOptions.getEndDate().getMonth(),
                filterOptions.getEndDate().getDayOfMonth()
        );
    }

    public void setOnValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.filterStartDateEditText)
    void onStartDateClicked() {
        startDatePicker.show();
    }

    @OnClick(R.id.filterEndDateEditText)
    void onEndDateClicked() {
        endDatePicker.show();
    }

    @OnClick(R.id.filterTypesLinearLayout)
    void onTypesClicked() {
        TypesSelectorFragment typesSelectorFragment = new TypesSelectorFragment();
        typesSelectorFragment.setOnValidationListener(this);
        typesSelectorFragment.setData(session.getTypes(), new HashSet<>(selectedTypes));
        ((MainActivity) requireActivity()).setFragment(typesSelectorFragment);
    }

    @OnClick(R.id.filterSeveritiesLinearLayout)
    void onSeveritiesClicked() {
        SeveritiesSelectorFragment severitiesSelectorFragment = new SeveritiesSelectorFragment();
        severitiesSelectorFragment.setOnValidationListener(this);
        severitiesSelectorFragment.setData(session.getSeverities(), new HashSet<>(selectedSeverities));
        ((MainActivity) requireActivity()).setFragment(severitiesSelectorFragment);
    }

    @OnClick(R.id.filterStatusesLinearLayout)
    void onStatusesClicked() {
        StatusesSelectorFragment statusesSelectorFragment = new StatusesSelectorFragment();
        statusesSelectorFragment.setOnValidationListener(this);
        statusesSelectorFragment.setData(session.getStatuses(), new HashSet<>(selectedStatuses));
        ((MainActivity) requireActivity()).setFragment(statusesSelectorFragment);
    }

    @OnClick(R.id.filterValidateButton)
    void onValidateClicked() {
        String radius = radiusEditText.getText().toString();
        if (radius.isEmpty()) {
            Toast.makeText(requireContext(), R.string.message_radius_required, Toast.LENGTH_SHORT).show();
            return;
        }
        int radiusValue = Integer.parseInt(radius);
        if (radiusValue < Constants.MIN_RADIUS) {
            String message = String.format(getString(R.string.message_min_radius), Constants.MIN_RADIUS);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        if (radiusValue > Constants.MAX_RADIUS) {
            String message = String.format(getString(R.string.message_max_radius), Constants.MAX_RADIUS);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate.isAfter(endDate)) {
            Toast.makeText(requireContext(), R.string.message_start_date_after_end_date, Toast.LENGTH_SHORT).show();
            return;
        }

        if (endDate.getYear() - startDate.getYear() > Constants.MAX_YEARS_INTERVAL) {
            String message = String.format(getString(R.string.message_dates_years_interval), Constants.MAX_YEARS_INTERVAL);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        FilterOptions filterOptions = new FilterOptions(
                Integer.parseInt(radiusEditText.getText().toString()),
                startDate,
                endDate,
                selectedTypes.stream().map(type -> type.id).collect(Collectors.toSet()),
                selectedSeverities.stream().map(severity -> severity.id).collect(Collectors.toSet()),
                selectedStatuses.stream().map(status -> status.id).collect(Collectors.toSet())
        );

        requireActivity().onBackPressed();
        validationListener.onValidateClicked(this, filterOptions);
    }

    private void addType(String text) {
        CardView typeView = (CardView) getLayoutInflater().inflate(R.layout.item_flexbox, typesFlexbox, false);
        TextView textView = typeView.findViewById(R.id.itemFlexboxTextView);
        textView.setText(text);
        typesFlexbox.addView(typeView);
    }

    private void addSeverity(String text) {
        CardView severityView = (CardView) getLayoutInflater().inflate(R.layout.item_flexbox, severitiesFlexbox, false);
        TextView textView = severityView.findViewById(R.id.itemFlexboxTextView);
        textView.setText(text);
        severitiesFlexbox.addView(severityView);
    }

    private void addStatus(String text) {
        CardView statusView = (CardView) getLayoutInflater().inflate(R.layout.item_flexbox, statusesFlexbox, false);
        TextView textView = statusView.findViewById(R.id.itemFlexboxTextView);
        textView.setText(text);
        statusesFlexbox.addView(statusView);
    }

    public interface ValidationListener {
        void onValidateClicked(FilterFragment source, FilterOptions filterOptions);
    }

}
