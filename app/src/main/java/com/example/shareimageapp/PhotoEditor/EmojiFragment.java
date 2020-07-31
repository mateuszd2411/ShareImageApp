package com.example.shareimageapp.PhotoEditor;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shareimageapp.PhotoEditor.Adapter.EmojiAdapter;
import com.example.shareimageapp.PhotoEditor.Interface.EmojiFragmentListener;
import com.example.shareimageapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ja.burhanrashid52.photoeditor.PhotoEditor;

public class EmojiFragment extends BottomSheetDialogFragment implements EmojiAdapter.EmojiAdapterListener {

    RecyclerView recycler_emoji;
    static EmojiFragment instance;

    EmojiFragmentListener listener;

    public void setListener(EmojiFragmentListener listener) {
        this.listener = listener;
    }

    public static EmojiFragment getInstance() {
        if (instance == null)
            instance = new EmojiFragment();
        return instance;
    }

    public EmojiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_emoji, container, false);

        recycler_emoji = (RecyclerView) itemView.findViewById(R.id.recycler_emoji);
        recycler_emoji.setHasFixedSize(true);
        recycler_emoji.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        EmojiAdapter adapter = new EmojiAdapter(getContext(), PhotoEditor.getEmojis(getContext()), this);
        recycler_emoji.setAdapter(adapter);

        return itemView;
    }

    @Override
    public void OnEmojiItemSelected(String emoji) {
        listener.onEmojiSelected(emoji);
    }
}