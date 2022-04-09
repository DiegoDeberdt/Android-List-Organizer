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
public interface ShoppingListItemDao {
    @Query("SELECT * FROM shopping_list_item WHERE shoppingListId = :id ORDER BY flagPurchased, displayName")
    LiveData<List<ShoppingListItem>> loadItemsByListId(long id);

    @Query("SELECT * FROM shopping_list_item WHERE id = :id")
    ShoppingListItem getItemById(long id);

    @Insert
    long insert(ShoppingListItem shoppingListItem);

    @Query("DELETE FROM shopping_list_item WHERE id = :id")
    void delete(long id);

    @Query("UPDATE shopping_list_item SET displayName = :displayName, description = :description WHERE id = :id")
    void update(long id, String displayName, String description);

    @Query("UPDATE shopping_list_item SET flagPurchased = :flagPurchased WHERE id= :id ")
    void update(long id, boolean flagPurchased);

    @Query("INSERT INTO shopping_list_item(shoppingListId, displayName, description) " +
           "SELECT :destinationId, displayName, description FROM shopping_list_item WHERE shoppingListId = :sourceId")
    void copy(long sourceId, long destinationId);
}