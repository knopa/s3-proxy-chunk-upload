package controllers.model;


/**
 * List of possible states for upload file
 *
 */
public enum DBType {
    Postgres(0, "postgres"),
    Mysql(1, "mysql");

    int type;
    String name;

    DBType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public static DBType getInstanceByName(String name) {
        for (DBType instance : DBType.values()) {
            if (name.equals(instance.name)) {
                return instance;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
