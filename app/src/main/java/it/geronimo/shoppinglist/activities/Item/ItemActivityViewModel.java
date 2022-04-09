package it.geronimo.shoppinglist.activities.Item;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.geronimo.shoppinglist.repository.*;
import it.geronimo.shoppinglist.repository.dao.*;
import it.geronimo.shoppinglist.repository.entities.*;

public class ItemActivityViewModel extends AndroidViewModel {
    private MutableLiveData<Integer> shoppingListId;
    private LiveData<List<ShoppingListItem>> shoppingListItemList;

    public ItemActivityViewModel(Application application) {
        super(application);
    }

    public LiveData<List<ShoppingListItem>> getShoppingListItemList(long shoppingListId) {
        if (this.shoppingListItemList == null) {
            ShoppingListDb db = ShoppingListDb.getFileDatabase(getApplication());
            ShoppingListItemDao dao = db.shoppingListItemModel();
            this.shoppingListItemList = dao.loadItemsByListId(shoppingListId);
        }
        return this.shoppingListItemList;
    }

    public void setShoppingListId(int shoppingListId) {
        this.shoppingListId.setValue(shoppingListId);
    }

    public LiveData<Integer> getShoppingListId() {
        return this.shoppingListId;
    }
}