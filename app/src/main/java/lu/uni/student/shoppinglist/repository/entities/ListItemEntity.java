package lu.uni.student.shoppinglist.repository.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName="list_item",
        foreignKeys = {@ForeignKey(entity = ListEntity.class,
                                   parentColumns = "id",
                                   childColumns = "listId",
                                   onDelete = ForeignKey.CASCADE) })
public class ListItemEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public long listId;

    @NonNull
    public String displayName;

    public String description;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    public boolean purchasedFlag;

    @NonNull
    public boolean archivedFlag;
}