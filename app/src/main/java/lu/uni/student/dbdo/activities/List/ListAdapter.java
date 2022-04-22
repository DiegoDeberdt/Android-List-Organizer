package lu.uni.student.dbdo.activities.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lu.uni.student.dbdo.R;
import lu.uni.student.dbdo.activities.Crud;
import lu.uni.student.dbdo.activities.Extra;
import lu.uni.student.dbdo.activities.Item.ItemActivity;
import lu.uni.student.dbdo.activities.Request;
import lu.uni.student.dbdo.repository.ListDb;
import lu.uni.student.dbdo.repository.dao.ListDao;
import lu.uni.student.dbdo.repository.dao.ListItemDao;
import lu.uni.student.dbdo.repository.entities.ListEntity;
import lu.uni.student.dbdo.repository.entities.ListEntityWithCalculatedValues;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private final Activity activity;
    private final List<ListEntityWithCalculatedValues> localDataSet;
    private final int[] imageId;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView listName;
        private final TextView listSize;
        private final LinearLayout mainLayout;
        private final TextView buttonViewOption;
        private final ImageView listImage;

        public ViewHolder(View view) {
            super(view);

            listName = view.findViewById(R.id.list_name);
            listSize = view.findViewById(R.id.list_size);
            mainLayout = view.findViewById(R.id.mainLayout);
            buttonViewOption = view.findViewById(R.id.list_row_options);
            listImage = view.findViewById(R.id.list_image);
        }
    }

    public ListAdapter(Activity activity, List<ListEntityWithCalculatedValues> dataSet, int[] imageId) {
        this.activity = activity;
        this.localDataSet = dataSet;
        this.imageId = imageId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.list_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        ListEntityWithCalculatedValues item = localDataSet.get(position);

        String listName = item.displayName;
        viewHolder.listName.setText(listName);

        viewHolder.listImage.setImageResource(imageId[item.iconIndex]);

        String listSize = item.numberOfUnFlaggedItems + "/" + item.totalNumberOfItems;
        viewHolder.listSize.setText(listSize);

        viewHolder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this.activity, ItemActivity.class);
            intent.putExtra(Extra.LIST_ID, item.id);
            intent.putExtra(Extra.NAME, item.displayName);
            this.activity.startActivity(intent);
        });

        viewHolder.buttonViewOption.setOnClickListener(view -> {

            ListDb db = ListDb.getFileDatabase(this.activity);
            ListDao daoList = db.shoppingListModel();
            ListItemDao daoItems = db.shoppingListItemModel();

            PopupMenu popupMenu = new PopupMenu(this.activity, viewHolder.buttonViewOption);
            popupMenu.inflate(R.menu.list_row_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int itemId = menuItem.getItemId();

                boolean returnValue = true;

                if (itemId == R.id.menu_item_rename) editList(item);
                else if (itemId == R.id.menu_item_copy) copyList(daoList, daoItems, item, item.parentId, true);
                else if (itemId == R.id.menu_item_delete) deleteList(daoList, item.id);
                else returnValue = false;

                return returnValue;
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    private void editList(ListEntity item) {
        Intent intent = new Intent(this.activity, ListEditActivity.class);
        intent.putExtra(Extra.CRUD, Crud.UPDATE);
        intent.putExtra(Extra.LIST_ID, item.id);
        intent.putExtra(Extra.NAME, item.displayName);
        intent.putExtra(Extra.IMAGE_INDEX, item.iconIndex);
        intent.putExtra(Extra.PARENT_ID, item.parentId);
        if (item.parentId != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(Extra.PARENT_ID, item.parentId);
            intent.putExtra(Extra.PARENT_ID, bundle);
        }

        this.activity.startActivityForResult(intent, Request.UPDATE_REQUEST);
    }

    private void deleteList(ListDao daoList, long id) {

        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        mainThreadHandler.post(() -> {

            View contextView = this.activity.findViewById(R.id.list_row_options);
            Snackbar undoDelete = Snackbar.make(contextView, R.string.snackbar_list_deleted, Snackbar.LENGTH_LONG);

            undoDelete.setAction(R.string.snackbar_list_delete_undo, view -> {
                // The user clicked the UNDO button so restore the list
                ExecutorService undoExecutor = Executors.newSingleThreadExecutor();
                undoExecutor.execute(()-> daoList.restore(id));
            });

            undoDelete.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_MANUAL) {
                    // Permanently delete the list
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> deleteListRecursively(daoList, id));
                }
                }
            });

            undoDelete.show();
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> daoList.archive(id));
    }

    private void deleteListRecursively(ListDao daoList, long id) {
        List<ListEntity> childLists = daoList.getListsToCopy(id);
        if (childLists.size() != 0) {
            for (ListEntity listItem : childLists) {
                deleteListRecursively(daoList, listItem.id);
                daoList.delete(listItem.id);
            }
        }
        daoList.delete(id);
    }

    private void copyList(ListDao daoList, ListItemDao daoItems, ListEntity item, Long parentId, boolean modifyListName) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

        executor.execute(() -> {

            copyListRecursively(daoList, daoItems, item, parentId, modifyListName);

            mainThreadHandler.post(() -> {

                View contextView = this.activity.findViewById(R.id.list_row_options);
                Snackbar.make(contextView, R.string.snackbar_list_copied, Snackbar.LENGTH_SHORT).show();
            });
        });
    }

    private void copyListRecursively(ListDao daoList, ListItemDao daoItems, ListEntity item, Long parentId, boolean modifyListName) {
        ListEntity newListEntity = new ListEntity();
        newListEntity.iconIndex = item.iconIndex;
        newListEntity.parentId = parentId;
        newListEntity.displayName = item.displayName;
        if (modifyListName) newListEntity.displayName += " - " + this.activity.getResources().getString(R.string.list_copy);

        newListEntity.id = daoList.insert(newListEntity);
        daoItems.copy(item.id, newListEntity.id);

        List<ListEntity> childLists = daoList.getListsToCopy(item.id);
        for(ListEntity listItem : childLists) {
            copyListRecursively(daoList, daoItems, listItem, newListEntity.id, false);
        }
    }
}
