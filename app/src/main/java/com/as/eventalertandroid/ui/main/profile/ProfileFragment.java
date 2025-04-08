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
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.enums.ImageType;
import com.as.eventalertandroid.enums.id.GenderId;
import com.as.eventalertandroid.handler.DeviceHandler;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.GenderDTO;
import com.as.eventalertandroid.net.model.SubscriptionStatusUpdateDTO;
import com.as.eventalertandroid.net.model.UserDTO;
import com.as.eventalertandroid.net.model.UserUpdateDTO;
import com.as.eventalertandroid.net.service.AuthService;
import com.as.eventalertandroid.net.service.FileService;
import com.as.eventalertandroid.net.service.SubscriptionService;
import com.as.eventalertandroid.net.service.UserService;
import com.as.eventalertandroid.ui.auth.AuthActivity;
import com.as.eventalertandroid.ui.common.ProgressDialog;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
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
    @BindView(R.id.profileRolesTextView)
    TextView rolesTextView;

    @BindString(R.string.profile_user_id)
    String userIdFormat;
    @BindString(R.string.profile_user_email)
    String userEmailFormat;
    @BindString(R.string.profile_user_password)
    String userPasswordFormat;
    @BindString(R.string.profile_user_reports_number)
    String userReportsNumberFormat;
    @BindString(R.string.profile_user_roles)
    String userRolesFormat;
    @BindString(R.string.profile_user_join_date)
    String userJoinDateFormat;

    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;

    private Unbinder unbinder;
    private DatePickerDialog datePicker;
    private Bitmap bitmap;
    private Uri cameraImageUri;
    private final UserDTO user = new UserDTO();
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

    }

    @OnClick(R.id.profilePasswordTextView)
    void onPasswordClicked() {

    }

    @OnClick(R.id.profileDateOfBirthEditText)
    void onDateOfBirthClicked() {
        datePicker.show();
    }

    @OnClick(R.id.profileGenderFrameLayout)
    void onGenderClicked() {
        Optional<GenderDTO> maleGender = session.getGenders().stream().filter(gender -> gender.id == GenderId.MALE).findFirst();
        Optional<GenderDTO> femaleGender = session.getGenders().stream().filter(gender -> gender.id == GenderId.FEMALE).findFirst();

        if (!maleGender.isPresent() || !femaleGender.isPresent()) {
            return;
        }

        user.gender = user.gender == null
                ? maleGender.get()
                : user.gender.id == GenderId.MALE ? femaleGender.get() : maleGender.get();
        genderTextView.animate().alpha(0).setDuration(150)
                .withEndAction(() -> {
                    genderTextView.setText(user.gender.label);
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

                    SubscriptionStatusUpdateDTO subscriptionStatusUpdate = new SubscriptionStatusUpdateDTO();
                    subscriptionStatusUpdate.isActive = false;
                    return subscriptionService.updateStatus(session.getUserId(), DeviceHandler.getAndroidId(requireContext()), subscriptionStatusUpdate);
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
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String phoneNumber = phoneEditText.getText().toString();

        if (firstName.isEmpty()) {
            Toast.makeText(requireContext(), R.string.message_first_name_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastName.isEmpty()) {
            Toast.makeText(requireContext(), R.string.message_last_name_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(requireContext(), R.string.message_phone_number_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (firstName.length() > Constants.LENGTH_50) {
            String message = String.format(getString(R.string.message_first_name_length), Constants.LENGTH_50);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastName.length() > Constants.LENGTH_50) {
            String message = String.format(getString(R.string.message_last_name_length), Constants.LENGTH_50);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phoneNumber.matches(Constants.PHONE_NUMBER_REGEX)) {
            Toast.makeText(requireContext(), R.string.message_phone_number_format, Toast.LENGTH_SHORT).show();
            return;
        }

        user.firstName = firstName;
        user.lastName = lastName;
        user.phoneNumber = phoneNumber;

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();
        if (bitmap == null) {
            updateUser().whenComplete((aVoid, throwable) -> progressDialog.dismiss());
        } else {
            MultipartBody.Part part = FileService.getPartFromBitmap(bitmap, Constants.IMAGE_USER_FILENAME);
            fileService.postImage(ImageType.USER, part)
                    .thenCompose(path -> {
                        user.imagePath = path;
                        return updateUser();
                    })
                    .whenComplete((aVoid, throwable) -> progressDialog.dismiss());
        }
    }

    void init() {
        UserDTO sessionUser = session.getUser();
        user.id = sessionUser.id;
        user.imagePath = sessionUser.imagePath;
        user.email = sessionUser.email;
        user.firstName = sessionUser.firstName;
        user.lastName = sessionUser.lastName;
        user.dateOfBirth = sessionUser.dateOfBirth;
        user.phoneNumber = sessionUser.phoneNumber;
        user.gender = sessionUser.gender;
        user.roles = sessionUser.roles;
        user.joinedAt = sessionUser.joinedAt;
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
        dateOfBirthEditText.setText(user.dateOfBirth == null ? "" : user.dateOfBirth.format(Constants.defaultDateFormatter));
        phoneEditText.setText(user.phoneNumber);
        genderTextView.setText(user.gender == null ? "" : user.gender.label);
        joinDateTextView.setText(String.format(userJoinDateFormat, user.joinedAt.format(Constants.defaultDateTimeFormatter)));
        numberOfReportsTextView.setText(String.format(userReportsNumberFormat, user.reportsNumber));
        String roleLabels = Arrays.stream(user.roles).map(role -> role.label).collect(Collectors.joining(", "));
        rolesTextView.setText(String.format(userRolesFormat, roleLabels));

        LocalDate now = LocalDate.now();
        datePicker = new DatePickerDialog(requireContext(),
                (dateView, year, month, dayOfMonth) -> {
                    user.dateOfBirth = LocalDate.of(year, (month + 1), dayOfMonth);
                    dateOfBirthEditText.setText(user.dateOfBirth.format(Constants.defaultDateFormatter));
                },
                user.dateOfBirth == null ? now.getYear() : user.dateOfBirth.getYear(),
                user.dateOfBirth == null ? now.getMonthValue() - 1 : user.dateOfBirth.getMonthValue() - 1,
                user.dateOfBirth == null ? now.getDayOfMonth() : user.dateOfBirth.getDayOfMonth());

        bitmap = null;
        cameraImageUri = null;
    }

    private CompletableFuture<Void> updateUser() {
        UserUpdateDTO userUpdate = new UserUpdateDTO();
        userUpdate.firstName = user.firstName;
        userUpdate.lastName = user.lastName;
        userUpdate.dateOfBirth = user.dateOfBirth;
        userUpdate.phoneNumber = user.phoneNumber;
        userUpdate.imagePath = user.imagePath;
        userUpdate.genderId = user.gender.id;
        userUpdate.roleIds = Stream.of(user.roles).map(userRole -> userRole.id).collect(Collectors.toSet());

        return userService.putProfile(userUpdate)
                .thenAccept(result -> {
                    session.setUser(result);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), R.string.message_success, Toast.LENGTH_SHORT).show()
                    );
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
        Toast.makeText(requireContext(), R.string.message_default_error, Toast.LENGTH_SHORT).show();
    }

}
