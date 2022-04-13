package lu.uni.student.shoppinglist.activities.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import lu.uni.student.shoppinglist.R;
import lu.uni.student.shoppinglist.activities.Crud;
import lu.uni.student.shoppinglist.activities.Extra;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Context context = this;

        RecyclerView shoppingListView = findViewById(R.id.shoppingLists);

        ListActivityViewModel viewModel = new ViewModelProvider(this).get(ListActivityViewModel.class);
        viewModel.getShoppingListList().observe(this, allShoppingLists -> {

            int[] imageId = ListIcons.getResourceIds(getResources(), getPackageName());
            final ListAdapter adapter = new ListAdapter(context, allShoppingLists, imageId);
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