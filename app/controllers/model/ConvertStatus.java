package controllers.model;

public enum ConvertStatus {
    INIT(0, "init"),
    PROCESSING(1, "processing"),
    CONVERTED(2, "converted"),
    ERROR(3, "error");

    int type;
    String name;

    ConvertStatus(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }
}
