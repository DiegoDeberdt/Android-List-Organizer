package it.geronimo.shoppinglist.activities.Item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import it.geronimo.shoppinglist.ExtrasNotFoundException;
import it.geronimo.shoppinglist.R;
import it.geronimo.shoppinglist.activities.Crud;
import it.geronimo.shoppinglist.activities.Extra;

public class ItemActivity extends AppCompatActivity {

    private final String BUNDLE_ID = "Shopping_List_Id";

    private long shoppingListId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Context context = this;

        Intent intent = getIntent();

        if (savedInstanceState != null) {
            this.shoppingListId = savedInstanceState.getLong(BUNDLE_ID);
        } else {
            this.shoppingListId = intent.getLongExtra(Extra.LIST_ID, -1);
        }
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
    }

    public void addButtonClick(View view) {
        Intent intent = new Intent(this, ItemEditActivity.class);
        intent.putExtra(Extra.CRUD, Crud.CREATE);
        intent.putExtra(Extra.LIST_ID, this.shoppingListId);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_ID, this.shoppingListId);
    }
}