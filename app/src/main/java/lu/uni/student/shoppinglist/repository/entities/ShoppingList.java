package lu.uni.student.shoppinglist.repository.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="shopping_list")
public class ShoppingList {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}