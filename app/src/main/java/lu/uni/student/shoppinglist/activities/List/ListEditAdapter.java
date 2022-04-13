package lu.uni.student.shoppinglist.activities.List;

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

    private final int[] resourceIds;
    private int indexOfSelectedIcon;
    private final List<ImageView> allImageViews = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.listImageSelection);
        }
    }

    public ListEditAdapter(int[] resourceIds) {
        this.resourceIds = resourceIds;
        this.indexOfSelectedIcon = 0;
    }

    public ListEditAdapter(int[] dataset, int indexOfSelectedIcon) {
        this.resourceIds = dataset;
        this.indexOfSelectedIcon = indexOfSelectedIcon;
    }

    public int getIndexOfSelectedIcon() {
        return indexOfSelectedIcon;
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

        viewHolder.imageView.setImageResource(resourceIds[position]);
        if (position == indexOfSelectedIcon) {
            viewHolder.imageView.setBackgroundResource(R.color.purple_200);
        }

        viewHolder.imageView.setOnClickListener(view -> {
            allImageViews.get(indexOfSelectedIcon).setBackgroundResource(0);
            view.setBackgroundResource(R.color.purple_200);
            indexOfSelectedIcon = viewHolder.getAdapterPosition();
        });
    }

    @Override
    public int getItemCount() {
        return this.resourceIds.length;
    }
}
