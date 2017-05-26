package actors;

import java.io.File;
import java.util.List;
import java.util.UUID;

import controllers.model.NotificationType;
import models.S3File;
import controllers.model.UploadStatus;
import controllers.model.DTO.CompleteUploadDTO;
import external.services.AssetsS3;
import global.GlobalParams;
import play.Logger;
import utils.IOUploadUtils;
import utils.SerializeUtils;
import akka.actor.UntypedActor;

public class UploadToS3Actor extends UntypedActor {
    
    @Override
    public void onReceive(Object msg) {
        //Marks the file for processing
        try {
            String uniq =  UUID.randomUUID().toString();
            S3File.processFiles(uniq);

            //Check and get the party marked for process uploaded files
            List<S3File> uploadedFiles = S3File.getUploadedFiles(uniq);
            for (S3File file : uploadedFiles) {
                String id = file.id;
                String dirPath = IOUploadUtils.getPaths(GlobalParams.UPLOAD_FOLDER, id);
                String dirPartsPath = IOUploadUtils.getPaths(dirPath, GlobalParams.UPLOAD_PARTS_FOLDER);
                try {
                    if (file.parts == null) {
                        Logger.error("Not found parts of file: " + id);
                    }

                    CompleteUploadDTO completeUploadDTO = (CompleteUploadDTO) SerializeUtils.fromString(file.parts.toString());

                    // Combined file
                    File uploadFile = new File(IOUploadUtils.getPaths(dirPath, id + file.getExtension()));
                    IOUploadUtils.combinedFile(dirPartsPath, GlobalParams.EXTENSION_PART, uploadFile, completeUploadDTO);
                    S3File.updateStatus(id, UploadStatus.COMBINED.getType());

                    // Upload to S3
                    String uploadFileName = file.getActualFileName();
                    if (!AssetsS3.uploadFile(uploadFileName, uploadFile, null)) {
                        Logger.error("Error upload file to S3: " + id);
                        S3File.updateStatus(id, UploadStatus.ERROR.getType());
                    } else {
                        S3File.updateStatus(id, UploadStatus.READ.getType());

                        NotificationTask notification = new NotificationTask(id, uploadFileName, NotificationType.UPLOAD, null);
                        Thread notificationThread = new Thread(notification);
                        notificationThread.start();

                        if(GlobalParams.isAllowConvert()) {
                            VideoConverter converter = new VideoConverter(id);
                            Thread convertThread = new Thread(converter);
                            convertThread.start();
                        }

                        IOUploadUtils.removeDir(dirPath);
                    }

                } catch (Exception e) {
                    Logger.error("Error: " + e);
                    S3File.updateStatus(id, UploadStatus.ERROR.getType());
                }
            }
        }
        catch (Exception e) {
            Logger.error("ActorError: " + e);
        }
    }
}
