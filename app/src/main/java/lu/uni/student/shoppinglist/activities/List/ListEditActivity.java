package lu.uni.student.shoppinglist.activities.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import lu.uni.student.shoppinglist.ExtrasNotFoundException;
import lu.uni.student.shoppinglist.R;
import lu.uni.student.shoppinglist.activities.Crud;
import lu.uni.student.shoppinglist.activities.Extra;
import lu.uni.student.shoppinglist.repository.ShoppingListDb;
import lu.uni.student.shoppinglist.repository.ThreadPerTaskExecutor;
import lu.uni.student.shoppinglist.repository.dao.ShoppingListDao;
import lu.uni.student.shoppinglist.repository.entities.ShoppingList;

public class ListEditActivity extends AppCompatActivity {

    // TODO: use a ViewModel

    private int imageIndex;
    private Crud crudAction;
    private long shoppingListId;
    private ListEditAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_edit);

        Intent intent = getIntent();

        if (!intent.hasExtra(Extra.CRUD)) throw new ExtrasNotFoundException(Extra.CRUD);
        this.crudAction = (Crud)intent.getSerializableExtra(Extra.CRUD);

        initTitleAndLabel(intent);

        int[] imageIds = getImageIds();
        if (this.crudAction == Crud.CREATE) {
            this.listAdapter = new ListEditAdapter(this, imageIds);
        }
        else if (this.crudAction == Crud.UPDATE) {
            if (!intent.hasExtra(Extra.IMAGE_INDEX)) throw new ExtrasNotFoundException(Extra.IMAGE_INDEX);
            this.imageIndex = intent.getIntExtra(Extra.IMAGE_INDEX, 0);
            this.listAdapter = new ListEditAdapter(this, imageIds, imageIndex);
        }

        RecyclerView imageList = findViewById(R.id.imageList);
        imageList.setAdapter(listAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 7);
        imageList.setLayoutManager(layoutManager);
    }

    private int[] getImageIds() {
        int[] images = new int[25];
        for(int i=0; i<25; i++) {
            String resourceIdentifier = "R.drawable.type" + i;
            int imageId = getResources().getIdentifier("type" + i, "drawable", getPackageName());
            images[i] = imageId;
        }
        return images;
    }

    private void initTitleAndLabel(Intent intent) {
        Button button = findViewById(R.id.listEditButton);
        TextInputEditText listEditName = findViewById(R.id.listEditName);

        switch(this.crudAction) {
            case CREATE:
                button.setText(R.string.create_list);
                setTitle(R.string.new_list);
                break;
            case UPDATE:
                button.setText(R.string.rename_list);
                setTitle(R.string.rename_list);

                if (!intent.hasExtra(Extra.LIST_ID)) throw new ExtrasNotFoundException(Extra.LIST_ID);
                this.shoppingListId = intent.getLongExtra(Extra.LIST_ID, -1);
                listEditName.setText(intent.getStringExtra(Extra.NAME));
                break;
        }
    }

    public void buttonClick(View view) {
        TextInputEditText textView = findViewById(R.id.listEditName);
        String displayName = textView.getText().toString();
        if (displayName.trim().length() == 0) displayName = getString(R.string.new_list);

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.displayName = displayName;
        shoppingList.id = this.shoppingListId;
        shoppingList.imageId = listAdapter.getSelectedImageResource();

        ShoppingListDb db = ShoppingListDb.getFileDatabase(getApplication());
        ShoppingListDao dao = db.shoppingListModel();

        ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();
        if (this.crudAction == Crud.CREATE) executor.execute(() -> dao.insert(shoppingList));
        else if (this.crudAction == Crud.UPDATE) executor.execute(() -> dao.update(shoppingList));

        finish();
    }
}