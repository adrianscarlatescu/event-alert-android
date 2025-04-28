package com.as.eventalertandroid.ui.common.filter;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.defaults.TextChangedWatcher;
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
import java.util.List;
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
    private List<TypeDTO> selectedTypes;
    private List<SeverityDTO> selectedSeverities;
    private List<StatusDTO> selectedStatuses;
    private LocalDate startDate;
    private LocalDate endDate;
    private ValidationListener validationListener;
    private Fragment selectorFragment;
    private final Session session = Session.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        unbinder = ButterKnife.bind(this, view);

        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = now.minusYears(1);

        if (radius != null) {
            String radiusStr = String.valueOf(radius);
            radiusEditText.setText(radiusStr);
            radiusEditText.setSelection(radiusStr.length());
        }

        if (startDate != null) {
            startDateEditText.setText(startDate.format(Constants.defaultDateFormatter));
        }
        startDatePicker = new DatePickerDialog(requireContext(),
                (startDateView, year, month, dayOfMonth) -> {
                    startDate = LocalDate.of(year, (month + 1), dayOfMonth);
                    if (startDate != null) {
                        startDateEditText.setText(startDate.format(Constants.defaultDateFormatter));
                    }
                }, startDate != null ? startDate.getYear() : oneYearAgo.getYear(),
                startDate != null ? startDate.getMonthValue() - 1 : oneYearAgo.getMonthValue() - 1,
                startDate != null ? startDate.getDayOfMonth() : oneYearAgo.getDayOfMonth());

        if (endDate != null) {
            endDateEditText.setText(endDate.format(Constants.defaultDateFormatter));
        }
        endDatePicker = new DatePickerDialog(requireContext(),
                (startDateView, year, month, dayOfMonth) -> {
                    endDate = LocalDate.of(year, (month + 1), dayOfMonth);
                    if (endDate != null) {
                        endDateEditText.setText(endDate.format(Constants.defaultDateFormatter));
                    }
                }, endDate != null ? endDate.getYear() : now.getYear(),
                endDate != null ? endDate.getMonthValue() - 1 : now.getMonthValue() - 1,
                endDate != null ? endDate.getDayOfMonth() : now.getDayOfMonth());

        radiusEditText.addTextChangedListener(new TextChangedWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateRadius();
            }
        });

        startDateEditText.addTextChangedListener(new TextChangedWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateStartDate();
            }
        });
        endDateEditText.addTextChangedListener(new TextChangedWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEndDate();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (selectedTypes != null && !selectedTypes.isEmpty()) {
            typesEditText.setText(String.format(getString(R.string.filter_selected_types), selectedTypes.size()));
        } else {
            typesEditText.setText(null);
        }
        if (selectorFragment != null && selectorFragment instanceof TypesSelectorFragment) {
            validateTypes();
        }

        if (selectedSeverities != null && !selectedSeverities.isEmpty()) {
            severitiesEditText.setText(String.format(getString(R.string.filter_selected_severities), selectedSeverities.size()));
        } else {
            severitiesEditText.setText(null);
        }
        if (selectorFragment != null && selectorFragment instanceof SeveritiesSelectorFragment) {
            validateSeverities();
        }

        if (selectedStatuses != null && !selectedStatuses.isEmpty()) {
            statusesEditText.setText(String.format(getString(R.string.filter_selected_statuses), selectedStatuses.size()));
        } else {
            statusesEditText.setText(null);
        }
        if (selectorFragment != null && selectorFragment instanceof StatusesSelectorFragment) {
            validateStatuses();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onValidateClicked(TypesSelectorFragment source, List<TypeDTO> selectedTypes) {
        this.selectedTypes = selectedTypes;
        this.selectorFragment = source;
    }

    @Override
    public void onValidateClicked(SeveritiesSelectorFragment source, List<SeverityDTO> selectedSeverities) {
        this.selectedSeverities = selectedSeverities;
        this.selectorFragment = source;
    }

    @Override
    public void onValidateClicked(StatusesSelectorFragment source, List<StatusDTO> selectedStatuses) {
        this.selectedStatuses = selectedStatuses;
        this.selectorFragment = source;
    }

    public void setFilterOptions(FilterOptions filterOptions) {
        radius = filterOptions.getRadius();

        selectedTypes = Session.getInstance().getTypes().stream()
                .filter(type -> filterOptions.getTypeIds().contains(type.id))
                .collect(Collectors.toList());

        selectedSeverities = Session.getInstance().getSeverities().stream()
                .filter(severity -> filterOptions.getSeverityIds().contains(severity.id))
                .collect(Collectors.toList());

        selectedStatuses = Session.getInstance().getStatuses().stream()
                .filter(status -> filterOptions.getStatusIds().contains(status.id))
                .collect(Collectors.toList());

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
        typesSelectorFragment.setData(session.getTypes(), selectedTypes);
        ((MainActivity) requireActivity()).setFragment(typesSelectorFragment);
    }

    @OnClick(R.id.filterSeveritiesTextInputEditText)
    void onSeveritiesClicked() {
        SeveritiesSelectorFragment severitiesSelectorFragment = new SeveritiesSelectorFragment();
        severitiesSelectorFragment.setOnValidationListener(this);
        severitiesSelectorFragment.setData(session.getSeverities(), selectedSeverities);
        ((MainActivity) requireActivity()).setFragment(severitiesSelectorFragment);
    }

    @OnClick(R.id.filterStatusesTextInputEditText)
    void onStatusesClicked() {
        StatusesSelectorFragment statusesSelectorFragment = new StatusesSelectorFragment();
        statusesSelectorFragment.setOnValidationListener(this);
        statusesSelectorFragment.setData(session.getStatuses(), selectedStatuses);
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
        return validateTypes() &
                validateSeverities() &
                validateStatuses() &
                validateRadius() &
                validateStartDate() &
                validateEndDate();
    }

    private boolean validateTypes() {
        if (selectedTypes == null || selectedTypes.isEmpty()) {
            typesLayout.setError(getString(R.string.message_min_type_required));
            return false;
        }

        typesLayout.setError(null);
        typesLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validateSeverities() {
        if (selectedSeverities == null || selectedSeverities.isEmpty()) {
            severitiesLayout.setError(getString(R.string.message_min_severity_required));
            return false;
        }

        severitiesLayout.setError(null);
        severitiesLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validateStatuses() {
        if (selectedStatuses == null ||selectedStatuses.isEmpty()) {
            statusesLayout.setError(getString(R.string.message_min_status_required));
            return false;
        }

        statusesLayout.setError(null);
        statusesLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validateRadius() {
        String radiusStr = radiusEditText.getEditableText().toString();
        if (radiusStr.isEmpty()) {
            radiusLayout.setError(getString(R.string.message_radius_required));
            return false;
        }
        int radiusValue = Integer.parseInt(radiusStr);
        if (radiusValue < Constants.MIN_RADIUS) {
            radiusLayout.setError(String.format(getString(R.string.message_min_radius), Constants.MIN_RADIUS));
            return false;
        }

        if (radiusValue > Constants.MAX_RADIUS) {
            radiusLayout.setError(String.format(getString(R.string.message_max_radius), Constants.MAX_RADIUS));
            return false;
        }

        radiusLayout.setError(null);
        radiusLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validateStartDate() {
        if (startDate == null) {
            startDateLayout.setError(getString(R.string.message_start_date_required));
            return false;
        }

        startDateLayout.setError(null);
        startDateLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validateEndDate() {
        if (endDate == null) {
            endDateLayout.setError(getString(R.string.message_end_date_required));
            return false;
        }
        if (startDate != null && startDate.isAfter(endDate)) {
            endDateLayout.setError(getString(R.string.message_start_date_after_end_date));
            return false;
        }
        if (startDate != null && endDate.getYear() - startDate.getYear() > Constants.MAX_YEARS_INTERVAL) {
            endDateLayout.setError(String.format(getString(R.string.message_dates_years_interval), Constants.MAX_YEARS_INTERVAL));
            return false;
        }

        endDateLayout.setError(null);
        endDateLayout.setErrorEnabled(false);
        return true;
    }

    public interface ValidationListener {
        void onValidateClicked(FilterFragment source, FilterOptions filterOptions);
    }

}
