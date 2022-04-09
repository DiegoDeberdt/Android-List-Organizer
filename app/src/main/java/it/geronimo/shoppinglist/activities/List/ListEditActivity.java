package it.geronimo.shoppinglist.activities.List;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import it.geronimo.shoppinglist.ExtrasNotFoundException;
import it.geronimo.shoppinglist.R;
import it.geronimo.shoppinglist.activities.Crud;
import it.geronimo.shoppinglist.activities.Extra;
import it.geronimo.shoppinglist.repository.ShoppingListDb;
import it.geronimo.shoppinglist.repository.ThreadPerTaskExecutor;
import it.geronimo.shoppinglist.repository.dao.ShoppingListDao;
import it.geronimo.shoppinglist.repository.entities.ShoppingList;

public class ListEditActivity extends AppCompatActivity {

    // TODO: use a ViewModel

    private Crud crudAction;
    private long shoppingListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_edit);

        Intent intent = getIntent();
        if (!intent.hasExtra(Extra.CRUD)) throw new ExtrasNotFoundException(Extra.CRUD);
        this.crudAction = (Crud)intent.getSerializableExtra(Extra.CRUD);

        Button button = findViewById(R.id.listEditButton);
        TextInputEditText listEditName = findViewById(R.id.listEditName);

        switch(crudAction) {
            case CREATE:
                button.setText("Create List");
                break;
            case UPDATE:
                button.setText("Update List Name");

                if (!intent.hasExtra(Extra.LIST_ID)) throw new ExtrasNotFoundException(Extra.LIST_ID);
                this.shoppingListId = (int)intent.getLongExtra(Extra.LIST_ID, -1);
                listEditName.setText(intent.getStringExtra(Extra.NAME));
                break;
        }
    }

    public void buttonClick(View view) {
        TextInputEditText textView = findViewById(R.id.listEditName);
        String displayName = textView.getText().toString();
        if (displayName.trim().length() == 0) displayName = "New List";

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.displayName = displayName;

        ShoppingListDb db = ShoppingListDb.getFileDatabase(getApplication());
        ShoppingListDao dao = db.shoppingListModel();

        ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();

        if (this.crudAction == Crud.CREATE) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    dao.insert(shoppingList);
                }
            });
        }
        else if (this.crudAction == Crud.UPDATE) {
            shoppingList.id = this.shoppingListId;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    dao.update(shoppingList);
                }
            });
        }

        finish();
    }
}