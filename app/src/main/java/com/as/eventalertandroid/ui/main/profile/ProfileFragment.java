package com.as.eventalertandroid.ui.main.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import com.as.eventalertandroid.enums.Gender;
import com.as.eventalertandroid.handler.DeviceHandler;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.User;
import com.as.eventalertandroid.net.model.request.SubscriptionStatusRequest;
import com.as.eventalertandroid.net.model.request.UserRequest;
import com.as.eventalertandroid.net.service.AuthService;
import com.as.eventalertandroid.net.service.FileService;
import com.as.eventalertandroid.net.service.SubscriptionService;
import com.as.eventalertandroid.net.service.UserService;
import com.as.eventalertandroid.ui.auth.AuthActivity;
import com.as.eventalertandroid.ui.common.ProgressDialog;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MultipartBody;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    @BindView(R.id.profilePhotoImageView)
    ImageView photoImageView;
    @BindView(R.id.profileIdTextView)
    TextView idTextView;
    @BindView(R.id.profileEmailTextView)
    TextView emailTextView;
    @BindView(R.id.profilePasswordTextView)
    TextView passwordTextView;
    @BindView(R.id.profileFirstNameEditText)
    EditText firstNameEditText;
    @BindView(R.id.profileLastNameEditText)
    EditText lastNameEditText;
    @BindView(R.id.profileDateOfBirthEditText)
    EditText dateOfBirthEditText;
    @BindView(R.id.profilePhoneNumberEditText)
    EditText phoneEditText;
    @BindView(R.id.profileGenderTextView)
    TextView genderTextView;
    @BindView(R.id.profileJoinDateTextView)
    TextView joinDateTextView;
    @BindView(R.id.profileReportsNumberTextView)
    TextView numberOfReportsTextView;

    @BindString(R.string.profile_user_id)
    String userIdFormat;
    @BindString(R.string.profile_user_email)
    String userEmailFormat;
    @BindString(R.string.profile_user_password)
    String userPasswordFormat;
    @BindString(R.string.profile_user_reports_number)
    String userReportsNumberFormat;
    @BindString(R.string.profile_user_join_date)
    String userJoinDateFormat;

    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;

    private Unbinder unbinder;
    private DatePickerDialog datePicker;
    private Bitmap bitmap;
    private Uri cameraImageUri;
    private final User user = new User();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
    private final UserService userService = RetrofitClient.getInstance().create(UserService.class);
    private final AuthService authService = RetrofitClient.getInstance().create(AuthService.class);
    private final FileService fileService = RetrofitClient.getInstance().create(FileService.class);
    private final SubscriptionService subscriptionService = RetrofitClient.getInstance().create(SubscriptionService.class);
    private final Session session = Session.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
        } else if (requestCode == GALLERY_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pickPicture();
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
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        photoImageView.setImageBitmap(bitmap);
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
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        photoImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        requireActivity().runOnUiThread(this::showError);
                    }
                    break;
                }
        }
    }

    @OnClick(R.id.profilePhotoImageView)
    void onProfilePhotoClicked() {
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

    @OnClick(R.id.profileEmailTextView)
    void onEmailClicked() {
        // TODO
    }

    @OnClick(R.id.profilePasswordTextView)
    void onPasswordClicked() {
        // TODO
    }

    @OnClick(R.id.profileDateOfBirthEditText)
    void onDateOfBirthClicked() {
        datePicker.show();
    }

    @OnClick(R.id.profileGenderFrameLayout)
    void onGenderClicked() {
        user.gender = user.gender == null ? Gender.MALE : user.gender == Gender.MALE ? Gender.FEMALE : Gender.MALE;
        genderTextView.animate().alpha(0).setDuration(150)
                .withEndAction(() -> {
                    genderTextView.setText(getString(user.gender.getName()));
                    genderTextView.animate().alpha(1).setDuration(150);
                });
    }

    @OnClick(R.id.profileLogoutButton)
    void onLogoutClicked() {
        Activity activity = requireActivity();
        CompletableFuture
                .supplyAsync(() -> {
                    if (session.getSubscription() == null) {
                        return CompletableFuture.completedFuture(null);
                    }

                    SubscriptionStatusRequest subscriptionStatusRequest = new SubscriptionStatusRequest();
                    subscriptionStatusRequest.isActive = false;
                    return subscriptionService.updateStatus(session.getUserId(), DeviceHandler.getAndroidId(requireContext()), subscriptionStatusRequest);
                })
                .thenCompose(subscription  -> authService.logout())
                .thenAccept(aVoid -> {
                    activity.getApplicationContext()
                            .getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply();

                    Intent intent = new Intent(activity, AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    activity.finish();
                })
                .exceptionally(throwable -> {
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

    @OnClick(R.id.profileValidateButton)
    void onValidateClicked() {
        if (firstNameEditText.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.message_first_name_required), Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastNameEditText.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.message_last_name_required), Toast.LENGTH_SHORT).show();
            return;
        }

        user.firstName = firstNameEditText.getText().toString();
        user.lastName = lastNameEditText.getText().toString();
        user.phoneNumber = phoneEditText.getText().toString();

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();
        if (bitmap == null) {
            updateUser().whenComplete((aVoid, throwable) -> progressDialog.dismiss());
        } else {
            MultipartBody.Part part = FileService.getPartFromBitmap(bitmap, Constants.IMAGE_USER_FILENAME);
            fileService.saveImage(part)
                    .thenCompose(path -> {
                        user.imagePath = path;
                        return updateUser();
                    })
                    .whenComplete((aVoid, throwable) -> progressDialog.dismiss());
        }
    }

    void init() {
        User sessionUser = session.getUser();
        user.id = sessionUser.id;
        user.imagePath = sessionUser.imagePath;
        user.email = sessionUser.email;
        user.firstName = sessionUser.firstName;
        user.lastName = sessionUser.lastName;
        user.dateOfBirth = sessionUser.dateOfBirth;
        user.phoneNumber = sessionUser.phoneNumber;
        user.gender = sessionUser.gender;
        user.userRoles = sessionUser.userRoles;
        user.joinDateTime = sessionUser.joinDateTime;
        user.reportsNumber = sessionUser.reportsNumber;

        String password = requireActivity().getApplicationContext()
                .getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
                .getString(Constants.USER_PASSWORD, "");

        ImageHandler.loadImage(photoImageView, user.imagePath, requireContext().getDrawable(R.drawable.item_placeholder_padding));
        idTextView.setText(String.format(userIdFormat, user.id));
        emailTextView.setText(String.format(userEmailFormat, user.email));
        passwordTextView.setText(String.format(userPasswordFormat, password.replaceAll("(?s).", "\u2022")));
        firstNameEditText.setText(user.firstName);
        lastNameEditText.setText(user.lastName);
        dateOfBirthEditText.setText(user.dateOfBirth == null ? "" : user.dateOfBirth.format(dateFormatter));
        phoneEditText.setText(user.phoneNumber);
        genderTextView.setText(user.gender == null ? "" : getString(user.gender.getName()));
        joinDateTextView.setText(String.format(userJoinDateFormat, user.joinDateTime.format(dateTimeFormatter)));
        numberOfReportsTextView.setText(String.format(userReportsNumberFormat, user.reportsNumber));

        LocalDate now = LocalDate.now();
        datePicker = new DatePickerDialog(requireContext(),
                (dateView, year, month, dayOfMonth) -> {
                    user.dateOfBirth = LocalDate.of(year, (month + 1), dayOfMonth);
                    dateOfBirthEditText.setText(user.dateOfBirth.format(dateFormatter));
                },
                user.dateOfBirth == null ? now.getYear() : user.dateOfBirth.getYear(),
                user.dateOfBirth == null ? now.getMonthValue() - 1 : user.dateOfBirth.getMonthValue() - 1,
                user.dateOfBirth == null ? now.getDayOfMonth() : user.dateOfBirth.getDayOfMonth());

        bitmap = null;
        cameraImageUri = null;
    }

    private CompletableFuture<Void> updateUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.firstName = user.firstName;
        userRequest.lastName = user.lastName;
        userRequest.dateOfBirth = user.dateOfBirth;
        userRequest.phoneNumber = user.phoneNumber;
        userRequest.imagePath = user.imagePath;
        userRequest.gender = user.gender;
        userRequest.roles = Stream.of(user.userRoles).map(userRole -> userRole.name).collect(Collectors.toSet());

        return userService.updateProfile(userRequest)
                .thenAccept(result -> {
                    session.setUser(result);
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), getString(R.string.message_success), Toast.LENGTH_SHORT).show());
                })
                .exceptionally(throwable -> {
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
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

    private void showError() {
        Toast.makeText(requireContext(), getString(R.string.message_default_error), Toast.LENGTH_SHORT).show();
    }

}
