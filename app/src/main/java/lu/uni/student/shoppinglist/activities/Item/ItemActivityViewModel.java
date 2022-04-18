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

    private LiveData<List<ShoppingListItem>> shoppingListItemList;
    private LiveData<List<ShoppingListWithCalculatedValues>> shoppingLists;

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

    public LiveData<List<ShoppingListWithCalculatedValues>> getShoppingLists(long parentShoppingListId){
        if (this.shoppingLists == null){
            ShoppingListDb db = ShoppingListDb.getFileDatabase(getApplication());
            ShoppingListDao dao = db.shoppingListModel();
            this.shoppingLists = dao.loadAllSubLevelLists(parentShoppingListId);
        }
        return this.shoppingLists;
    }
}