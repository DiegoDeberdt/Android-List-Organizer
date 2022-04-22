package lu.uni.student.dbdo.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import lu.uni.student.dbdo.repository.dao.*;
import lu.uni.student.dbdo.repository.entities.*;

@Database(entities = {ListEntity.class, ListItemEntity.class}, version = 1)
public abstract class ListDb extends RoomDatabase {

    private static ListDb INSTANCE;

    public abstract ListDao shoppingListModel();
    public abstract ListItemDao shoppingListItemModel();

    public static ListDb getFileDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ListDb.class, "dbdo").createFromAsset("database/shopping_list.db").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}