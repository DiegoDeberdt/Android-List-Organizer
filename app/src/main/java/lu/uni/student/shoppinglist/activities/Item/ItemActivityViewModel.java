package lu.uni.student.shoppinglist.activities.Item;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lu.uni.student.shoppinglist.repository.*;
import lu.uni.student.shoppinglist.repository.dao.*;
import lu.uni.student.shoppinglist.repository.entities.*;

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