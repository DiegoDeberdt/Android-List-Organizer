package lu.uni.student.shoppinglist.activities.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lu.uni.student.shoppinglist.R;
import lu.uni.student.shoppinglist.activities.Crud;
import lu.uni.student.shoppinglist.activities.Extra;
import lu.uni.student.shoppinglist.activities.Item.ItemActivity;
import lu.uni.student.shoppinglist.repository.ShoppingListDb;
import lu.uni.student.shoppinglist.repository.ThreadPerTaskExecutor;
import lu.uni.student.shoppinglist.repository.dao.ShoppingListDao;
import lu.uni.student.shoppinglist.repository.dao.ShoppingListItemDao;
import lu.uni.student.shoppinglist.repository.entities.ShoppingList;
import lu.uni.student.shoppinglist.repository.entities.ShoppingListWithCalculatedValues;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private final Context context;
    private final List<ShoppingListWithCalculatedValues> localDataSet;
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
            buttonViewOption = view.findViewById(R.id.textViewOptions);
            listImage = view.findViewById(R.id.list_image);
        }
    }

    public ListAdapter(Context context, List<ShoppingListWithCalculatedValues> dataSet, int[] imageId) {
        this.context = context;
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

        ShoppingListWithCalculatedValues item = localDataSet.get(position);

        String listName = item.displayName;
        viewHolder.listName.setText(listName);

        viewHolder.listImage.setImageResource(imageId[item.imageId]);

        String listSize = item.numberOfUnFlaggedItems + "/" + item.totalNumberOfItems;
        viewHolder.listSize.setText(listSize);

        viewHolder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ItemActivity.class);
            intent.putExtra(Extra.LIST_ID, item.id);
            intent.putExtra(Extra.NAME, item.displayName);
            context.startActivity(intent);
        });

        viewHolder.buttonViewOption.setOnClickListener(view -> {

            ShoppingListDb db = ShoppingListDb.getFileDatabase(context);
            ShoppingListDao daoList = db.shoppingListModel();
            ShoppingListItemDao daoItems = db.shoppingListItemModel();

            ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();

            PopupMenu popupMenu = new PopupMenu(context, viewHolder.buttonViewOption);
            popupMenu.inflate(R.menu.list_row_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.menu_item_rename) {
                    Intent intent = new Intent(context, ListEditActivity.class);
                    intent.putExtra(Extra.CRUD, Crud.UPDATE);
                    intent.putExtra(Extra.LIST_ID, item.id);
                    intent.putExtra(Extra.NAME, item.displayName);
                    intent.putExtra(Extra.IMAGE_INDEX, item.imageId);
                    context.startActivity(intent);
                    return true;
                }
                else if (itemId == R.id.menu_item_copy) {
                    ShoppingList newShoppingList = new ShoppingList();
                    newShoppingList.displayName = item.displayName + " - Copy";
                    executor.execute(() -> {
                        newShoppingList.id = daoList.insert(newShoppingList);
                        daoItems.copy(item.id, newShoppingList.id);
                    });

                    return true;
                }
                else if (itemId == R.id.menu_item_delete) {
                    executor.execute(() -> daoList.delete(item.id));
                    return true;
                }
                else {
                    return false;
                }
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
