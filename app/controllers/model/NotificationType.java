package controllers.model;


public enum NotificationType {
    UPLOAD(0, "upload"),
    CONVERT(1, "convert");

    int type;
    String name;

    NotificationType(int type, String name) {
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
