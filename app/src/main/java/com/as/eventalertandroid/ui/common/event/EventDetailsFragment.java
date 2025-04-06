package com.as.eventalertandroid.ui.common.event;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.LocationHandler;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.CommentCreateDTO;
import com.as.eventalertandroid.net.model.EventDTO;
import com.as.eventalertandroid.net.service.CommentService;
import com.as.eventalertandroid.ui.common.ImageDialog;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.as.eventalertandroid.ui.common.event.comment.EventCommentDialog;
import com.as.eventalertandroid.ui.common.event.map.EventMapFragment;
import com.as.eventalertandroid.ui.main.MainActivity;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EventDetailsFragment extends Fragment {

    @BindView(R.id.eventDetailsImageView)
    ImageView eventImageView;
    @BindView(R.id.eventDetailsSeverityCardView)
    CardView severityCardView;
    @BindView(R.id.eventDetailsTypeImageView)
    ImageView typeImageView;
    @BindView(R.id.eventDetailsTypeTextView)
    TextView typeTextView;
    @BindView(R.id.eventDetailsSeverityTextView)
    TextView severityTextView;
    @BindView(R.id.eventDetailsStatusTextView)
    TextView statusTextView;
    @BindView(R.id.eventDetailsImpactRadiusTextView)
    TextView impactRadiusTextView;
    @BindView(R.id.eventDetailsCreatedAtTextView)
    TextView createdAtTextView;
    @BindView(R.id.eventDetailsAddressTextView)
    TextView addressTextView;
    @BindView(R.id.eventDetailsDescriptionTextView)
    TextView descriptionTextView;
    @BindView(R.id.eventDetailsCreatorImageView)
    ImageView creatorImageView;
    @BindView(R.id.eventDetailsCreatorNameTextView)
    TextView creatorNameTextView;
    @BindView(R.id.eventDetailsCommentsProgressBar)
    ProgressBar commentsProgressBar;
    @BindView(R.id.eventDetailsCommentsNoResultsTextView)
    TextView noCommentsTextView;
    @BindView(R.id.eventDetailsCommentsRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.item_placeholder)
    Drawable placeholder;
    @BindDrawable(R.drawable.item_placeholder_padding)
    Drawable placeholderPadding;
    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    @BindString(R.string.reported_by)
    String reportedByFormat;

    private Unbinder unbinder;
    private final CommentService commentService = RetrofitClient.getInstance().create(CommentService.class);
    private final Session session = Session.getInstance();
    private final EventCommentsAdapter adapter = new EventCommentsAdapter();
    private EventDTO event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        ImageHandler.loadImage(eventImageView, event.imagePath);
        ImageHandler.loadImage(typeImageView, event.type.imagePath, placeholder);
        ImageHandler.loadImage(creatorImageView, event.user.imagePath, placeholderPadding);

        severityCardView.setCardBackgroundColor(Color.parseColor(event.severity.color));

        if (event.impactRadius != null) {
            impactRadiusTextView.setText(String.format(getString(R.string.impact_radius_km), event.impactRadius.stripTrailingZeros().toPlainString()));
        } else {
            impactRadiusTextView.setVisibility(View.GONE);
        }

        typeTextView.setText(event.type.label);
        severityTextView.setText(event.severity.label);
        statusTextView.setText(event.status.label);
        createdAtTextView.setText(event.createdAt.format(Constants.defaultDateTimeFormatter));

        String address = LocationHandler.getAddress(new Geocoder(requireContext(), Locale.getDefault()), event.latitude, event.longitude);
        addressTextView.setText(address);

        String creatorName = event.user.firstName + " " + event.user.lastName;
        creatorNameTextView.setText(String.format(reportedByFormat, creatorName));

        if (event.description != null && !event.description.isEmpty()) {
            descriptionTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setText(event.description);
        }

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            commentsProgressBar.setVisibility(View.VISIBLE);
            commentService.getCommentsByEventId(event.id)
                    .thenAccept(comments ->
                            requireActivity().runOnUiThread(() -> {
                                commentsProgressBar.setVisibility(View.GONE);
                                if (comments.isEmpty()) {
                                    noCommentsTextView.setVisibility(View.VISIBLE);
                                } else {
                                    adapter.setComments(comments);
                                }
                            })
                    )
                    .exceptionally(throwable -> {
                        requireActivity().runOnUiThread(() -> {
                                    commentsProgressBar.setVisibility(View.GONE);
                                    if (adapter.getItemCount() == 0) {
                                        noCommentsTextView.setVisibility(View.VISIBLE);
                                    }
                                }
                        );
                        ErrorHandler.showMessage(requireActivity(), throwable);
                        return null;
                    });
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    @OnClick(R.id.eventDetailsImageView)
    void onImageClicked() {
        ImageDialog imageDialog = new ImageDialog(requireContext(), event.imagePath);
        imageDialog.show();
    }

    @OnClick(R.id.eventDetailsCreatorImageView)
    void onCreatorImageClicked() {
        ImageDialog imageDialog = new ImageDialog(requireContext(), event.user.imagePath);
        imageDialog.show();
    }

    @OnClick(R.id.eventDetailsMapImageView)
    void onMapClicked() {
        EventMapFragment eventMapFragment = new EventMapFragment();
        eventMapFragment.setEvent(event);
        ((MainActivity) requireActivity()).setFragment(eventMapFragment);
    }

    @OnClick(R.id.eventDetailsCommentButton)
    void onCommentClicked() {
        EventCommentDialog commentDialog = new EventCommentDialog(requireContext()) {
            @Override
            public void onValidateClicked(String comment) {
                CommentCreateDTO commentCreate = new CommentCreateDTO();
                commentCreate.comment = comment;
                commentCreate.eventId = event.id;
                commentCreate.userId = session.getUserId();

                ProgressDialog progressDialog = new ProgressDialog(requireContext());
                progressDialog.show();

                commentService.postComment(commentCreate)
                        .thenAccept(newComment ->
                                progressDialog.dismiss(() ->
                                        requireActivity().runOnUiThread(() -> {
                                            if (noCommentsTextView.getVisibility() == View.VISIBLE) {
                                                noCommentsTextView.setVisibility(View.GONE);
                                            }
                                            adapter.addComment(newComment);
                                            recyclerView.scrollToPosition(0);
                                        })
                                )
                        )
                        .exceptionally(throwable -> {
                            progressDialog.dismiss();
                            ErrorHandler.showMessage(requireActivity(), throwable);
                            return null;
                        });

                dismiss();
            }
        };
        commentDialog.show();
    }

}
