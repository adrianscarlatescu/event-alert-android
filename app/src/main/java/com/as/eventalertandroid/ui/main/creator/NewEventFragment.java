package com.as.eventalertandroid.ui.main.creator;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.Event;
import com.as.eventalertandroid.net.model.EventSeverity;
import com.as.eventalertandroid.net.model.EventTag;
import com.as.eventalertandroid.net.model.body.EventBody;
import com.as.eventalertandroid.net.service.EventService;
import com.as.eventalertandroid.net.service.FileService;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.as.eventalertandroid.ui.main.creator.severity.SeveritySelectorFragment;
import com.as.eventalertandroid.ui.main.creator.tag.TagSelectorFragment;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MultipartBody;

public class NewEventFragment extends Fragment implements
        SeveritySelectorFragment.ValidationListener,
        TagSelectorFragment.ValidationListener {

    @BindView(R.id.newEventImageView)
    ImageView imageView;
    @BindView(R.id.newEventTagTextView)
    TextView tagTextView;
    @BindView(R.id.newEventSeverityTextView)
    TextView severityTextView;
    @BindView(R.id.newEventDescriptionEditText)
    EditText descriptionEditText;

    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;

    private Unbinder unbinder;
    private EventTag selectedTag;
    private EventSeverity selectedSeverity;
    private Bitmap bitmap;
    private Uri cameraImageUri;
    private CreationListener creationListener;
    private FileService fileService = RetrofitClient.getRetrofitInstance().create(FileService.class);
    private EventService eventService = RetrofitClient.getRetrofitInstance().create(EventService.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_event, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (selectedTag == null) {
            tagTextView.setText(getString(R.string.none));
        } else {
            tagTextView.setText(selectedTag.name);
        }

        if (selectedSeverity == null) {
            severityTextView.setText(getString(R.string.none));
        } else {
            severityTextView.setText(selectedSeverity.name);
        }

        imageView.setImageBitmap(bitmap);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
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

    @OnClick(R.id.newEventCameraImageView)
    void onCameraClicked() {
        String[] items = {getString(R.string.source_camera), getString(R.string.source_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_source));
        builder.setItems(items, (dialog, item) -> {
            switch (item) {
                case 0:
                    if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST);
                    } else {
                        takePicture();
                    }
                    break;
                case 1:
                    pickPicture();
                    break;
            }
        });
        builder.show();
    }

    @OnClick(R.id.newEventTagFrameLayout)
    void onTagLayoutClicked() {
        TagSelectorFragment tagSelectorFragment = new TagSelectorFragment();
        tagSelectorFragment.setData(Session.getInstance().getTags(), selectedTag);
        tagSelectorFragment.setOnValidationListener(this);
        ((MainActivity) requireActivity()).setFragment(tagSelectorFragment);
    }

    @OnClick(R.id.newEventSeverityFrameLayout)
    void onSeverityLayoutClicked() {
        SeveritySelectorFragment severitySelectorFragment = new SeveritySelectorFragment();
        severitySelectorFragment.setData(Session.getInstance().getSeverities(), selectedSeverity);
        severitySelectorFragment.setOnValidationListener(this);
        ((MainActivity) requireActivity()).setFragment(severitySelectorFragment);
    }

    @OnClick(R.id.newEventValidateButton)
    void onValidateClicked() {
        if (selectedTag == null) {
            Toast.makeText(requireContext(), getString(R.string.message_tag_required), Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedSeverity == null) {
            Toast.makeText(requireContext(), getString(R.string.message_severity_required), Toast.LENGTH_SHORT).show();
            return;
        }
        if (bitmap == null) {
            Toast.makeText(requireContext(), getString(R.string.message_image_required), Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        EventBody newEvent = new EventBody();
        newEvent.latitude = Session.getInstance().getLatitude();
        newEvent.longitude = Session.getInstance().getLongitude();
        newEvent.userId = Session.getInstance().getUser().id;
        newEvent.tagId = selectedTag.id;
        newEvent.severityId = selectedSeverity.id;
        newEvent.description = descriptionEditText.getText().toString();

        MultipartBody.Part part = FileService.getPartFromBitmap(bitmap, Constants.IMAGE_EVENT_FILENAME);
        fileService.saveImage(part)
                .thenCompose(path -> {
                    newEvent.imagePath = path;
                    return eventService.save(newEvent);
                })
                .thenAccept(event ->
                        progressDialog.dismiss(() ->
                                requireActivity().runOnUiThread(() -> {
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
    public void onValidateClicked(SeveritySelectorFragment source, EventSeverity selectedSeverity) {
        this.selectedSeverity = selectedSeverity;
    }

    @Override
    public void onValidateClicked(TagSelectorFragment source, EventTag selectedTag) {
        this.selectedTag = selectedTag;
    }

    private void showError() {
        Toast.makeText(requireContext(), getString(R.string.message_default_error), Toast.LENGTH_SHORT).show();
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
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth() < 500 ? bitmap.getWidth() : 500;
        int height = bitmap.getHeight() < 500 ? bitmap.getHeight() : 500;
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public interface CreationListener {
        void onNewEventCreated(NewEventFragment source, Event event);
    }

}
