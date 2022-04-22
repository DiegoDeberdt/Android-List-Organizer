package lu.uni.student.dbdo.activities.List;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import lu.uni.student.dbdo.repository.ListDb;
import lu.uni.student.dbdo.repository.dao.*;
import lu.uni.student.dbdo.repository.entities.*;

import java.util.List;

public class ListActivityViewModel extends AndroidViewModel {

    private LiveData<List<ListEntityWithCalculatedValues>> shoppingListList;

    public ListActivityViewModel(Application application) {
        super(application);
    }

    public LiveData<List<ListEntityWithCalculatedValues>> getShoppingListList() {
        if (this.shoppingListList == null) {
            ListDb db = ListDb.getFileDatabase(getApplication());
            ListDao dao = db.shoppingListModel();
            this.shoppingListList = dao.loadAllTopLevelLists();
        }
        return this.shoppingListList;
    }
}
