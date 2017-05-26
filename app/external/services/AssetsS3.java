package external.services;

import java.io.File;

import play.Logger;
import plugins.S3Module;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class AssetsS3 {

    /**
     * Upload a file which is in the assets bucket.
     * 
     * @param fileName File name
     * @param file File
     * @param contentType Content type for file
     * @return
     */
    public static boolean uploadFile(String fileName, File file, String contentType) {
        try {
            if (S3Module.amazonS3 != null) {
                String bucket = S3Module.s3Bucket;
                ObjectMetadata metaData = new ObjectMetadata();
                if (contentType != null) {
                    metaData.setContentType(contentType);
                }
                PutObjectRequest putObj = new PutObjectRequest(bucket,
                        fileName, file);
                putObj.setMetadata(metaData);
                putObj.withCannedAcl(CannedAccessControlList.PublicRead);
                S3Module.amazonS3.putObject(putObj);
                return true;
            } else {
                Logger.error("Could not save because amazonS3 was null");
                return false;
            }
        } catch (Exception e) {
            Logger.error("S3 Upload -" + e.getMessage());
            return false;
        }
    }
}
