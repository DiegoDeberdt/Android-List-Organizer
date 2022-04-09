package it.geronimo.shoppinglist.activities.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.geronimo.shoppinglist.ExtrasNotFoundException;
import it.geronimo.shoppinglist.R;
import it.geronimo.shoppinglist.activities.Crud;
import it.geronimo.shoppinglist.activities.Extra;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Context context = this;

        RecyclerView shoppingListView = findViewById(R.id.shoppingLists);

        ListActivityViewModel viewModel = new ViewModelProvider(this).get(ListActivityViewModel.class);
        viewModel.getShoppingListList().observe(this, allShoppingLists -> {

            final ListAdapter adapter = new ListAdapter(context, allShoppingLists);
            shoppingListView.setAdapter(adapter);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            shoppingListView.setLayoutManager(layoutManager);
        });
    }

    public void addButtonClick(View view) {
        Intent intent = new Intent(this, ListEditActivity.class);
        intent.putExtra(Extra.CRUD, Crud.CREATE);
        startActivity(intent);
    }
}