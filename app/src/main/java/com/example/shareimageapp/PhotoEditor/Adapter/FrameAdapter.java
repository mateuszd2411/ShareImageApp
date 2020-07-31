package com.example.shareimageapp.PhotoEditor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareimageapp.R;

import java.util.ArrayList;
import java.util.List;

public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.FrameVieHolder> {

    Context context;
    List<Integer> frameList;
    FrameAdapterListener listener;

    int row_selected = -1;

    public FrameAdapter(Context context, FrameAdapterListener listener) {
        this.context = context;
        this.frameList = getFrameList();
        this.listener = listener;
    }

    private List<Integer> getFrameList() {
        List<Integer> result = new ArrayList<>();

        result.add(R.drawable.frame1);
        result.add(R.drawable.frame2);
        result.add(R.drawable.frame3);

        return result;
    }

    @NonNull
    @Override
    public FrameVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.frame_item, parent, false);
        return new FrameVieHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FrameVieHolder holder, int position) {
        if (row_selected == position)
            holder.img_check.setVisibility(View.VISIBLE);
        else
            holder.img_check.setVisibility(View.INVISIBLE);

        holder.img_frame.setImageResource(frameList.get(position));
    }

    @Override
    public int getItemCount() {
        return frameList.size();
    }

    public class FrameVieHolder extends RecyclerView.ViewHolder {

        ImageView img_check, img_frame;

        public FrameVieHolder(@NonNull View itemView) {
            super(itemView);
            img_check = (ImageView) itemView.findViewById(R.id.img_check);
            img_frame = (ImageView) itemView.findViewById(R.id.img_frame);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.omFrameSelected(frameList.get(getAdapterPosition()));

                    row_selected = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface FrameAdapterListener {
        void omFrameSelected(int frame);
    }

}

