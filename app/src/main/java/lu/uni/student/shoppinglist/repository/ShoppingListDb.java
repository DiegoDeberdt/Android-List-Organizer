package lu.uni.student.shoppinglist.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import lu.uni.student.shoppinglist.repository.dao.*;
import lu.uni.student.shoppinglist.repository.entities.*;

@Database(entities = {ShoppingList.class, ShoppingListItem.class}, version = 1)
public abstract class ShoppingListDb extends RoomDatabase {

    private static ShoppingListDb INSTANCE;

    public abstract ShoppingListDao shoppingListModel();
    public abstract ShoppingListItemDao shoppingListItemModel();

    public static ShoppingListDb getFileDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ShoppingListDb.class, "shopping_list_db").createFromAsset("database/shopping_list.db").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}