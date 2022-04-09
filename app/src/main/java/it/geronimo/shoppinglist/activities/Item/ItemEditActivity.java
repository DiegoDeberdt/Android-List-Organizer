package it.geronimo.shoppinglist.activities.Item;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;

import it.geronimo.shoppinglist.ExtrasNotFoundException;
import it.geronimo.shoppinglist.R;
import it.geronimo.shoppinglist.activities.Crud;
import it.geronimo.shoppinglist.activities.Extra;
import it.geronimo.shoppinglist.repository.ShoppingListDb;
import it.geronimo.shoppinglist.repository.ThreadPerTaskExecutor;
import it.geronimo.shoppinglist.repository.dao.ShoppingListItemDao;
import it.geronimo.shoppinglist.repository.entities.ShoppingListItem;

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

            if (!intent.hasExtra(Extra.NAME)) throw new ExtrasNotFoundException(Extra.NAME);
            String name = intent.getStringExtra(Extra.NAME);
            TextInputEditText nameInput = findViewById(R.id.itemEditName);
            nameInput.setText(name);

            if (!intent.hasExtra(Extra.DESCRIPTION)) throw new ExtrasNotFoundException(Extra.DESCRIPTION);
            String description = intent.getStringExtra(Extra.DESCRIPTION);
            TextInputEditText descriptionInput = findViewById(R.id.itemEditDescription);
            descriptionInput.setText(description);
        }
    }

    private void initTitleAndLabel() {
        Button button = findViewById(R.id.itemEditButton);
        switch(this.crudAction) {
            case CREATE:
                button.setText(R.string.add_item);
                setTitle(R.string.add_list_item);
                break;
            case UPDATE:
                button.setText(R.string.update_item);
                setTitle(R.string.edit_list_item);
                break;
        }
    }

    public void buttonClick(View view) {

        TextInputEditText nameTextView = findViewById(R.id.itemEditName);
        String _displayName = nameTextView.getText().toString();
        if (_displayName.trim().length() == 0) _displayName = getString(R.string.new_item);
        final String displayName = _displayName;

        TextInputEditText descriptionTextView = findViewById(R.id.itemEditDescription);
        final String description = descriptionTextView.getText().toString();

        ShoppingListDb db = ShoppingListDb.getFileDatabase(this);
        ShoppingListItemDao dao = db.shoppingListItemModel();

        ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();
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

    public void photoButtonClick(View view) {
        dispatchTakePictureIntent();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            // TODO replace startActivityForResult
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView imageView = findViewById(R.id.imageView);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}