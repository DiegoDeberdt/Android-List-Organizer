package it.geronimo.shoppinglist.activities.Item;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.geronimo.shoppinglist.R;
import it.geronimo.shoppinglist.activities.Crud;
import it.geronimo.shoppinglist.activities.Extra;
import it.geronimo.shoppinglist.repository.ShoppingListDb;
import it.geronimo.shoppinglist.repository.ThreadPerTaskExecutor;
import it.geronimo.shoppinglist.repository.dao.ShoppingListItemDao;
import it.geronimo.shoppinglist.repository.entities.ShoppingListItem;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Context context;
    private final List<ShoppingListItem> localDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemName;
        private final TextView itemDescription;
        private final CheckBox itemCheckbox;
        private final LinearLayout mainLayout;
        private final LinearLayout descriptionLayout;

        public ViewHolder(View view) {
            super(view);

            // Define click listener for the ViewHolder's View

            this.itemName = view.findViewById(R.id.item_name);
            this.itemDescription = view.findViewById(R.id.item_description);
            this.itemCheckbox = view.findViewById(R.id.item_checkbox);
            this.mainLayout = view.findViewById(R.id.mainLayout);
            this.descriptionLayout = view.findViewById(R.id.descriptionLayout);
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public ItemAdapter(Context context, List<ShoppingListItem> dataSet) {

        this.context = context;
        this.localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.item_row_item, viewGroup, false);

        return new ItemAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        ShoppingListItem item = localDataSet.get(position);

        viewHolder.itemName.setText(item.displayName);
        viewHolder.itemDescription.setText(item.description);
        viewHolder.itemCheckbox.setChecked(item.flagPurchased);

        viewHolder.itemCheckbox.setOnClickListener(view -> {
            ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();
            executor.execute(() -> {
                ShoppingListDb db = ShoppingListDb.getFileDatabase(context);
                ShoppingListItemDao dao = db.shoppingListItemModel();
                dao.update(item.id, ((CheckBox)view).isChecked());
            });
        });

        viewHolder.itemName.setOnClickListener(view -> {
            if (view == viewHolder.itemName) {
                if (viewHolder.descriptionLayout.getVisibility() == View.GONE) {
                    viewHolder.descriptionLayout.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.descriptionLayout.setVisibility(View.GONE);
                }
            }
//                Intent intent = new Intent(context, ItemEditActivity.class);
//                intent.putExtra(Extra.CRUD, Crud.UPDATE);
//                intent.putExtra(Extra.LIST_ID, item.shoppingListId);
//                intent.putExtra(Extra.ITEM_ID, item.id);
//                intent.putExtra(Extra.NAME, item.displayName);
//                intent.putExtra(Extra.DESCRIPTION, item.description);
//                context.startActivity(intent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
