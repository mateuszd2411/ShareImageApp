package com.example.shareimageapp.PhotoEditor.Adapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoeditor.BrushFragment;
import com.example.photoeditor.R;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    Context context;
    List<Integer> colorList;
    ColorAdapterListener listener;

    public ColorAdapter(Context context, ColorAdapterListener listener) {
        this.context = context;
        this.colorList = getColorList();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item, parent, false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.color_section.setCardBackgroundColor(colorList.get(position));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {

        public CardView color_section;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            color_section = (CardView) itemView.findViewById(R.id.color_section);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onColorSelected(colorList.get(getAdapterPosition()));
                }
            });
        }
    }

    private List<Integer> getColorList() {
        List<Integer> colorList = new ArrayList<>();

        colorList.add(Color.parseColor("#0d1418"));
        colorList.add(Color.parseColor("#ff004d"));
        colorList.add(Color.parseColor("#d47ca0"));
        colorList.add(Color.parseColor("#7cb07c"));
        colorList.add(Color.parseColor("#636466"));
        colorList.add(Color.parseColor("#6d99b4"));
        colorList.add(Color.parseColor("#d47ca0"));
        colorList.add(Color.parseColor("#e35e5e"));

        colorList.add(Color.parseColor("#00ffff"));
        colorList.add(Color.parseColor("#d8d9da"));
        colorList.add(Color.parseColor("#fee11a"));
        colorList.add(Color.parseColor("#007030"));
        colorList.add(Color.parseColor("#de4118"));
        colorList.add(Color.parseColor("#db7093"));
        colorList.add(Color.parseColor("#ffefd5"));
        colorList.add(Color.parseColor("#cd919e"));

        return colorList;
    }

    public interface ColorAdapterListener
    {
        void onColorSelected(int color);
    }

}