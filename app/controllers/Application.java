package controllers;

import actors.NotificationTask;
import actors.VideoConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.model.ConvertStatus;
import controllers.model.NotificationType;
import controllers.model.UploadStatus;
import global.GlobalParams;
import models.S3File;
import play.libs.ws.WS;
import play.mvc.*;

import views.html.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Application extends Controller {
    private static final String SUBSCRIPTION_CONFIRMATION = "SubscriptionConfirmation";
    private static final String NOTIFICATION = "Notification";
    public static Result index() {
        return ok(index.render(""));
    }


    public static Result elastic() {
        try {
            JsonNode request = request().body().asJson();
            if (request == null) {
                ObjectMapper mapper = new ObjectMapper();
                request = mapper.readTree(request().body().asText());
            }
            if (request != null) {
                switch (getFieldValue(request, "Type")) {
                    case SUBSCRIPTION_CONFIRMATION:
                        String url = getFieldValue(request, "SubscribeURL");
                        WS.url(url).get();
                        break;
                    case NOTIFICATION:
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode message = mapper.readTree(request.findValue("Message").asText());
                        String jobId = getFieldValue(message, "jobId");
                        String state = getFieldValue(message, "state");

                        if (!jobId.isEmpty()) {
                            S3File file = S3File.getFileByJob(jobId);
                            if (file != null) {
                                switch (state) {
                                    case "ERROR":
                                    case "WARNING":
                                        S3File.updateConvertStatus(file.id, ConvertStatus.ERROR.getType(), jobId, message.toString());
                                        break;
                                    case "COMPLETED":
                                        S3File.updateConvertStatus(file.id, ConvertStatus.CONVERTED.getType(), jobId, message.toString());

                                        JsonNode outputs = message.findPath("outputs");
                                        HashMap<String, String> keys = new HashMap<>();
                                        if(outputs.isArray()) {
                                            for (final JsonNode output : outputs) {
                                                String key = output.findPath("key").asText();
                                                String presetId = output.findPath("presetId").asText();
                                                for(Map.Entry<String, String> entry : GlobalParams.VIDEO_PRESETS.entrySet()) {
                                                    String preset = entry.getValue();
                                                    if(presetId.equals(preset)) {
                                                        keys.put(entry.getKey(), key);
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        NotificationTask notification = new NotificationTask(file.id, file.name, NotificationType.CONVERT, keys);
                                        Thread notificationThread = new Thread(notification);
                                        notificationThread.start();
                                        break;
                                }
                            } else {
                                return badRequest("S3File not found, jobId: " + jobId);
                            }
                        }
                        break;
                }

            } else {
                play.Logger.error("Elastic empty request");
            }
            return ok(index.render(""));
        } catch (Exception e) {
            play.Logger.error("Elastic empty request :" + e);
        }
        return ok(index.render(""));
    }

    public static Result convert() {
        JsonNode request = request().body().asJson();
        String id = getFieldValue(request, "id");
        String url = getFieldValue(request, "url");

        if(!id.isEmpty()) {
            S3File file = S3File.getFile(id);
            if(file != null) {
                id = file.id;
            }
        }

        if(!url.isEmpty()) {
            S3File file = S3File.getFileByName(url);
            if(file == null) {
                id = UUID.randomUUID().toString();
                if (url.startsWith(GlobalParams.AWS_S3_BUCKET_PATH)) {
                    url = url.replace(GlobalParams.AWS_S3_BUCKET_PATH, "");
                }
                S3File s3File = new S3File(id, url);
                s3File.status = UploadStatus.READ.getType();
                s3File.save();
            } else {
                id = file.id;
            }
        }

        if(GlobalParams.isAllowConvert()) {
            VideoConverter converter = new VideoConverter(id);
            Thread convertThread = new Thread(converter);
            convertThread.start();
        }

        return ok(index.render(""));
    }

    private static String getFieldValue(JsonNode request, String fieldName) {
        if(request.has(fieldName)) {
            return request.get(fieldName).asText();
        }
        return "";
    }
}
