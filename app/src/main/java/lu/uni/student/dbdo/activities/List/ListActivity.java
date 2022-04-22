package lu.uni.student.dbdo.activities.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import lu.uni.student.dbdo.R;
import lu.uni.student.dbdo.activities.Crud;
import lu.uni.student.dbdo.activities.Extra;
import lu.uni.student.dbdo.activities.Request;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Context context = this;

        RecyclerView shoppingListView = findViewById(R.id.topLevelLists);

        ListActivityViewModel viewModel = new ViewModelProvider(this).get(ListActivityViewModel.class);
        viewModel.getShoppingListList().observe(this, allShoppingLists -> {

            int[] iconResourceIds = ListIcons.getResourceIds(getResources(), getPackageName());
            final ListAdapter adapter = new ListAdapter(this, allShoppingLists, iconResourceIds);
            shoppingListView.setAdapter(adapter);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            shoppingListView.setLayoutManager(layoutManager);
        });
    }

    public void addButtonClick(View view) {
        Intent intent = new Intent(this, ListEditActivity.class);
        intent.putExtra(Extra.CRUD, Crud.CREATE);
        intent.putExtra(Extra.PARENT_ID, (Bundle)null);
        startActivityForResult(intent, Request.CREATE_LIST_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode != Request.REQUEST_RESPONSE) return;

        View contextView = findViewById(R.id.list_fab);
        if (requestCode == Request.CREATE_LIST_REQUEST) {
            Snackbar.make(contextView, R.string.snackbar_new_list_created, Snackbar.LENGTH_SHORT).show();
        }
        else if (requestCode == Request.UPDATE_REQUEST) {
            Snackbar.make(contextView, R.string.snackbar_list_updated, Snackbar.LENGTH_SHORT).show();
        }
    }
}