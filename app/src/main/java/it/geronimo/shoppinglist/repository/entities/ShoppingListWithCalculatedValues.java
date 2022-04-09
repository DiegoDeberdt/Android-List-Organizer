package it.geronimo.shoppinglist.repository.entities;

import androidx.room.Ignore;

public class ShoppingListWithCalculatedValues extends ShoppingList {

    public int totalNumberOfItems = -1;

    public int numberOfUnFlaggedItems = -1;

    @Override
    public String toString() {
        return displayName + " " + Integer.toString(numberOfUnFlaggedItems) + "/" + Integer.toString(totalNumberOfItems);
    }
}