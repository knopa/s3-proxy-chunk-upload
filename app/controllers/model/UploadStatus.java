package controllers.model;

/**
 * List of possible states for upload file
 *
 */
public enum UploadStatus {
    INIT(0, "init"),
    UPLOADED(1, "uploaded"),
    COMBINED(2, "combined"),
    READ(3, "read"),
    ABORTED(4, "aborted"),
    ERROR(5, "error");

    int type;
    String name;

    UploadStatus(int type, String name) {
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
