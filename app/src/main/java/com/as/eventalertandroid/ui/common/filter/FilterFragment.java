package com.as.eventalertandroid.ui.common.filter;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FilterFragment extends Fragment implements
        TypesSelectorFragment.ValidationListener,
        SeveritiesSelectorFragment.ValidationListener,
        StatusesSelectorFragment.ValidationListener {

    @BindView(R.id.filterRadiusTextInputLayout)
    TextInputLayout radiusLayout;
    @BindView(R.id.filterRadiusTextInputEditText)
    TextInputEditText radiusEditText;

    @BindView(R.id.filterStartDateTextInputLayout)
    TextInputLayout startDateLayout;
    @BindView(R.id.filterStartDateTextInputEditText)
    TextInputEditText startDateEditText;

    @BindView(R.id.filterEndDateTextInputLayout)
    TextInputLayout endDateLayout;
    @BindView(R.id.filterEndDateTextInputEditText)
    TextInputEditText endDateEditText;

    @BindView(R.id.filterTypesTextInputLayout)
    TextInputLayout typesLayout;
    @BindView(R.id.filterTypesTextInputEditText)
    TextInputEditText typesEditText;

    @BindView(R.id.filterSeveritiesTextInputLayout)
    TextInputLayout severitiesLayout;
    @BindView(R.id.filterSeveritiesTextInputEditText)
    TextInputEditText severitiesEditText;

    @BindView(R.id.filterStatusesTextInputLayout)
    TextInputLayout statusesLayout;
    @BindView(R.id.filterStatusesTextInputEditText)
    TextInputEditText statusesEditText;

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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String radiusStr = String.valueOf(radius);
        radiusEditText.setText(radiusStr);
        radiusEditText.setSelection(radiusStr.length());

        startDateEditText.setText(startDate.format(Constants.defaultDateFormatter));
        endDateEditText.setText(endDate.format(Constants.defaultDateFormatter));

        startDatePicker = new DatePickerDialog(requireContext(),
                (startDateView, year, month, dayOfMonth) -> {
                    startDate = LocalDate.of(year, (month + 1), dayOfMonth);
                    startDateEditText.setText(startDate.format(Constants.defaultDateFormatter));
                }, startDate.getYear(), startDate.getMonthValue() - 1, startDate.getDayOfMonth());

        endDatePicker = new DatePickerDialog(requireContext(),
                (startDateView, year, month, dayOfMonth) -> {
                    endDate = LocalDate.of(year, (month + 1), dayOfMonth);
                    endDateEditText.setText(endDate.format(Constants.defaultDateFormatter));
                }, endDate.getYear(), endDate.getMonthValue() - 1, endDate.getDayOfMonth());

        if (selectedTypes.isEmpty()) {
            selectedTypes = new HashSet<>(session.getTypes());
        }
        if (selectedSeverities.isEmpty()) {
            selectedSeverities = new HashSet<>(session.getSeverities());
        }
        if (selectedStatuses.isEmpty()) {
            selectedStatuses = new HashSet<>(session.getStatuses());
        }

        typesEditText.setText(String.format(getString(R.string.filter_selected_types), selectedTypes.size()));
        severitiesEditText.setText(String.format(getString(R.string.filter_selected_severities), selectedSeverities.size()));
        statusesEditText.setText(String.format(getString(R.string.filter_selected_statuses), selectedStatuses.size()));
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

    @OnClick(R.id.filterStartDateTextInputEditText)
    void onStartDateClicked() {
        startDatePicker.show();
    }

    @OnClick(R.id.filterEndDateTextInputEditText)
    void onEndDateClicked() {
        endDatePicker.show();
    }

    @OnClick(R.id.filterTypesTextInputEditText)
    void onTypesClicked() {
        TypesSelectorFragment typesSelectorFragment = new TypesSelectorFragment();
        typesSelectorFragment.setOnValidationListener(this);
        typesSelectorFragment.setData(session.getTypes(), new HashSet<>(selectedTypes));
        ((MainActivity) requireActivity()).setFragment(typesSelectorFragment);
    }

    @OnClick(R.id.filterSeveritiesTextInputEditText)
    void onSeveritiesClicked() {
        SeveritiesSelectorFragment severitiesSelectorFragment = new SeveritiesSelectorFragment();
        severitiesSelectorFragment.setOnValidationListener(this);
        severitiesSelectorFragment.setData(session.getSeverities(), new HashSet<>(selectedSeverities));
        ((MainActivity) requireActivity()).setFragment(severitiesSelectorFragment);
    }

    @OnClick(R.id.filterStatusesTextInputEditText)
    void onStatusesClicked() {
        StatusesSelectorFragment statusesSelectorFragment = new StatusesSelectorFragment();
        statusesSelectorFragment.setOnValidationListener(this);
        statusesSelectorFragment.setData(session.getStatuses(), new HashSet<>(selectedStatuses));
        ((MainActivity) requireActivity()).setFragment(statusesSelectorFragment);
    }

    @OnClick(R.id.filterValidateButton)
    void onValidateClicked() {
        if (!validateForm()) {
            return;
        }

        FilterOptions filterOptions = new FilterOptions(
                Integer.parseInt(radiusEditText.getEditableText().toString()),
                startDate,
                endDate,
                selectedTypes.stream().map(type -> type.id).collect(Collectors.toSet()),
                selectedSeverities.stream().map(severity -> severity.id).collect(Collectors.toSet()),
                selectedStatuses.stream().map(status -> status.id).collect(Collectors.toSet())
        );

        requireActivity().onBackPressed();
        validationListener.onValidateClicked(this, filterOptions);
    }

    private boolean validateForm() {
        boolean isRadiusValid = true;
        boolean isEndDateValid = true;

        String radius = radiusEditText.getEditableText().toString();
        if (radius.isEmpty()) {
            radiusLayout.setError(getString(R.string.message_radius_required));
            isRadiusValid = false;
        } else {
            int radiusValue = Integer.parseInt(radius);
            if (radiusValue < Constants.MIN_RADIUS) {
                radiusLayout.setError(String.format(getString(R.string.message_min_radius), Constants.MIN_RADIUS));
                isRadiusValid = false;
            }

            if (radiusValue > Constants.MAX_RADIUS) {
                radiusLayout.setError(String.format(getString(R.string.message_max_radius), Constants.MAX_RADIUS));
                isRadiusValid = false;
            }
        }

        if (startDate.isAfter(endDate)) {
            endDateLayout.setError(getString(R.string.message_start_date_after_end_date));
            isEndDateValid = false;
        }

        if (endDate.getYear() - startDate.getYear() > Constants.MAX_YEARS_INTERVAL) {
            endDateLayout.setError(String.format(getString(R.string.message_dates_years_interval), Constants.MAX_YEARS_INTERVAL));
            isEndDateValid = false;
        }

        if (isRadiusValid) {
            radiusLayout.setError(null);
            radiusLayout.setErrorEnabled(false);
        }
        if (isEndDateValid) {
            endDateLayout.setError(null);
            endDateLayout.setErrorEnabled(false);
        }

        return isRadiusValid &&
                isEndDateValid;
    }

    public interface ValidationListener {
        void onValidateClicked(FilterFragment source, FilterOptions filterOptions);
    }

}
