package lu.uni.student.shoppinglist.activities.Item;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lu.uni.student.shoppinglist.R;
import lu.uni.student.shoppinglist.activities.Crud;
import lu.uni.student.shoppinglist.activities.Extra;
import lu.uni.student.shoppinglist.activities.Request;
import lu.uni.student.shoppinglist.repository.ShoppingListDb;
import lu.uni.student.shoppinglist.repository.dao.ShoppingListItemDao;
import lu.uni.student.shoppinglist.repository.entities.ShoppingListItem;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Activity activity;
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
            this.buttonViewOption = view.findViewById(R.id.list_row_options);
        }
    }

    public ItemAdapter(Activity activity, List<ShoppingListItem> dataSet) {
        this.activity = activity;
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

        ShoppingListItem item = localDataSet.get(position);

        viewHolder.itemName.setText(item.displayName);
        viewHolder.itemDescription.setText(item.description);
        viewHolder.itemCheckbox.setChecked(item.flagPurchased);

        viewHolder.itemCheckbox.setOnClickListener(view -> {

            // Handle the user clicking on the checkbox

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                ShoppingListDb db = ShoppingListDb.getFileDatabase(this.activity);
                ShoppingListItemDao dao = db.shoppingListItemModel();
                dao.update(item.id, ((CheckBox)view).isChecked());
            });
        });

        viewHolder.itemName.setOnClickListener(view -> {

            // Expand and collapse the item "description"

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

                PopupMenu popup = new PopupMenu(activity, viewHolder.buttonViewOption);
                popup.inflate(R.menu.item_row_menu);
                popup.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_item_edit:
                            onMenuItemEditClicked(item);
                            return true;
                        case R.id.menu_item_delete:
                            onMenuItemDeleteClicked(item.id);
                            return true;
                        default:
                            return false;
                    }
                });

                popup.show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    private void onMenuItemDeleteClicked(long id) {
        ShoppingListDb db = ShoppingListDb.getFileDatabase(activity);
        ShoppingListItemDao dao = db.shoppingListItemModel();

        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        mainThreadHandler.post(() -> {
            View contextView = this.activity.findViewById(R.id.item_fab);
            Snackbar undoDelete = Snackbar.make(contextView, R.string.snackbar_item_deleted, Snackbar.LENGTH_LONG);

            undoDelete.setAction(R.string.snackbar_list_delete_undo, view -> {
                // The user clicked the UNDO button so restore the list item
                ExecutorService undoExecutor = Executors.newSingleThreadExecutor();
                undoExecutor.execute(()-> dao.restore(id));
            });

            undoDelete.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        // Permanently delete the archived list item
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> dao.delete(id));
                    }
                }
            });
            undoDelete.show();
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> dao.archive(id));
    }

    private void onMenuItemEditClicked(ShoppingListItem item) {
        Intent intent = new Intent(activity, ItemEditActivity.class);
        intent.putExtra(Extra.CRUD, Crud.UPDATE);
        intent.putExtra(Extra.LIST_ID, item.shoppingListId);
        intent.putExtra(Extra.ITEM_ID, item.id);

        activity.startActivityForResult(intent, Request.UPDATE_REQUEST);
    }
}
