package lu.uni.student.shoppinglist.activities.List;

import android.content.res.Resources;

public class ListIcons {
    public static int[] getResourceIds(Resources resources, String packageName) {
        int[] id = new int[31];

        id[0] = resources.getIdentifier("aldi", "drawable", packageName);
        id[1] = resources.getIdentifier("auchan", "drawable", packageName);
        id[2] = resources.getIdentifier("cactus", "drawable", packageName);
        id[3] = resources.getIdentifier("carrefour", "drawable", packageName);
        id[4] = resources.getIdentifier("cora", "drawable", packageName);
        id[5] = resources.getIdentifier("lidl", "drawable", packageName);

        for(int i=6; i<31; i++) {
            int resourceId = resources.getIdentifier("type" + i, "drawable", packageName);
            id[i] = resourceId;
        }

        return id;
    }
}
