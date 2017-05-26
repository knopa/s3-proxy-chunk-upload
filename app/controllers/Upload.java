package controllers;

import controllers.model.DTO.*;
import global.GlobalParams;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.xml.bind.UnmarshalException;

import org.w3c.dom.Document;

import controllers.model.UploadStatus;

import models.S3File;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.RawBuffer;
import play.mvc.Result;
import utils.IOUploadUtils;
import utils.JAXBUtils;
import utils.SerializeUtils;

public class Upload extends Controller {   

    /**
     * This operation use for initiates a multipart upload and for complete upload.
     * @param name Object name
     * @param uploads Parameter indicates the initiate operation, should not be null 
     * @param id , should not be null 
     * @return
     */
    public static Result initComplete(String name, String uploads,
            String id) {
        
        if (uploads == null && !checkValue(id)) {
            return Response.noSuchKey("Required parameter 'uploads' is not found", id);            
        } else if (checkValue(id)) {
            // complete action
            return complete(id);
        } else {
            // init action
            return init(name);
        } 
    }

    /**
     * Initiates a multipart upload and returns an upload ID.
     * This upload ID is used to associate all the parts in the specific multipart upload.
     * @param name Object name
     * @return Upload ID
     */
    private static Result init(String name) {       
        String id = initDB(name);

        return Response.success(new InitiateUploadDTO(id,
                GlobalParams.AWS_S3_BUCKET));
    }

    private static String initDB(String name) {
        String id = UUID.randomUUID().toString();
        if(name != null && name.startsWith(GlobalParams.AWS_S3_BUCKET_PATH)) {
            name = name.replace(GlobalParams.AWS_S3_BUCKET_PATH, "");
        }

        // Creating a record in a database
        S3File s3File = new S3File(id, name);
        s3File.save();
        return id;
    }

    /**
     * The operation uploads a part in a multipart upload.
     * If you upload a new part using the same part number that was used with a previous part,
     * the previously uploaded part is overwritten.
     * @param name Object name
     * @param partNumber Part number uniquely identifies a part
     * @param id Upload ID
     * @return The response includes the ETag header. 
     * You need to retain this value for use when you send the Complete Multipart Upload request.
     */
    @BodyParser.Of(BodyParser.Raw.class)
    public static Result uploadPart(String name, Integer partNumber, String id) {

        boolean isComplete = false;
        if(id == null && partNumber == null) {
            id = initDB(name);
            partNumber = 1;
            isComplete = true;
        } else {
            if (!checkValue(id)) {
                return Response.noSuchUpload();
            }

            if (partNumber == null || partNumber < 1) {
                return Response.invalidPart("Invalid part number", id);
            }

            S3File s3File = S3File.getFile(id);
            if(s3File == null) {
                return Response.noSuchKey("Upload id not found", id);
            }
            int status = s3File.status;
            if (status > UploadStatus.UPLOADED.getType()) {
                return Response.internalError("The part uploading is not possible, status: " + status, id);
            }
        }

        String eTag = String.valueOf(partNumber);
        try {
            File uploadPartFile;
            Http.MultipartFormData body = request().body().asMultipartFormData();
            if (body != null) {
                Http.MultipartFormData.FilePart multiPartFile = body.getFile("file");
                uploadPartFile = (File) multiPartFile.getFile();
            } else {
                RawBuffer raw = request().body().asRaw();
                if (raw == null) {
                    return Response.invalidPart("Body is empty", id);
                }
                uploadPartFile = request().body().asRaw().asFile();
            }
            
            if (uploadPartFile == null) {
                return Response.invalidPart("Missing file", id);
            } else {
                String contentMD5 = request().getHeader("Content-MD5");
                if (checkValue(contentMD5)) {
                    if (!contentMD5.equals(IOUploadUtils.getMD5(uploadPartFile))) {
                        return Response.internalError("The Content-MD5 you specified is not valid.", id);
                    }
                }
                eTag = IOUploadUtils.getMD5(uploadPartFile);
            }            
            String dirPath = IOUploadUtils.getPaths(GlobalParams.UPLOAD_FOLDER, id, "parts");
            IOUploadUtils.saveFile(dirPath, String.valueOf(partNumber)
                    + GlobalParams.EXTENSION_PART, uploadPartFile);

            if(isComplete) {
                CompleteUploadDTO completeUploadDTO = new CompleteUploadDTO();
                completeUploadDTO.getParts().add(0, new PartDTO(String.valueOf(partNumber)));

                String parts = SerializeUtils.toString(completeUploadDTO);
                S3File.updateParts(id, UploadStatus.UPLOADED.getType(), parts);

                return Response.success(new CompleteUploadResultDTO(
                        id, GlobalParams.AWS_S3_BUCKET, id));
            }
        } catch (IOException e) {
            return Response.invalidPart("Failed to save file", id);
        }

        response().setHeader("ETag", eTag);
        return ok();
    }
    
    /**
     * This operation completes a multipart upload by assembling previously uploaded parts.
     * There are only preserved the status of uploaded. 
     * The operation to save the file on the S3 server occurs in UploadToS3Actor manager.
     * @param id Upload id.
     * @return
     */
    @BodyParser.Of(BodyParser.Xml.class)
    private static Result complete(String id) {
        if(!checkValue(id)) {
            return Response.noSuchUpload();
        }

        CompleteUploadDTO completeUploadDTO = null;

        Document dom = request().body().asXml();
        try {
            if(dom == null) {
                String text = request().body().asText();
                if (text == null) {
                    return Response.unexpectedContent("Expecting Xml data", id);
                } else {
                    completeUploadDTO = JAXBUtils.unmarshallUploadFromDocumentString(text, null, CompleteUploadDTO.class);
                }
            } else {
                completeUploadDTO = JAXBUtils.unmarshallUploadFromDocument(dom, null, CompleteUploadDTO.class);
            }
            if(completeUploadDTO != null) {

                try {
                    String dirParentPath = IOUploadUtils.getPaths(GlobalParams.UPLOAD_FOLDER, id);
                    String dirPartsPath = IOUploadUtils.getPaths(dirParentPath, GlobalParams.UPLOAD_PARTS_FOLDER);
                    IOUploadUtils.checkParts(dirPartsPath, GlobalParams.EXTENSION_PART, completeUploadDTO);
                } catch (IOException e) {
                    return Response.invalidPart("Part is not found on the server storage", id);
                }

                String parts = SerializeUtils.toString(completeUploadDTO);
                S3File.updateParts(id, UploadStatus.UPLOADED.getType(), parts);
            } else {
                return Response.unexpectedContent("Wrong parsing Xml data", id);
            }

        } catch (UnmarshalException e) {
            return Response.internalError("Expecting Xml data", id);
        } catch (IOException e) {
            return Response.internalError("Serialize error", id);
        }

        return Response.success(new CompleteUploadResultDTO(id, GlobalParams.AWS_S3_BUCKET, id));
    }

        /**
         * This operation aborts a multipart upload.
         * After a multipart upload is aborted, no additional parts can be uploaded using that upload ID.
         * @param name Object name
         * @param id Upload id
         * @return
         */
    public static Result abort(String name, String id) {
        if(!checkValue(id)) {
            return Response.noSuchUpload();
        }
        
        S3File.updateStatus(id, UploadStatus.ABORTED.getType());
        String dirPath = IOUploadUtils.getPaths(GlobalParams.UPLOAD_FOLDER, id);
        try {
            IOUploadUtils.removeDir(dirPath);
        } catch (IOException e) {
            return Response.operationAborted("Failed to remove dir: " + dirPath, id);
        }
        return ok();
    }
    
    /**
     * This operation checks and returns the current status of the uploaded file.
     * @param id Upload id.
     * @return
     */
    public static Result check(String id) {
        
        if(!checkValue(id)) {
            return Response.noSuchUpload();
        }
        S3File s3File = S3File.getFile(id);
        if(s3File == null) {
            return Response.noSuchKey("Upload id not found", id);
        }
        
        return Response.success(new CheckUploadResultDTO(s3File.url, s3File.status));
    }
    
    private static boolean checkValue(String value) {
        if (value == null || "".equals(value)) {
            return false;
        }
        return true;
    } 
}
