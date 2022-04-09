package it.geronimo.shoppinglist.activities;

public enum Crud {
    CREATE(0x1), READ(0x2), UPDATE(0x4), DELETE(0x8);
    private final int flag;
    Crud(int flag) {this.flag = flag;}
    public int getFlag() {return flag;}
}

