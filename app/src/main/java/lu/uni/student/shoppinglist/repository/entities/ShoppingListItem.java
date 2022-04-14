package lu.uni.student.shoppinglist.repository.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName="shopping_list_item",
        foreignKeys = {@ForeignKey(entity = ShoppingList.class,
                                   parentColumns = "id",
                                   childColumns = "shoppingListId",
                                   onDelete = ForeignKey.CASCADE) })
public class ShoppingListItem {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public long shoppingListId;

    @NonNull
    public String displayName;

    public String description;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    public boolean flagPurchased;
}