package lu.uni.student.shoppinglist.repository.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName="shopping_list",
        foreignKeys = {@ForeignKey(entity = ShoppingList.class,
                                   parentColumns = "id",
                                   childColumns = "parentId",
                                   onDelete = ForeignKey.NO_ACTION) })
public class ShoppingList {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String displayName;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    public int iconIndex;

    // Nullable !
    public Long parentId;

    @NonNull
    public boolean archived;
}