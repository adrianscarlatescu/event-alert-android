package com.as.eventalertandroid.ui.common.event;

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
import com.as.eventalertandroid.handler.ColorHandler;
import com.as.eventalertandroid.handler.DistanceHandler;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.Event;
import com.as.eventalertandroid.net.model.request.EventCommentRequest;
import com.as.eventalertandroid.net.service.EventCommentService;
import com.as.eventalertandroid.ui.common.ImageDialog;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.as.eventalertandroid.ui.main.MainActivity;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
    @BindView(R.id.eventDetailsTagImageView)
    ImageView tagImageView;
    @BindView(R.id.eventDetailsTagTextView)
    TextView tagTextView;
    @BindView(R.id.eventDetailsSeverityTextView)
    TextView severityTextView;
    @BindView(R.id.eventDetailsDateTimeTextView)
    TextView dateTimeTextView;
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
    private final EventCommentService eventCommentService = RetrofitClient.getInstance().create(EventCommentService.class);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
    private final Session session = Session.getInstance();
    private final CommentsAdapter adapter = new CommentsAdapter();
    private Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        ImageHandler.loadImage(eventImageView, event.imagePath);
        ImageHandler.loadImage(tagImageView, event.tag.imagePath, placeholder);
        ImageHandler.loadImage(creatorImageView, event.user.imagePath, placeholderPadding);

        severityCardView.setCardBackgroundColor(ColorHandler.getColorFromHex(event.severity.color, 0.8f));
        tagTextView.setText(event.tag.name);
        severityTextView.setText(event.severity.name);
        dateTimeTextView.setText(event.dateTime.format(dateTimeFormatter));
        String address = DistanceHandler.getAddress(new Geocoder(requireContext(), Locale.getDefault()), event.latitude, event.longitude);
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
            eventCommentService.getByEventId(event.id)
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

    public void setEvent(Event event) {
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

    @OnClick(R.id.eventDetailsSeeOnMapLinearLayout)
    void onSeeOnMapClicked() {
        SeeOnMapFragment seeOnMapFragment = new SeeOnMapFragment();
        seeOnMapFragment.setEvent(event);
        ((MainActivity) requireActivity()).setFragment(seeOnMapFragment);
    }

    @OnClick(R.id.eventDetailsCommentButton)
    void onCommentClicked() {
        CommentDialog commentDialog = new CommentDialog(requireContext()) {
            @Override
            public void onValidateClicked(String comment) {
                EventCommentRequest commentRequest = new EventCommentRequest();
                commentRequest.comment = comment;
                commentRequest.eventId = event.id;
                commentRequest.userId = session.getUserId();

                ProgressDialog progressDialog = new ProgressDialog(requireContext());
                progressDialog.show();

                eventCommentService.save(commentRequest)
                        .thenAccept(eventComment ->
                                progressDialog.dismiss(() ->
                                        requireActivity().runOnUiThread(() -> {
                                            if (noCommentsTextView.getVisibility() == View.VISIBLE) {
                                                noCommentsTextView.setVisibility(View.GONE);
                                            }
                                            adapter.addComment(eventComment);
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
