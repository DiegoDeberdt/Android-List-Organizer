package lu.uni.student.dbdo.activities.Item;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lu.uni.student.dbdo.R;
import lu.uni.student.dbdo.activities.Crud;
import lu.uni.student.dbdo.activities.Extra;
import lu.uni.student.dbdo.activities.Request;
import lu.uni.student.dbdo.repository.ListDb;
import lu.uni.student.dbdo.repository.dao.ListItemDao;
import lu.uni.student.dbdo.repository.entities.ListItemEntity;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Activity activity;
    private final List<ListItemEntity> localDataSet;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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

    public ItemAdapter(Activity activity, List<ListItemEntity> dataSet) {
        this.activity = activity;
        this.localDataSet = dataSet;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.item_row_item, viewGroup, false);

        return new ItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder viewHolder, final int position) {

        ListItemEntity item = localDataSet.get(position);

        viewHolder.itemName.setText(item.displayName);
        viewHolder.itemDescription.setText(item.description);
        viewHolder.itemCheckbox.setChecked(item.purchasedFlag);

        viewHolder.itemCheckbox.setOnClickListener(view -> {

            // Handle the user clicking on the checkbox
            executor.execute(() -> {
                ListDb db = ListDb.getFileDatabase(this.activity);
                ListItemDao dao = db.shoppingListItemModel();
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

        viewHolder.buttonViewOption.setOnClickListener(view -> {

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
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    private void onMenuItemDeleteClicked(long id) {

        ListDb db = ListDb.getFileDatabase(activity);
        ListItemDao dao = db.shoppingListItemModel();

        View contextView = this.activity.findViewById(R.id.item_fab);
        Snackbar undoDeleteSnackbar = Snackbar.make(contextView, R.string.snackbar_item_deleted, Snackbar.LENGTH_LONG);

        undoDeleteSnackbar.setAction(R.string.snackbar_list_delete_undo, view -> {
            // The user clicked the UNDO button so restore the list item
            executor.execute(()-> dao.restore(id));
        });

        undoDeleteSnackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_MANUAL) {
                    // Permanently delete the archived list item
                    executor.execute(() -> dao.delete(id));
                }
            }
        });

        executor.execute(() -> {
            dao.archive(id);
            undoDeleteSnackbar.show();
        });
    }

    private void onMenuItemEditClicked(ListItemEntity item) {
        Intent intent = new Intent(activity, ItemEditActivity.class);
        intent.putExtra(Extra.CRUD, Crud.UPDATE);
        intent.putExtra(Extra.LIST_ID, item.listId);
        intent.putExtra(Extra.ITEM_ID, item.id);

        activity.startActivityForResult(intent, Request.UPDATE_REQUEST);
    }
}
