package lu.uni.student.shoppinglist.activities.List;

import android.content.res.Resources;

public class ListIcons {
    public static int[] getResourceIds(Resources resources, String packageName) {
        int[] id = new int[36];

        for(int i=0; i<36; i++) {
            int resourceId = resources.getIdentifier("type" + i, "drawable", packageName);
            id[i] = resourceId;
        }

        return id;
    }
}
