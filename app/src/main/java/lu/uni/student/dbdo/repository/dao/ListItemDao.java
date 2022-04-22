package lu.uni.student.dbdo.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import lu.uni.student.dbdo.repository.entities.*;

@Dao
public interface ListItemDao {

    @Query("SELECT * FROM list_item WHERE listId = :id AND archivedFlag = 0 ORDER BY purchasedFlag, displayName COLLATE NOCASE ASC")
    LiveData<List<ListItemEntity>> loadItemsByListId(long id);

    @Query("SELECT * FROM list_item WHERE id = :id")
    ListItemEntity getItemById(long id);

    @Insert
    long insert(ListItemEntity listItemEntity);

    @Query("DELETE FROM list_item WHERE id = :id")
    void delete(long id);

    @Query("UPDATE list_item SET archivedFlag = 1 WHERE id = :id")
    void archive(long id);

    @Query("UPDATE list_item SET archivedFlag = 0 WHERE id = :id")
    void restore(long id);

    @Query("UPDATE list_item SET displayName = :displayName, description = :description WHERE id = :id")
    void update(long id, String displayName, String description);

    @Query("UPDATE list_item SET purchasedFlag = :flagPurchased WHERE id= :id ")
    void update(long id, boolean flagPurchased);

    @Query("INSERT INTO list_item(listId, displayName, description, archivedFlag) " +
           "SELECT :destinationId, displayName, description, 0 FROM list_item WHERE listId = :sourceId AND archivedFlag = 0")
    void copy(long sourceId, long destinationId);

    @Query("UPDATE list_item SET purchasedFlag = 0 WHERE listId = :listId AND archivedFlag = 0")
    void resetAllItemsInList(long listId);
}