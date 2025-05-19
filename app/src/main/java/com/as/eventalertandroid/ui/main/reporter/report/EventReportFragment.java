package com.as.eventalertandroid.ui.main.reporter.report;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.enums.ImageType;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.EventCreateDTO;
import com.as.eventalertandroid.net.model.EventDTO;
import com.as.eventalertandroid.net.model.SeverityDTO;
import com.as.eventalertandroid.net.model.StatusDTO;
import com.as.eventalertandroid.net.model.TypeDTO;
import com.as.eventalertandroid.net.model.UserDTO;
import com.as.eventalertandroid.net.service.EventService;
import com.as.eventalertandroid.net.service.FileService;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.as.eventalertandroid.ui.main.reporter.report.severity.SeveritySelectorFragment;
import com.as.eventalertandroid.ui.main.reporter.report.status.StatusSelectorFragment;
import com.as.eventalertandroid.ui.main.reporter.report.type.TypeSelectorFragment;
import com.as.eventalertandroid.validator.TextValidator;
import com.as.eventalertandroid.validator.Validator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MultipartBody;

public class EventReportFragment extends Fragment implements
        TypeSelectorFragment.ValidationListener,
        SeveritySelectorFragment.ValidationListener,
        StatusSelectorFragment.ValidationListener {

    @BindView(R.id.eventReportImageView)
    ImageView imageView;

    @BindView(R.id.eventReportTypeLayout)
    TextInputLayout typeLayout;
    @BindView(R.id.eventReportTypeEditText)
    TextInputEditText typeEditText;

    @BindView(R.id.eventReportSeverityLayout)
    TextInputLayout severityLayout;
    @BindView(R.id.eventReportSeverityEditText)
    TextInputEditText severityEditText;

    @BindView(R.id.eventReportStatusLayout)
    TextInputLayout statusLayout;
    @BindView(R.id.eventReportStatusEditText)
    TextInputEditText statusEditText;

    @BindView(R.id.eventReportImpactRadiusLayout)
    TextInputLayout impactRadiusLayout;
    @BindView(R.id.eventReportImpactRadiusEditText)
    TextInputEditText impactRadiusEditText;

    @BindView(R.id.eventReportDescriptionLayout)
    TextInputLayout descriptionLayout;
    @BindView(R.id.eventReportDescriptionEditText)
    TextInputEditText descriptionEditText;

    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;

    private Unbinder unbinder;
    private TypeDTO selectedType;
    private SeverityDTO selectedSeverity;
    private StatusDTO selectedStatus;
    private Bitmap bitmap;
    private Uri cameraImageUri;
    private CreationListener creationListener;
    private Fragment selectorFragment;
    private final FileService fileService = RetrofitClient.getInstance().create(FileService.class);
    private final EventService eventService = RetrofitClient.getInstance().create(EventService.class);
    private final Session session = Session.getInstance();

    private final Validator typeValidator = () -> {
        String messageTypeRequired = getString(R.string.message_type_required);

        if (selectedType == null) {
            typeLayout.setError(messageTypeRequired);
            return false;
        }
        if (typeLayout.getError() != null && typeLayout.getError().equals(messageTypeRequired)) {
            typeLayout.setError(null);
            typeLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator severityValidator = () -> {
        String messageSeverityRequired = getString(R.string.message_severity_required);

        if (selectedSeverity == null) {
            severityLayout.setError(messageSeverityRequired);
            return false;
        }
        if (severityLayout.getError() != null && severityLayout.getError().equals(messageSeverityRequired)) {
            severityLayout.setError(null);
            severityLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator statusValidator = () -> {
        String messageStatusRequired = getString(R.string.message_status_required);

        if (selectedStatus == null) {
            statusLayout.setError(messageStatusRequired);
            return false;
        }
        if (statusLayout.getError() != null && statusLayout.getError().equals(messageStatusRequired)) {
            statusLayout.setError(null);
            statusLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator impactRadiusValidator = () -> {
        String impactRadiusStr = impactRadiusEditText.getEditableText().toString();

        String messageMinImpactRadius = String.format(getString(R.string.message_min_impact_radius), Constants.MIN_IMPACT_RADIUS.intValue());
        String messageMaxImpactRadius = String.format(getString(R.string.message_max_impact_radius), Constants.MAX_IMPACT_RADIUS.intValue());
        String messageImpactRadiusDecimals = getString(R.string.message_impact_radius_decimals);

        if (!impactRadiusStr.isEmpty()) {
            BigDecimal impactRadiusToVerify = new BigDecimal(impactRadiusStr);
            if (impactRadiusToVerify.compareTo(Constants.MIN_IMPACT_RADIUS) < 0) {
                impactRadiusLayout.setError(messageMinImpactRadius);
                return false;
            }
            if (impactRadiusToVerify.compareTo(Constants.MAX_IMPACT_RADIUS) > 0) {
                impactRadiusLayout.setError(messageMaxImpactRadius);
                return false;
            }
            if (!impactRadiusStr.matches(Constants.IMPACT_RADIUS_REGEX)) {
                impactRadiusLayout.setError(messageImpactRadiusDecimals);
                return false;
            }
        }
        if (impactRadiusLayout.getError() != null && (impactRadiusLayout.getError().equals(messageMinImpactRadius) || impactRadiusLayout.getError().equals(messageMaxImpactRadius) || impactRadiusLayout.getError().equals(messageImpactRadiusDecimals))) {
            impactRadiusLayout.setError(null);
            impactRadiusLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator descriptionValidator = () -> {
        String descriptionStr = descriptionEditText.getEditableText().toString();

        String messageDescriptionLength = String.format(getString(R.string.message_description_length), Constants.LENGTH_1000);

        if (descriptionStr.length() > Constants.LENGTH_1000) {
            descriptionLayout.setError(messageDescriptionLength);
            return false;
        }
        if (descriptionLayout.getError() != null && descriptionLayout.getError().equals(messageDescriptionLength)) {
            descriptionLayout.setError(null);
            descriptionLayout.setErrorEnabled(false);
        }

        return true;
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_report, container, false);
        unbinder = ButterKnife.bind(this, view);

        imageView.setImageBitmap(bitmap);

        impactRadiusEditText.addTextChangedListener(TextValidator.of(impactRadiusValidator));
        descriptionEditText.addTextChangedListener(TextValidator.of(descriptionValidator));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (selectorFragment != null && selectorFragment instanceof TypeSelectorFragment) {
            if (selectedType != null) {
                typeEditText.setText(selectedType.label);
            } else {
                typeEditText.setText(null);
            }
            typeValidator.validate();
        }

        if (selectorFragment != null && selectorFragment instanceof SeveritySelectorFragment) {
            if (selectedSeverity != null) {
                severityEditText.setText(selectedSeverity.label);
            } else {
                severityEditText.setText(null);
            }
            severityValidator.validate();
        }

        if (selectorFragment != null && selectorFragment instanceof StatusSelectorFragment) {
            if (selectedStatus != null) {
                statusEditText.setText(selectedStatus.label);
            } else {
                statusEditText.setText(null);
            }
            statusValidator.validate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(requireContext(), R.string.message_permission_camera, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pickPicture();
            } else {
                Toast.makeText(requireContext(), R.string.message_permission_media, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        InputStream imageStream = requireContext().getContentResolver().openInputStream(cameraImageUri);
                        bitmap = resizeBitmap(cropBitmap(BitmapFactory.decodeStream(imageStream)));
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        requireActivity().runOnUiThread(this::showError);
                    }
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri imageUri = data.getData();
                        if (imageUri == null) {
                            return;
                        }
                        InputStream imageStream = requireContext().getContentResolver().openInputStream(imageUri);
                        bitmap = resizeBitmap(cropBitmap(BitmapFactory.decodeStream(imageStream)));
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        requireActivity().runOnUiThread(this::showError);
                    }
                    break;
                }
        }
    }

    public void setOnCreationListener(CreationListener creationListener) {
        this.creationListener = creationListener;
    }

    @OnClick(R.id.eventReportCameraImageView)
    void onCameraClicked() {
        String[] items = {getString(R.string.source_camera), getString(R.string.source_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_source));
        builder.setItems(items, (dialog, item) -> {
            switch (item) {
                case 0:
                    if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    } else {
                        takePicture();
                    }
                    break;
                case 1:
                    if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED ||
                            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, GALLERY_REQUEST);
                    } else {
                        pickPicture();
                    }
                    break;
            }
        });
        builder.show();
    }

    @OnClick(R.id.eventReportTypeEditText)
    void onTypeLayoutClicked() {
        TypeSelectorFragment typeSelectorFragment = new TypeSelectorFragment();
        typeSelectorFragment.setData(session.getTypes(), selectedType);
        typeSelectorFragment.setOnValidationListener(this);
        ((MainActivity) requireActivity()).setFragment(typeSelectorFragment);
    }

    @OnClick(R.id.eventReportSeverityEditText)
    void onSeverityLayoutClicked() {
        SeveritySelectorFragment severitySelectorFragment = new SeveritySelectorFragment();
        severitySelectorFragment.setData(session.getSeverities(), selectedSeverity);
        severitySelectorFragment.setOnValidationListener(this);
        ((MainActivity) requireActivity()).setFragment(severitySelectorFragment);
    }

    @OnClick(R.id.eventReportStatusEditText)
    void onStatusLayoutClicked() {
        StatusSelectorFragment statusSelectorFragment = new StatusSelectorFragment();
        statusSelectorFragment.setData(session.getStatuses(), selectedStatus);
        statusSelectorFragment.setOnValidationListener(this);
        ((MainActivity) requireActivity()).setFragment(statusSelectorFragment);
    }

    @OnClick(R.id.eventReportValidateButton)
    void onValidateClicked() {
        if (!validateForm()) {
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        String impactRadiusStr = impactRadiusEditText.getEditableText().toString();
        BigDecimal impactRadius = !impactRadiusStr.isEmpty() ? new BigDecimal(impactRadiusStr) : null;

        String descriptionStr = descriptionEditText.getEditableText().toString();
        String description = !descriptionStr.isEmpty() ? descriptionStr : null;

        EventCreateDTO eventCreate = new EventCreateDTO();
        eventCreate.latitude = session.getUserLatitude();
        eventCreate.longitude = session.getUserLongitude();
        eventCreate.userId = session.getUserId();
        eventCreate.typeId = selectedType.id;
        eventCreate.severityId = selectedSeverity.id;
        eventCreate.statusId = selectedStatus.id;
        eventCreate.impactRadius = impactRadius;
        eventCreate.description = description;

        MultipartBody.Part part = FileService.getPartFromBitmap(bitmap, Constants.IMAGE_EVENT_FILENAME);
        fileService.postImage(ImageType.EVENT, part)
                .thenCompose(path -> {
                    eventCreate.imagePath = path;
                    return eventService.postEvent(eventCreate);
                })
                .thenAccept(event ->
                        progressDialog.dismiss(() ->
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), R.string.message_event_reported, Toast.LENGTH_SHORT).show();
                                    requireActivity().onBackPressed();
                                    creationListener.onNewEventCreated(this, event);
                                })
                        )
                )
                .exceptionally(throwable -> {
                    progressDialog.dismiss();
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

    @Override
    public void onValidateClicked(TypeSelectorFragment source, TypeDTO selectedType) {
        this.selectedType = selectedType;
        this.selectorFragment = source;
    }

    @Override
    public void onValidateClicked(SeveritySelectorFragment source, SeverityDTO selectedSeverity) {
        this.selectedSeverity = selectedSeverity;
        this.selectorFragment = source;
    }

    @Override
    public void onValidateClicked(StatusSelectorFragment source, StatusDTO selectedStatus) {
        this.selectedStatus = selectedStatus;
        this.selectorFragment = source;
    }

    private boolean validateForm() {
        UserDTO user = session.getUser();
        if (user.firstName == null || user.firstName.isEmpty() || user.lastName == null || user.lastName.isEmpty()) {
            Toast.makeText(requireContext(), R.string.message_profile_full_name_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (bitmap == null) {
            Toast.makeText(requireContext(), R.string.message_image_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        return typeValidator.validate() &
                severityValidator.validate() &
                statusValidator.validate() &
                impactRadiusValidator.validate() &
                descriptionValidator.validate();
    }

    private void showError() {
        Toast.makeText(requireContext(), R.string.message_default_error, Toast.LENGTH_SHORT).show();
    }

    private void pickPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void takePicture() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "New picture with Camera");
        cameraImageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = Math.min(height, width);
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = Math.max(cropW, 0);
        int cropH = (height - width) / 2;
        cropH = Math.max(cropH, 0);
        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        int width = Math.min(bitmap.getWidth(), 500);
        int height = Math.min(bitmap.getHeight(), 500);
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public interface CreationListener {
        void onNewEventCreated(EventReportFragment source, EventDTO event);
    }

}
