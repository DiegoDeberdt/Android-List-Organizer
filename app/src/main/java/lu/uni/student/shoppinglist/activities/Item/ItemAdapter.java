package lu.uni.student.shoppinglist.activities.Item;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lu.uni.student.shoppinglist.R;
import lu.uni.student.shoppinglist.activities.Crud;
import lu.uni.student.shoppinglist.activities.Extra;
import lu.uni.student.shoppinglist.repository.ShoppingListDb;
import lu.uni.student.shoppinglist.repository.ThreadPerTaskExecutor;
import lu.uni.student.shoppinglist.repository.dao.ShoppingListItemDao;
import lu.uni.student.shoppinglist.repository.entities.ShoppingListItem;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Context context;
    private final List<ShoppingListItem> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemName;
        private final TextView itemDescription;
        private final CheckBox itemCheckbox;
        private final LinearLayout descriptionLayout;
        private final TextView buttonViewOption;

        public ViewHolder(View view) {
            super(view);

            this.itemName = view.findViewById(R.id.item_name);
            this.itemDescription = view.findViewById(R.id.item_description);
            this.itemCheckbox = view.findViewById(R.id.item_checkbox);
            this.descriptionLayout = view.findViewById(R.id.descriptionLayout);
            this.buttonViewOption = view.findViewById(R.id.textViewOptions);
        }
    }

    public ItemAdapter(Context context, List<ShoppingListItem> dataSet) {
        this.context = context;
        this.localDataSet = dataSet;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.item_row_item, viewGroup, false);

        return new ItemAdapter.ViewHolder(view);
    }

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
        });

        viewHolder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, viewHolder.buttonViewOption);

                //inflating menu from xml resource
                popup.inflate(R.menu.item_row_menu);

                //adding click listener
                popup.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_item_edit:
                            Intent intent = new Intent(context, ItemEditActivity.class);
                            intent.putExtra(Extra.CRUD, Crud.UPDATE);
                            intent.putExtra(Extra.LIST_ID, item.shoppingListId);
                            intent.putExtra(Extra.ITEM_ID, item.id);
                            intent.putExtra(Extra.NAME, item.displayName);
                            intent.putExtra(Extra.DESCRIPTION, item.description);
                            context.startActivity(intent);
                            return true;
                        case R.id.menu_item_delete:
                            ShoppingListDb db = ShoppingListDb.getFileDatabase(context);
                            ShoppingListItemDao dao = db.shoppingListItemModel();
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
