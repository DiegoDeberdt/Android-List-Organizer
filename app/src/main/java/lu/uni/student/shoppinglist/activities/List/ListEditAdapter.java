package lu.uni.student.shoppinglist.activities.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lu.uni.student.shoppinglist.R;

public class ListEditAdapter extends RecyclerView.Adapter<ListEditAdapter.ViewHolder> {

    private final Context context;
    private final int[] localDataSet;
    private int indexOfSelectedImage = 0;
    private List<ImageView> allImageViews = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.listImageSelection);
        }
    }

    public ListEditAdapter(Context context, int[] dataset) {
        this.context = context;
        this.localDataSet = dataset;
        this.indexOfSelectedImage = 0;
    }

    public ListEditAdapter(Context context, int[] dataset, int indexOfSelectedImage) {
        this.context = context;
        this.localDataSet = dataset;
        this.indexOfSelectedImage = indexOfSelectedImage;
    }

    public int getSelectedImageResource() {
        return indexOfSelectedImage;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_edit_image_row, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        allImageViews.add(holder.imageView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        viewHolder.imageView.setImageResource(localDataSet[position]);
        if (position == indexOfSelectedImage) {
            viewHolder.imageView.setBackgroundResource(R.color.purple_200);
        }

        viewHolder.imageView.setOnClickListener(view -> {
            allImageViews.get(indexOfSelectedImage).setBackgroundResource(0);
            view.setBackgroundResource(R.color.purple_200);
            indexOfSelectedImage = position;
        });
    }

    @Override
    public int getItemCount() {
        return this.localDataSet.length;
    }
}
