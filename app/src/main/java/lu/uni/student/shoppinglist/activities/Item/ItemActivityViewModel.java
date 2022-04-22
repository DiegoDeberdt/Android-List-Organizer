package lu.uni.student.shoppinglist.activities.Item;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import lu.uni.student.shoppinglist.repository.*;
import lu.uni.student.shoppinglist.repository.dao.*;
import lu.uni.student.shoppinglist.repository.entities.*;

public class ItemActivityViewModel extends AndroidViewModel {

    private LiveData<List<ListItemEntity>> shoppingListItemList;
    private LiveData<List<ListEntityWithCalculatedValues>> shoppingLists;

    public ItemActivityViewModel(Application application) {
        super(application);
    }

    public LiveData<List<ListItemEntity>> getShoppingListItemList(long shoppingListId) {
        if (this.shoppingListItemList == null) {
            ListDb db = ListDb.getFileDatabase(getApplication());
            ListItemDao dao = db.shoppingListItemModel();
            this.shoppingListItemList = dao.loadItemsByListId(shoppingListId);
        }
        return this.shoppingListItemList;
    }

    public LiveData<List<ListEntityWithCalculatedValues>> getShoppingLists(long parentShoppingListId){
        if (this.shoppingLists == null){
            ListDb db = ListDb.getFileDatabase(getApplication());
            ListDao dao = db.shoppingListModel();
            this.shoppingLists = dao.loadAllSubLevelLists(parentShoppingListId);
        }
        return this.shoppingLists;
    }
}