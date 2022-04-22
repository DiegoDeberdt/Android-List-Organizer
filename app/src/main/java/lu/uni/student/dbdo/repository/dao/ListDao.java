package lu.uni.student.dbdo.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import lu.uni.student.dbdo.repository.entities.*;

@Dao
public interface ListDao {
    @Query( "SELECT id , displayName , iconIndex, parentId, archivedFlag, (" +
            "WITH cte AS (" +
            "SELECT list.id as id " +
            "UNION ALL " +
            "SELECT l.id FROM list l INNER JOIN cte c ON l.parentId = c.id) " +
            "SELECT COUNT(*) FROM list_item WHERE listId IN cte AND archivedFlag = 0" +
            ") as totalNumberOfItems, (" +
            "WITH cte AS (" +
            "SELECT list.id as id " +
            "UNION ALL " +
            "SELECT l.id FROM list l INNER JOIN cte c ON l.parentId = c.id)" +
            "SELECT COUNT(*) FROM list_item i INNER JOIN list l on i.listId = l.id WHERE " +
            "listId IN cte and purchasedFlag = 0 AND i.archivedFlag = 0" +
            ") as numberOfUnFlaggedItems " +
            "FROM list " +
            "WHERE list.parentId IS NULL AND archivedFlag = 0")
    LiveData<List<ListEntityWithCalculatedValues>> loadAllTopLevelLists();

    @Query( "SELECT id , displayName , iconIndex, parentId, archivedFlag, (" +
            "WITH cte AS (" +
            "SELECT list.id as id " +
            "UNION ALL " +
            "SELECT l.id FROM list l INNER JOIN cte c ON l.parentId = c.id) " +
            "SELECT COUNT(*) FROM list_item WHERE listId IN cte AND archivedFlag = 0" +
            ") as totalNumberOfItems, (" +
            "WITH cte AS (" +
            "SELECT list.id as id " +
            "UNION ALL " +
            "SELECT l.id FROM list l INNER JOIN cte c ON l.parentId = c.id)" +
            "SELECT COUNT(*) FROM list_item i INNER JOIN list l on i.listId = l.id WHERE " +
            "listId IN cte and purchasedFlag = 0 AND i.archivedFlag = 0" +
            ") as numberOfUnFlaggedItems " +
            "FROM list " +
            "WHERE list.parentId = :parentId AND archivedFlag = 0")
    LiveData<List<ListEntityWithCalculatedValues>> loadAllSubLevelLists(long parentId);

    @Insert
    long insert(ListEntity listEntity);

    @Query("DELETE FROM list WHERE id= :listId")
    void delete(long listId);

    @Query("UPDATE list SET archivedFlag = 1 WHERE id = :listId")
    void archive(long listId);

    @Query("UPDATE list SET archivedFlag = 0 WHERE id = :listId")
    void restore(long listId);

    @Update
    void update(ListEntity listEntity);

    @Query("SELECT * FROM list WHERE parentId = :parentId")
    List<ListEntity> getListsToCopy(long parentId);
}
