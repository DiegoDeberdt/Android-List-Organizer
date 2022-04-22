package lu.uni.student.dbdo.activities.Item;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import lu.uni.student.dbdo.repository.*;
import lu.uni.student.dbdo.repository.dao.*;
import lu.uni.student.dbdo.repository.entities.*;

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