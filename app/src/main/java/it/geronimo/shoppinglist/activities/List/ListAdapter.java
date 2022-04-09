package it.geronimo.shoppinglist.activities.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.geronimo.shoppinglist.R;
import it.geronimo.shoppinglist.activities.Crud;
import it.geronimo.shoppinglist.activities.Extra;
import it.geronimo.shoppinglist.activities.Item.ItemActivity;
import it.geronimo.shoppinglist.repository.ShoppingListDb;
import it.geronimo.shoppinglist.repository.ThreadPerTaskExecutor;
import it.geronimo.shoppinglist.repository.dao.ShoppingListDao;
import it.geronimo.shoppinglist.repository.entities.ShoppingListWithCalculatedValues;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private final Context context;
    private final List<ShoppingListWithCalculatedValues> localDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView listName;
        private final TextView listSize;
        private final LinearLayout mainLayout;
        private final TextView buttonViewOption;

        public ViewHolder(View view) {
            super(view);

            listName = view.findViewById(R.id.list_name);
            listSize = view.findViewById(R.id.list_size);
            mainLayout = view.findViewById(R.id.mainLayout);
            buttonViewOption = view.findViewById(R.id.textViewOptions);
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public ListAdapter(Context context, List<ShoppingListWithCalculatedValues> dataSet) {
        this.context = context;
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.list_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        ShoppingListWithCalculatedValues item = localDataSet.get(position);

        String listName = item.displayName;
        viewHolder.listName.setText(listName);

        String listSize = item.numberOfUnFlaggedItems + "/" + item.totalNumberOfItems;
        viewHolder.listSize.setText(listSize);

        // Define click listener for the ViewHolder's View

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ItemActivity.class);
                intent.putExtra(Extra.LIST_ID, item.id);
                intent.putExtra(Extra.NAME, item.displayName);
                context.startActivity(intent);
            }
        });

        viewHolder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, viewHolder.buttonViewOption);

                //inflating menu from xml resource
                popup.inflate(R.menu.list_row_menu);

                //adding click listener
                popup.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_item_edit:
                            Intent intent = new Intent(context, ListEditActivity.class);
                            intent.putExtra(Extra.CRUD, Crud.UPDATE);
                            intent.putExtra(Extra.LIST_ID, item.id);
                            intent.putExtra(Extra.NAME, item.displayName);
                            context.startActivity(intent);
                            return true;
                        case R.id.menu_item_delete:
                            ShoppingListDb db = ShoppingListDb.getFileDatabase(context);
                            ShoppingListDao dao = db.shoppingListModel();
                            ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();
                            executor.execute(() -> dao.delete(item.id));
                            return true;
                        default:
                            return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
