package actors;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import controllers.model.ConvertStatus;
import global.GlobalParams;
import models.S3File;
import org.apache.commons.io.FilenameUtils;
import play.Logger;
import plugins.S3Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VideoConverter implements Runnable {
    private static String PIPELINE_ID;
    private static final String VIDEO_EXTENSION = ".mp4";
    private static final String STREAM = "stream";

    private String id;

    private static BasicAWSCredentials BASIC_AWS_CREDENTIALS;
    private static AmazonElasticTranscoder amazonElasticTranscoder;

    public VideoConverter(String id)
    {
        this.id = id;

        if(BASIC_AWS_CREDENTIALS == null)
        {
            PIPELINE_ID = GlobalParams.VIDEO_PIPELINE_ID;
            BASIC_AWS_CREDENTIALS = (BasicAWSCredentials) S3Module.awsCredentials;
            amazonElasticTranscoder = new AmazonElasticTranscoderClient(BASIC_AWS_CREDENTIALS);
            amazonElasticTranscoder.setEndpoint(GlobalParams.AWS_ET_END_POINT);
        }
    }

    @Override
    public void run()
    {
        try
        {
            S3File file = S3File.getFile(id);
            if(file != null) {
                String jobId = createElasticTranscoderJob(file.name);
                S3File.updateConvertStatus(file.id, ConvertStatus.PROCESSING.getType(), jobId);
            }
        }
        catch (Exception e)
        {
            Logger.error("Video convert id: " + id + ", error: " + e);
        }
    }

    /**
     * Creates a job in Elastic Transcoder using the configured pipeline, input
     * key, preset, and output key prefix.
     *
     * @return Job ID of the job that was created in Elastic Transcoder.
     * @throws Exception
     */
    private String createElasticTranscoderJob(String inputUrl) throws Exception
    {
        JobInput input = new JobInput().withKey(inputUrl);
        List<CreateJobOutput> outputs = new ArrayList<>();

        for(Map.Entry<String, String> entry : GlobalParams.VIDEO_PRESETS.entrySet()) {
            String suffix = entry.getKey();
            String preset = entry.getValue();

            String outputUrl = createSuffixUrl(inputUrl, suffix);
            if(!outputUrl.isEmpty()) {
                CreateJobOutput out = new CreateJobOutput();
                out.withKey(outputUrl);
                out.withPresetId(preset);
                if(suffix.equals(STREAM)) {
                    out.withSegmentDuration("1");
                }
                outputs.add(out);
            }
        }

        CreateJobRequest createJobRequest = new CreateJobRequest()
                .withPipelineId(PIPELINE_ID)
                .withInput(input)
                .withOutputs(outputs);
        return amazonElasticTranscoder.createJob(createJobRequest).getJob().getId();
    }

    private String createSuffixUrl(String inputUrl, String suffix) {
        String outputUrl = "";

        if(suffix.equals(STREAM)) {
            if (inputUrl.endsWith(VIDEO_EXTENSION)) {
                String name = FilenameUtils.getBaseName(inputUrl);
                String outName = name.replace(VIDEO_EXTENSION, "");
                outName = outName + '/' + STREAM;
                outputUrl = inputUrl.replace(name + VIDEO_EXTENSION, outName);
            }
        } else {
            if (inputUrl.endsWith(VIDEO_EXTENSION)) {
                outputUrl = inputUrl.replace(VIDEO_EXTENSION, suffix + VIDEO_EXTENSION);
            }
        }

        return outputUrl;
    }
}