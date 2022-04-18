package lu.uni.student.shoppinglist.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import lu.uni.student.shoppinglist.repository.entities.*;

@Dao
public interface ShoppingListDao {
    @Query( "SELECT id , displayName , iconIndex, parentId, (" +
            "WITH cte AS (" +
            "SELECT list.id as id " +
            "UNION ALL " +
            "SELECT l.id FROM shopping_list l INNER JOIN cte c ON l.parentId = c.id) " +
            "SELECT COUNT(*) FROM shopping_list_item WHERE shoppingListId IN cte " +
            ") as totalNumberOfItems, (" +
            "WITH cte AS (" +
            "SELECT list.id as id " +
            "UNION ALL " +
            "SELECT l.id FROM shopping_list l INNER JOIN cte c ON l.parentId = c.id)" +
            "SELECT COUNT(*) FROM shopping_list_item i INNER JOIN shopping_list l on i.shoppingListId = l.id WHERE " +
            "shoppingListId IN cte and flagPurchased = 0 " +
            ") as numberOfUnFlaggedItems " +
            "FROM shopping_list list " +
            "WHERE list.parentId IS NULL")
    LiveData<List<ShoppingListWithCalculatedValues>> loadAllTopLevelLists();

    @Query( "SELECT id , displayName , iconIndex, parentId, (" +
            "WITH cte AS (" +
            "SELECT list.id as id " +
            "UNION ALL " +
            "SELECT l.id FROM shopping_list l INNER JOIN cte c ON l.parentId = c.id) " +
            "SELECT COUNT(*) FROM shopping_list_item WHERE shoppingListId IN cte " +
            ") as totalNumberOfItems, (" +
            "WITH cte AS (" +
            "SELECT list.id as id " +
            "UNION ALL " +
            "SELECT l.id FROM shopping_list l INNER JOIN cte c ON l.parentId = c.id)" +
            "SELECT COUNT(*) FROM shopping_list_item i INNER JOIN shopping_list l on i.shoppingListId = l.id WHERE " +
            "shoppingListId IN cte and flagPurchased = 0 " +
            ") as numberOfUnFlaggedItems " +
            "FROM shopping_list list " +
            "WHERE list.parentId = :parentId")
    LiveData<List<ShoppingListWithCalculatedValues>> loadAllSubLevelLists(long parentId);

    @Insert
    long insert(ShoppingList shoppingList);

    @Query("DELETE FROM shopping_list WHERE id= :shoppingListId")
    void delete(long shoppingListId);

    @Update
    void update(ShoppingList shoppingList);

    @Query("SELECT * FROM shopping_list WHERE parentId = :parentId")
    List<ShoppingList> getListsToCopy(long parentId);
}
