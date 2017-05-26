package global;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import actors.UploadToS3Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import controllers.Response;
import controllers.model.DBType;
import play.*;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import java.lang.reflect.Method;

public class GlobalConfig extends GlobalSettings {
    @Override
    public void onStart(Application arg0) {
        super.onStart(arg0);
        Configuration configuration = Play.application().configuration();
        GlobalParams.UPLOAD_SCHEDULE_SECONDS = configuration.getInt("UPLOAD_SCHEDULE_SECONDS");
        GlobalParams.LIMIT_PROCCESS_FILES    = configuration.getInt("UPLOAD_SCHEDULE_SECONDS");
        GlobalParams.DB_TYPE                 = DBType.getInstanceByName(Play.application().configuration().getString("DB_TYPE"));
        GlobalParams.AWS_S3_BUCKET_PATH      = GlobalParams.AWS_S3_BUCKET + "/";
        GlobalParams.AWS_S3_HOST             = configuration.getString("AWS_S3_HOST") + "/" + GlobalParams.AWS_S3_BUCKET_PATH;
        GlobalParams.UPLOAD_FOLDER           = configuration.getString("UPLOAD_FOLDER");
        GlobalParams.SEND_UPLOAD_STATUS_URL  = configuration.getString("SEND_UPLOAD_STATUS_URL");
        GlobalParams.SEND_CONVERT_STATUS_URL = configuration.getString("SEND_CONVERT_STATUS_URL");
        GlobalParams.AWS_ET_END_POINT        = configuration.getString("AWS_ET_END_POINT");
        GlobalParams.VIDEO_PIPELINE_ID       = configuration.getString("VIDEO_PIPELINE_ID");

        Object presets = configuration.getObject("VIDEO_PRESETS");
        if(presets instanceof  HashMap) {
            GlobalParams.VIDEO_PRESETS = (HashMap<String, String>) presets;
        }

        //Launch manager to keep track of uploaded files and sending them to the S3 server
        ActorRef uploadActor = Akka.system().actorOf(Props.create(UploadToS3Actor.class));
        Akka.system().scheduler().schedule(
                Duration.create(0, TimeUnit.MILLISECONDS),
                Duration.create(GlobalParams.UPLOAD_SCHEDULE_SECONDS, TimeUnit.SECONDS),
                uploadActor,
                "upload",
                Akka.system().dispatcher(),
                null
        );
        Logger.info("S3Proxy app path: " + Play.application().path().getAbsolutePath());
    }

    @Override
    public Action onRequest(Http.Request request, Method actionMethod) {
        return super.onRequest(request, actionMethod);
    }

    @Override
    public F.Promise<Result> onError(Http.RequestHeader requestHeader, Throwable throwable)
    {
        Logger.error("Error: " + throwable.getMessage());
        return F.Promise.<Result>pure(Response.internalError("InternalError", "error"));
    }

    @Override
    public  F.Promise<Result> onBadRequest(Http.RequestHeader uri, String typeError) {
        Logger.error("Bad request: " + typeError);
        return F.Promise.<Result>pure(Response.invalidPart("Invalid part", "bad"));
    }
}
