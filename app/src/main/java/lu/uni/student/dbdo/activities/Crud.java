package lu.uni.student.dbdo.activities;

public enum Crud {
    CREATE(0x1),
    UPDATE(0x2);

    private final int flag;

    Crud(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}

