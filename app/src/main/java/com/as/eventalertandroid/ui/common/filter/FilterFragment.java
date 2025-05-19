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
import com.as.eventalertandroid.validator.TextValidator;
import com.as.eventalertandroid.validator.Validator;
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

    private final Validator typesValidator = () -> {
        String messageMinTypeRequired = getString(R.string.message_min_type_required);

        if (selectedTypes == null || selectedTypes.isEmpty()) {
            typesLayout.setError(messageMinTypeRequired);
            return false;
        }
        if (typesLayout.getError() != null && typesLayout.getError().equals(messageMinTypeRequired)) {
            typesLayout.setError(null);
            typesLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator severitiesValidator = () -> {
        String messageMinSeverityRequired = getString(R.string.message_min_severity_required);

        if (selectedSeverities == null || selectedSeverities.isEmpty()) {
            severitiesLayout.setError(messageMinSeverityRequired);
            return false;
        }
        if (severitiesLayout.getError() != null && severitiesLayout.getError().equals(messageMinSeverityRequired)) {
            severitiesLayout.setError(null);
            severitiesLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator statusesValidator = () -> {
        String messageMinStatusRequired = getString(R.string.message_min_status_required);

        if (selectedStatuses == null || selectedStatuses.isEmpty()) {
            statusesLayout.setError(messageMinStatusRequired);
            return false;
        }
        if (statusesLayout.getError() != null && statusesLayout.getError().equals(messageMinStatusRequired)) {
            statusesLayout.setError(null);
            statusesLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator radiusValidator = () -> {
        String radiusStr = radiusEditText.getEditableText().toString();

        String messageRadiusRequired = getString(R.string.message_radius_required);
        String messageMinRadius = String.format(getString(R.string.message_min_radius), Constants.MIN_RADIUS);
        String messageMaxRadius = String.format(getString(R.string.message_max_radius), Constants.MAX_RADIUS);

        if (radiusStr.isEmpty()) {
            radiusLayout.setError(messageRadiusRequired);
            return false;
        }
        int radiusValue = Integer.parseInt(radiusStr);
        if (radiusValue < Constants.MIN_RADIUS) {
            radiusLayout.setError(messageMinRadius);
            return false;
        }
        if (radiusValue > Constants.MAX_RADIUS) {
            radiusLayout.setError(messageMaxRadius);
            return false;
        }
        if (radiusLayout.getError() != null && (radiusLayout.getError().equals(messageRadiusRequired) || radiusLayout.getError().equals(messageMinRadius) || radiusLayout.getError().equals(messageMaxRadius))) {
            radiusLayout.setError(null);
            radiusLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator startDateValidator = () -> {
        String messageStartDateRequired = getString(R.string.message_start_date_required);

        if (startDate == null) {
            startDateLayout.setError(messageStartDateRequired);
            return false;
        }
        if (startDateLayout.getError() != null && startDateLayout.getError().equals(messageStartDateRequired)) {
            startDateLayout.setError(null);
            startDateLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator endDateValidator = () -> {
        String messageEndDateRequired = getString(R.string.message_end_date_required);

        if (endDate == null) {
            endDateLayout.setError(messageEndDateRequired);
            return false;
        }
        if (endDateLayout.getError() != null && endDateLayout.getError().equals(messageEndDateRequired)) {
            endDateLayout.setError(null);
            endDateLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator startDateAndEndDateValidator = () -> {
        String messageStartDateAfterEndDate = getString(R.string.message_start_date_after_end_date);
        String messageDatesYearsInterval = String.format(getString(R.string.message_dates_years_interval), Constants.MAX_YEARS_INTERVAL);

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            endDateLayout.setError(messageStartDateAfterEndDate);
            return false;
        }
        if (startDate != null && endDate != null && endDate.getYear() - startDate.getYear() > Constants.MAX_YEARS_INTERVAL) {
            endDateLayout.setError(messageDatesYearsInterval);
            return false;
        }
        if (endDateLayout.getError() != null && (endDateLayout.getError().equals(messageStartDateAfterEndDate) || endDateLayout.getError().equals(messageDatesYearsInterval))) {
            endDateLayout.setError(null);
            endDateLayout.setErrorEnabled(false);
        }

        return true;
    };

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

        radiusEditText.addTextChangedListener(TextValidator.of(radiusValidator));
        startDateEditText.addTextChangedListener(TextValidator.of(() -> startDateValidator.validate() & startDateAndEndDateValidator.validate()));
        endDateEditText.addTextChangedListener(TextValidator.of(() -> endDateValidator.validate() & startDateAndEndDateValidator.validate()));

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
            typesValidator.validate();
        }

        if (selectedSeverities != null && !selectedSeverities.isEmpty()) {
            severitiesEditText.setText(String.format(getString(R.string.filter_selected_severities), selectedSeverities.size()));
        } else {
            severitiesEditText.setText(null);
        }
        if (selectorFragment != null && selectorFragment instanceof SeveritiesSelectorFragment) {
            severitiesValidator.validate();
        }

        if (selectedStatuses != null && !selectedStatuses.isEmpty()) {
            statusesEditText.setText(String.format(getString(R.string.filter_selected_statuses), selectedStatuses.size()));
        } else {
            statusesEditText.setText(null);
        }
        if (selectorFragment != null && selectorFragment instanceof StatusesSelectorFragment) {
            statusesValidator.validate();
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
        return typesValidator.validate() &
                severitiesValidator.validate() &
                statusesValidator.validate() &
                radiusValidator.validate() &
                startDateValidator.validate() &
                endDateValidator.validate() &
                startDateAndEndDateValidator.validate();
    }

    public interface ValidationListener {
        void onValidateClicked(FilterFragment source, FilterOptions filterOptions);
    }

}
