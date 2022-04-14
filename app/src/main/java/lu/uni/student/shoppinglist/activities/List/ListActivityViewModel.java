package lu.uni.student.shoppinglist.activities.List;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import lu.uni.student.shoppinglist.repository.dao.*;
import lu.uni.student.shoppinglist.repository.ShoppingListDb;
import lu.uni.student.shoppinglist.repository.entities.*;

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
            this.shoppingListList = dao.loadAllTopLevelLists();
        }
        return this.shoppingListList;
    }
}
