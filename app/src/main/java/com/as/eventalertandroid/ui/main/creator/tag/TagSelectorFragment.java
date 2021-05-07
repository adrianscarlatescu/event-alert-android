package com.as.eventalertandroid.ui.main.creator.tag;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.EventTag;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TagSelectorFragment extends Fragment implements TagSelectorAdapter.ClickListener {

    @BindView(R.id.tagSelectorRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private ValidationListener validationListener;
    private TagSelectorAdapter adapter = new TagSelectorAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_selector, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(TagSelectorAdapter source) {
        // Nothing to do
    }

    public void setData(List<EventTag> tags, EventTag selectedTags) {
        adapter.setTags(tags);
        adapter.setSelectedTag(selectedTags);
    }

    public void setOnValidationListener(TagSelectorFragment.ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.tagSelectorValidateButton)
    void onValidateClicked() {
        if (adapter.getSelectedTag() == null) {
            Toast.makeText(requireContext(), getString(R.string.message_tag_required), Toast.LENGTH_SHORT).show();
            return;
        }
        validationListener.onValidateClicked(this, adapter.getSelectedTag());
        requireActivity().onBackPressed();
    }

    public interface ValidationListener {
        void onValidateClicked(TagSelectorFragment source, EventTag selectedTag);
    }

}
