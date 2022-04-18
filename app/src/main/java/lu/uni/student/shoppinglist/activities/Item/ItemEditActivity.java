package lu.uni.student.shoppinglist.activities.Item;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lu.uni.student.shoppinglist.ExtrasNotFoundException;
import lu.uni.student.shoppinglist.R;
import lu.uni.student.shoppinglist.activities.Crud;
import lu.uni.student.shoppinglist.activities.Extra;
import lu.uni.student.shoppinglist.repository.ShoppingListDb;
import lu.uni.student.shoppinglist.repository.dao.ShoppingListItemDao;
import lu.uni.student.shoppinglist.repository.entities.ShoppingListItem;

public class ItemEditActivity extends AppCompatActivity {

    private final String BUNDLE_CRUD = "Crud";
    private final String BUNDLE_LIST_ID = "shoppingListId";
    private final String BUNDLE_ITEM_ID = "shoppingListItemId";

    private Crud crudAction;
    private long shoppingListId;
    private long shoppingListItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        // TODO: check if I need to save instance data. Processing the intent is probably enough.
//        if (savedInstanceState != null) processInstanceState(savedInstanceState);
//        else processIntent();

        processIntent();
        initTitleAndLabel();
    }

    private void processInstanceState(Bundle savedInstanceState) {
        this.crudAction = (Crud)savedInstanceState.getSerializable(BUNDLE_CRUD);
        this.shoppingListId = savedInstanceState.getLong(BUNDLE_LIST_ID);
        this.shoppingListItemId = savedInstanceState.getLong(BUNDLE_ITEM_ID);
    }

    private void processIntent() {
        Intent intent = getIntent();

        if (!intent.hasExtra(Extra.CRUD)) throw new ExtrasNotFoundException(Extra.CRUD);
        this.crudAction = (Crud)intent.getSerializableExtra(Extra.CRUD);

        if (!intent.hasExtra(Extra.LIST_ID)) throw new ExtrasNotFoundException(Extra.LIST_ID);
        this.shoppingListId = intent.getLongExtra(Extra.LIST_ID, -1);

        if (this.crudAction == Crud.UPDATE) {
            if (!intent.hasExtra(Extra.ITEM_ID)) throw new ExtrasNotFoundException(Extra.ITEM_ID);
            this.shoppingListItemId = intent.getLongExtra(Extra.ITEM_ID, -1);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                ShoppingListDb db = ShoppingListDb.getFileDatabase(getApplication());
                ShoppingListItemDao dao = db.shoppingListItemModel();
                ShoppingListItem item = dao.getItemById(this.shoppingListItemId);

                handler.post(() -> {
                    TextInputEditText nameInput = findViewById(R.id.itemEditName);
                    nameInput.setText(item.displayName);

                    TextInputEditText descriptionInput = findViewById(R.id.itemEditDescription);
                    descriptionInput.setText(item.description);
                });
            });
        }
    }

    private void initTitleAndLabel() {
        Button button = findViewById(R.id.itemEditButton);
        switch(this.crudAction) {
            case CREATE:
                button.setText(R.string.item_add_button);
                setTitle(R.string.item_add_action_title);
                break;
            case UPDATE:
                button.setText(R.string.save_changes_button);
                setTitle(R.string.item_edit_action_title);
                break;
        }
    }

    public void buttonClick(View view) {

        TextInputEditText nameTextView = findViewById(R.id.itemEditName);
        String _displayName = nameTextView.getText().toString();
        if (_displayName.trim().length() == 0) _displayName = getString(R.string.item_new);
        final String displayName = _displayName;

        TextInputEditText descriptionTextView = findViewById(R.id.itemEditDescription);
        final String description = descriptionTextView.getText().toString();

        ShoppingListDb db = ShoppingListDb.getFileDatabase(this);
        ShoppingListItemDao dao = db.shoppingListItemModel();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        if (this.crudAction == Crud.CREATE) {
            ShoppingListItem shoppingListItem = new ShoppingListItem();
            shoppingListItem.displayName = displayName;
            shoppingListItem.description = description;
            shoppingListItem.shoppingListId = this.shoppingListId;
            executor.execute(() -> dao.insert(shoppingListItem));
        }
        else if (this.crudAction == Crud.UPDATE) {
            executor.execute(() -> dao.update(shoppingListItemId, displayName, description));
        }

        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_LIST_ID, this.shoppingListId);
        outState.putLong(BUNDLE_ITEM_ID, this.shoppingListItemId);
        outState.putSerializable(BUNDLE_CRUD, this.crudAction);
    }
}