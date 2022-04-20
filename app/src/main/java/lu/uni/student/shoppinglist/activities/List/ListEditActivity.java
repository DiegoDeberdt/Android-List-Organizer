package lu.uni.student.shoppinglist.activities.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lu.uni.student.shoppinglist.ExtrasNotFoundException;
import lu.uni.student.shoppinglist.R;
import lu.uni.student.shoppinglist.activities.Crud;
import lu.uni.student.shoppinglist.activities.Extra;
import lu.uni.student.shoppinglist.activities.Request;
import lu.uni.student.shoppinglist.repository.ShoppingListDb;
import lu.uni.student.shoppinglist.repository.dao.ShoppingListDao;
import lu.uni.student.shoppinglist.repository.entities.ShoppingList;

public class ListEditActivity extends AppCompatActivity {

    private Crud crudAction;
    private long shoppingListId;
    private Long parentShoppingListId;
    private ListEditAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_edit);

        Intent intent = getIntent();

        if (!intent.hasExtra(Extra.CRUD)) throw new ExtrasNotFoundException(Extra.CRUD);
        this.crudAction = (Crud)intent.getSerializableExtra(Extra.CRUD);

        this.parentShoppingListId = null;
        if (intent.hasExtra(Extra.PARENT_ID)) {
            Bundle bundle = intent.getBundleExtra(Extra.PARENT_ID);
            if (bundle != null) {
                this.parentShoppingListId = bundle.getLong(Extra.PARENT_ID);
            }
        }

        initTitleAndLabel(intent);

        int[] iconResourceIds = ListIcons.getResourceIds(getResources(), getPackageName());

        if (this.crudAction == Crud.CREATE) {
            this.listAdapter = new ListEditAdapter(iconResourceIds);
        }
        else if (this.crudAction == Crud.UPDATE) {
            if (!intent.hasExtra(Extra.IMAGE_INDEX)) throw new ExtrasNotFoundException(Extra.IMAGE_INDEX);
            int imageIndex = intent.getIntExtra(Extra.IMAGE_INDEX, 0);
            this.listAdapter = new ListEditAdapter(iconResourceIds, imageIndex);
        }

        RecyclerView imageList = findViewById(R.id.imageList);
        imageList.setAdapter(listAdapter);

        GridLayoutManager layoutManager = null;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 5);
        }
        else{
            layoutManager = new GridLayoutManager(this, 11);
        }
        imageList.setLayoutManager(layoutManager);
    }

    private void initTitleAndLabel(Intent intent) {
        Button button = findViewById(R.id.listEditButton);
        TextInputEditText listEditName = findViewById(R.id.listEditName);

        switch(this.crudAction) {
            case CREATE:
                button.setText(R.string.list_create_button);
                setTitle(R.string.list_create_action_title);
                break;
            case UPDATE:
                button.setText(R.string.save_changes_button);
                setTitle(R.string.list_edit_action_title);

                if (!intent.hasExtra(Extra.LIST_ID)) throw new ExtrasNotFoundException(Extra.LIST_ID);
                this.shoppingListId = intent.getLongExtra(Extra.LIST_ID, -1);
                listEditName.setText(intent.getStringExtra(Extra.NAME));
                break;
        }
    }

    public void buttonClick(View view) {
        TextInputEditText textView = findViewById(R.id.listEditName);
        String displayName = textView.getText().toString();
        if (displayName.trim().length() == 0) displayName = getString(R.string.list_create_action_title);

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.displayName = displayName;
        shoppingList.id = this.shoppingListId;
        shoppingList.parentId = this.parentShoppingListId;
        shoppingList.iconIndex = listAdapter.getIndexOfSelectedIcon();

        ShoppingListDb db = ShoppingListDb.getFileDatabase(getApplication());
        ShoppingListDao dao = db.shoppingListModel();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        if (this.crudAction == Crud.CREATE) {
            executor.execute(() -> dao.insert(shoppingList));
        }
        else if (this.crudAction == Crud.UPDATE) {
            executor.execute(() -> dao.update(shoppingList));
        }

        setResult(Request.REQUEST_RESPONSE);
        finish();
    }
}