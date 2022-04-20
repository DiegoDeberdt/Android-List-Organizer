package lu.uni.student.shoppinglist.activities.Item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lu.uni.student.shoppinglist.ExtrasNotFoundException;
import lu.uni.student.shoppinglist.R;
import lu.uni.student.shoppinglist.activities.Crud;
import lu.uni.student.shoppinglist.activities.Extra;
import lu.uni.student.shoppinglist.activities.List.ListAdapter;
import lu.uni.student.shoppinglist.activities.List.ListEditActivity;
import lu.uni.student.shoppinglist.activities.List.ListIcons;
import lu.uni.student.shoppinglist.activities.Request;
import lu.uni.student.shoppinglist.repository.ShoppingListDb;
import lu.uni.student.shoppinglist.repository.dao.ShoppingListItemDao;

public class ItemActivity extends AppCompatActivity {

    private final String BUNDLE_ID = "Shopping_List_Id";

    private long shoppingListId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Context context = this;

        Intent intent = getIntent();

        if (savedInstanceState != null) this.shoppingListId = savedInstanceState.getLong(BUNDLE_ID);
        else this.shoppingListId = intent.getLongExtra(Extra.LIST_ID, -1);
        if (this.shoppingListId == -1) throw new ExtrasNotFoundException(Extra.LIST_ID);

        setTitle(intent.getStringExtra(Extra.NAME));

        ItemActivityViewModel viewModel = new ViewModelProvider(this).get(ItemActivityViewModel.class);
        viewModel.getShoppingListItemList(shoppingListId).observe(this, allShoppingListItems -> {
            RecyclerView itemListView = findViewById(R.id.itemsList);
            if (itemListView != null) {
                final ItemAdapter adapter = new ItemAdapter(this, allShoppingListItems);
                itemListView.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                itemListView.setLayoutManager(layoutManager);
            }
        });

        viewModel.getShoppingLists(shoppingListId).observe(this, shoppingLists -> {
            RecyclerView listView = findViewById(R.id.subLists);
            if (listView != null) {
                int[] iconResourceIds = ListIcons.getResourceIds(getResources(), getPackageName());
                final ListAdapter adapter = new ListAdapter(this, shoppingLists, iconResourceIds);
                listView.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                listView.setLayoutManager(layoutManager);
            }
        });
    }

    public void addButtonClick(View view) {
        Intent intent = new Intent(this, ItemEditActivity.class);
        intent.putExtra(Extra.CRUD, Crud.CREATE);
        intent.putExtra(Extra.LIST_ID, this.shoppingListId);
        startActivityForResult(intent, Request.CREATE_ITEM_REQUEST);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_ID, this.shoppingListId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.option_create_list:
                createNewList();
                return true;
            case R.id.option_reset_finished_items:
                resetAllItemsInList();
                return true;
            default:
                throw new RuntimeException("Unhandled option");
        }
    }

    private void resetAllItemsInList() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ShoppingListDb db = ShoppingListDb.getFileDatabase(this);
            ShoppingListItemDao daoItems = db.shoppingListItemModel();
            daoItems.resetAllItemsInList(this.shoppingListId);
        });
    }

    private void createNewList() {
        Bundle bundle = new Bundle();
        bundle.putLong(Extra.PARENT_ID, this.shoppingListId);

        Intent intent = new Intent(this, ListEditActivity.class);
        intent.putExtra(Extra.CRUD, Crud.CREATE);
        intent.putExtra(Extra.PARENT_ID, bundle);

        startActivityForResult(intent, Request.CREATE_LIST_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode != Request.REQUEST_RESPONSE) return;

        View contextView = findViewById(R.id.item_fab);
        if (requestCode == Request.CREATE_ITEM_REQUEST) {
            Snackbar.make(contextView, R.string.snackbar_new_item_created, Snackbar.LENGTH_SHORT).show();
        }
        else if (requestCode == Request.CREATE_LIST_REQUEST) {
            Snackbar.make(contextView, R.string.snackbar_new_list_created, Snackbar.LENGTH_SHORT).show();
        }
        else if (requestCode == Request.UPDATE_REQUEST) {
            Snackbar.make(contextView, R.string.snackbar_item_updated, Snackbar.LENGTH_SHORT).show();
        }
    }
}