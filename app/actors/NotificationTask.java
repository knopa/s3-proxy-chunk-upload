package actors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.model.NotificationType;
import controllers.model.UploadStatus;
import global.GlobalParams;
import play.Logger;
import play.libs.ws.WS;

import java.util.HashMap;

public class NotificationTask implements Runnable {
    private String id;
    private String uploadFileName;
    private NotificationType type;
    private HashMap<String, String> keys;

    public NotificationTask(String id,  String uploadFileName,  NotificationType type, HashMap<String, String> keys) {
        this.id = id;
        this.uploadFileName = uploadFileName;
        this.type = type;
        this.keys = keys;
    }

    @Override
    public void run() {
        try {
            switch (type) {
                case UPLOAD:
                    notify(GlobalParams.SEND_UPLOAD_STATUS_URL);
                    break;
                case CONVERT:
                    notify(GlobalParams.SEND_CONVERT_STATUS_URL);
                    break;
            }
        } catch(Exception e) {
            Logger.error("Notification Error: " + e);
        }
    }

    private void notify(String url) {
        if (!url.isEmpty()) {
            Logger.info("Send request to: " + url);
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode json = mapper.createObjectNode();

            JsonNode k = mapper.valueToTree(keys);
            json.put("id", id)
                .put("name", uploadFileName)
                .put("bucket", GlobalParams.AWS_S3_BUCKET)
                .put("type", type.getType())
                .put("status", UploadStatus.READ.getType())
                .put("keys", k);

            Logger.info("Send post request: " + json);
            WS.url(url).post(json);
        }
    }
}
