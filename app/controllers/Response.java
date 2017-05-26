package controllers;

import javax.xml.bind.MarshalException;

import controllers.model.DTO.ErrorDTO;
import controllers.model.AppCode;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import utils.JAXBUtils;

/**
 * List of possible responses to requests.
 * All responses are converted into a XML format.
 *
 */
public class Response extends Controller {
    
    public static Result success(Object object) {
        return toXML(200, object);
    }
    
    public static Result noSuchUpload() {
    	Logger.error("Required parameter 'uploadId' is not found");
        return toXML(404, new ErrorDTO(AppCode.NO_SUCH_UPLOAD, "Required parameter 'uploadId' is not found", ""));
    }
    
    public static Result internalError(String msg, String id) {
    	Logger.error(msg + ": " + id);
        return toXML(500, new ErrorDTO(AppCode.INTERNAL_ERROR, msg, id));
    }
    
    public static Result unexpectedContent(String msg, String id) {
    	Logger.error(msg + ": " + id);
        return toXML(400, new ErrorDTO(AppCode.UNEXPECTED_CONTENT, msg, id));
    }
    
    public static Result invalidPart(String msg, String id) {
    	Logger.error(msg + ": " + id);
        return toXML(400, new ErrorDTO(AppCode.INVALID_PART, msg, id));
    }
    
    public static Result noSuchKey(String msg, String id) {
    	Logger.error(msg + ": " + id);
        return toXML(404, new ErrorDTO(AppCode.NO_SUCH_KEY, msg, id));
    }
    
    public static Result operationAborted(String msg, String id) {
    	Logger.error(msg + ": " + id);
        return toXML(409, new ErrorDTO(AppCode.OPERATION_ABORTED, msg, id));
    }
    
    private static Result toXML(int status, Object object) {
        String output;
        try {
            output = JAXBUtils.marshallToString(object);

        } catch (MarshalException e) {
        	Logger.error("Unable to marshal object to stream: " + e.getMessage());
            return internalServerError("Unable to marshal object to stream " + e);
        }
        catch (Exception e) {
            Logger.error("toXML " + e.getMessage());
            return internalServerError("toXML " + e);
        }

        return status(status, output).as("application/xml");
    }
    
}
