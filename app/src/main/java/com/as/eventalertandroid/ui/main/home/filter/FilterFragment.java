package com.as.eventalertandroid.ui.main.home.filter;

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
import com.as.eventalertandroid.net.model.EventSeverity;
import com.as.eventalertandroid.net.model.EventTag;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.as.eventalertandroid.ui.main.home.filter.severity.SeveritiesSelectorFragment;
import com.as.eventalertandroid.ui.main.home.filter.tag.TagsSelectorFragment;
import com.google.android.flexbox.FlexboxLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FilterFragment extends Fragment implements
        TagsSelectorFragment.ValidationListener,
        SeveritiesSelectorFragment.ValidationListener {

    @BindView(R.id.filterRadiusEditText)
    EditText radiusEditText;
    @BindView(R.id.filterStartDateEditText)
    EditText startDateEditText;
    @BindView(R.id.filterEndDateEditText)
    EditText endDateEditText;
    @BindView(R.id.filterTagsFlexbox)
    FlexboxLayout tagsFlexbox;
    @BindView(R.id.filterSeveritiesFlexbox)
    FlexboxLayout severitiesFlexbox;

    private Unbinder unbinder;
    private DatePickerDialog startDatePicker;
    private DatePickerDialog endDatePicker;
    private FilterOptions filterOptions;
    private Set<EventTag> selectedTags;
    private Set<EventSeverity> selectedSeverities;
    private LocalDate startDate;
    private LocalDate endDate;
    private ValidationListener validationListener;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
    private final Session session = Session.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        unbinder = ButterKnife.bind(this, view);

        radiusEditText.setText(String.valueOf(filterOptions.getRadius()));
        radiusEditText.setSelection(radiusEditText.getText().length());
        startDateEditText.setText(startDate.format(dateFormatter));
        endDateEditText.setText(endDate.format(dateFormatter));

        startDatePicker = new DatePickerDialog(requireContext(),
                (startDateView, year, month, dayOfMonth) -> {
                    startDate = LocalDate.of(year, (month + 1), dayOfMonth);
                    startDateEditText.setText(startDate.format(dateFormatter));
                }, startDate.getYear(), startDate.getMonthValue() - 1, startDate.getDayOfMonth());

        endDatePicker = new DatePickerDialog(requireContext(),
                (startDateView, year, month, dayOfMonth) -> {
                    endDate = LocalDate.of(year, (month + 1), dayOfMonth);
                    endDateEditText.setText(endDate.format(dateFormatter));
                }, endDate.getYear(), endDate.getMonthValue() - 1, endDate.getDayOfMonth());

        if (selectedTags.isEmpty()) {
            selectedTags = new HashSet<>(session.getTags());
        }
        if (selectedSeverities.isEmpty()) {
            selectedSeverities = new HashSet<>(session.getSeverities());
        }

        int tagsSize = session.getTags().size();
        if (selectedTags.size() == tagsSize) {
            addTag(String.format(getString(R.string.all_tags), tagsSize));
        } else {
            selectedTags.forEach(tag -> addTag(tag.name));
        }
        int severitiesSize = session.getSeverities().size();
        if (selectedSeverities.size() == severitiesSize) {
            addSeverity(String.format(getString(R.string.all_severities), severitiesSize));
        } else {
            selectedSeverities.forEach(tag -> addSeverity(tag.name));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onValidateClicked(TagsSelectorFragment source, Set<EventTag> selectedTags) {
        this.selectedTags = selectedTags;
    }

    @Override
    public void onValidateClicked(SeveritiesSelectorFragment source, Set<EventSeverity> selectedSeverities) {
        this.selectedSeverities = selectedSeverities;
    }

    public void setFilterOptions(FilterOptions filterOptions) {
        this.filterOptions = filterOptions;

        selectedTags = new HashSet<>(filterOptions.getTags());
        selectedSeverities = new HashSet<>(filterOptions.getSeverities());
        startDate = LocalDate.of(
                filterOptions.getStartDate().getYear(),
                filterOptions.getStartDate().getMonth(),
                filterOptions.getStartDate().getDayOfMonth());
        endDate = LocalDate.of(
                filterOptions.getEndDate().getYear(),
                filterOptions.getEndDate().getMonth(),
                filterOptions.getEndDate().getDayOfMonth());
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

    @OnClick(R.id.filterTagsLinearLayout)
    void onTagsClicked() {
        TagsSelectorFragment tagsSelectorFragment = new TagsSelectorFragment();
        tagsSelectorFragment.setOnValidationListener(this);
        tagsSelectorFragment.setData(session.getTags(), new HashSet<>(selectedTags));
        ((MainActivity) requireActivity()).setFragment(tagsSelectorFragment);
    }

    @OnClick(R.id.filterSeveritiesLinearLayout)
    void onSeveritiesClicked() {
        SeveritiesSelectorFragment severitiesSelectorFragment = new SeveritiesSelectorFragment();
        severitiesSelectorFragment.setOnValidationListener(this);
        severitiesSelectorFragment.setData(session.getSeverities(), new HashSet<>(selectedSeverities));
        ((MainActivity) requireActivity()).setFragment(severitiesSelectorFragment);
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

        filterOptions.setRadius(Integer.parseInt(radiusEditText.getText().toString()));
        filterOptions.setStartDate(startDate);
        filterOptions.setEndDate(endDate);
        filterOptions.setTags(selectedTags);
        filterOptions.setSeverities(selectedSeverities);

        requireActivity().onBackPressed();
        validationListener.onValidateClicked(this, filterOptions);
    }

    private void addTag(String text) {
        CardView tagView = (CardView) getLayoutInflater().inflate(R.layout.item_flexbox, tagsFlexbox, false);
        TextView textView = tagView.findViewById(R.id.itemFlexboxTextView);
        textView.setText(text);
        tagsFlexbox.addView(tagView);
    }

    private void addSeverity(String text) {
        CardView severityView = (CardView) getLayoutInflater().inflate(R.layout.item_flexbox, severitiesFlexbox, false);
        TextView textView = severityView.findViewById(R.id.itemFlexboxTextView);
        textView.setText(text);
        severitiesFlexbox.addView(severityView);
    }

    public interface ValidationListener {
        void onValidateClicked(FilterFragment source, FilterOptions filterOptions);
    }

}
