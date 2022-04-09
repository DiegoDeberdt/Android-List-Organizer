package it.geronimo.shoppinglist.activities.List;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import it.geronimo.shoppinglist.repository.dao.*;
import it.geronimo.shoppinglist.repository.ShoppingListDb;
import it.geronimo.shoppinglist.repository.entities.*;

import java.util.List;

public class ListActivityViewModel extends AndroidViewModel {

    private LiveData<List<ShoppingListWithCalculatedValues>> shoppingListList;

    public ListActivityViewModel(Application application) {
        super(application);
    }

    public LiveData<List<ShoppingListWithCalculatedValues>> getShoppingListList() {
        if (this.shoppingListList == null) {
            ShoppingListDb db = ShoppingListDb.getFileDatabase(getApplication());
            ShoppingListDao dao = db.shoppingListModel();
            this.shoppingListList = dao.loadAll();
        }
        return this.shoppingListList;
    }
}
