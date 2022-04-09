package it.geronimo.shoppinglist.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.geronimo.shoppinglist.repository.entities.*;

@Dao
public interface ShoppingListDao {
    @Query("SELECT id" +
            ", displayName" +
            ", (SELECT COUNT(*) FROM shopping_list_item WHERE shoppingListId = list.id) as totalNumberOfItems" +
            ", (SELECT COUNT(*) FROM shopping_list_item WHERE shoppingListId = list.id AND flagPurchased = 0) as numberOfUnFlaggedItems" +
            " FROM shopping_list list")
    LiveData<List<ShoppingListWithCalculatedValues>> loadAll();

    @Insert
    long insert(ShoppingList shoppingList);

    @Query("DELETE FROM shopping_list WHERE id= :shoppingListId")
    void delete(long shoppingListId);

    @Update
    void update(ShoppingList shoppingList);
}
