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

    private Crud crudAction;
    private long shoppingListId;
    private long shoppingListItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        processIntent();
        initTitleAndLabel();
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

                // Query the database

                ShoppingListDb db = ShoppingListDb.getFileDatabase(getApplication());
                ShoppingListItemDao dao = db.shoppingListItemModel();
                ShoppingListItem item = dao.getItemById(this.shoppingListItemId);

                handler.post(() -> {

                    // Update the UI

                    TextInputEditText nameInput = findViewById(R.id.itemEditName);
                    nameInput.setText(item.displayName);

                    TextInputEditText descriptionInput = findViewById(R.id.itemEditDescription);
                    descriptionInput.setText(item.description);
                });
            });
        }
    }

    /*
     * Update the activity title.
     * Also set the label that is shown on the button.
     */
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

    /*
     * Click handler for the button.
     */
    public void buttonClick(View view) {

        TextInputEditText nameTextView = findViewById(R.id.itemEditName);
        String displayName = nameTextView.getText().toString();
        if (displayName.trim().length() == 0) displayName = getString(R.string.item_new);

        TextInputEditText descriptionTextView = findViewById(R.id.itemEditDescription);
        final String description = descriptionTextView.getText().toString();

        ShoppingListDb db = ShoppingListDb.getFileDatabase(this);
        ShoppingListItemDao dao = db.shoppingListItemModel();

        if (this.crudAction == Crud.CREATE) {

            createNewListItem(displayName, description, dao);
        }
        else if (this.crudAction == Crud.UPDATE) {

            updateListItem(displayName, description, dao);
        }

        finish();
    }

    private void updateListItem(final String displayName, final String description, final ShoppingListItemDao dao) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> dao.update(this.shoppingListItemId, displayName, description));
    }

    private void createNewListItem(final String displayName, final String description, final ShoppingListItemDao dao) {
        ShoppingListItem shoppingListItem = new ShoppingListItem();
        shoppingListItem.displayName = displayName;
        shoppingListItem.description = description;
        shoppingListItem.shoppingListId = this.shoppingListId;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> dao.insert(shoppingListItem));
    }
}