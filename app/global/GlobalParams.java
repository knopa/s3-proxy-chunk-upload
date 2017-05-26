package global;

import controllers.model.DBType;
import play.Logger;
import play.mvc.Http;

import java.util.HashMap;

public class GlobalParams {
    public static String UPLOAD_PARTS_FOLDER = "parts";
    public static String EXTENSION_PART = ".part";
    public static String AWS_S3_BUCKET_PATH;

    public static int UPLOAD_SCHEDULE_SECONDS;
    public static int LIMIT_PROCCESS_FILES;
    public static DBType DB_TYPE;
    public static String AWS_ACCESS_KEY;
    public static String AWS_SECRET_KEY;
    public static String AWS_S3_BUCKET;
    public static String AWS_S3_HOST;
    public static String UPLOAD_FOLDER;
    public static String SEND_UPLOAD_STATUS_URL;
    public static String SEND_CONVERT_STATUS_URL;
    public static String AWS_ET_END_POINT;
    public static String VIDEO_PIPELINE_ID;
    public static HashMap<String, String> VIDEO_PRESETS;

    public static boolean isAllowConvert() {
        return VIDEO_PRESETS != null && VIDEO_PRESETS.size() > 0;
    }
}
