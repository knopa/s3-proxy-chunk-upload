package plugins;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import global.GlobalParams;
import play.Logger;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import play.libs.Scala;
import scala.collection.Seq;

public class S3Module extends Module {
    public static AmazonS3 amazonS3;
    public static String s3Bucket;
    public static AWSCredentials awsCredentials;
    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        GlobalParams.AWS_ACCESS_KEY = Scala.orNull(configuration.getString("AWS_ACCESS_KEY", scala.Option.empty()));
        GlobalParams.AWS_SECRET_KEY = Scala.orNull(configuration.getString("AWS_SECRET_KEY", scala.Option.empty()));
        GlobalParams.AWS_S3_BUCKET = Scala.orNull(configuration.getString("AWS_S3_BUCKET", scala.Option.empty()));

        String accessKey = GlobalParams.AWS_ACCESS_KEY;
        String secretKey = GlobalParams.AWS_SECRET_KEY;
        s3Bucket = GlobalParams.AWS_S3_BUCKET;

        if ((accessKey != null) && (secretKey != null)) {
            awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            amazonS3 = new AmazonS3Client(awsCredentials);

            Logger.info("Using S3 Bucket: " + s3Bucket);
        }
        return seq(
                bind(S3Plugin.class).to(S3PluginImpl.class)
        );
    }
}
