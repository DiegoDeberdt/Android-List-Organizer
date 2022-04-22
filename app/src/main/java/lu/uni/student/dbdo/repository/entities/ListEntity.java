package lu.uni.student.dbdo.repository.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName="list",
        foreignKeys = {@ForeignKey(entity = ListEntity.class,
                                   parentColumns = "id",
                                   childColumns = "parentId",
                                   onDelete = ForeignKey.NO_ACTION) })
public class ListEntity {
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
    public boolean archivedFlag;
}