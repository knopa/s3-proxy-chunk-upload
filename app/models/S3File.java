package models;

import controllers.model.ConvertStatus;
import global.GlobalParams;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.apache.commons.io.FilenameUtils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Model;

import controllers.model.UploadStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "s3files")
@MappedSuperclass
public class S3File extends Model {

    public static final Finder<String, S3File> find = new Finder<>(S3File.class);

    @Id
    public String id;
    public String name;
    public int status;
    public int convertStatus;
    public String jobId;
    public String proccess;
    public String url;
    public Date created;
    @Column(columnDefinition = "TEXT")
    public String parts;
    public String convertResponse;

    public S3File() {}

    public S3File(String id, String name) {
       this.id = id;
       this.name = name;
       this.created = new Date();
       this.status = UploadStatus.INIT.getType();
        this.convertStatus = ConvertStatus.INIT.getType();
       this.url = GlobalParams.AWS_S3_HOST + getActualFileName();
    }

    public URL getUrl() throws MalformedURLException {
        return new URL(url);
    }
    
    public String getActualFileName() {
        return name;
    }
    
    public String getExtension() {
        return "." +  FilenameUtils.getExtension(name);
    }

    public static S3File getFile(String id) {
        return find.where().eq("id", id).findUnique();
    }


    public static S3File getFileByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

    public static S3File getFileByJob(String jobid) {
        return find.where().eq("job_id", jobid).findUnique();
    }

    public static String getFileName(String id) {
        S3File file = getFile(id);
        return file.name;
    }

    public static List<S3File> getUploadedFiles(String uniq) {
        return find.where()
                .eq("status", UploadStatus.UPLOADED.getType())
                .eq("proccess", uniq)
                .findList();
    }

    public static String getUpdateSqlByDbType() {
        String sql = "";
        switch(GlobalParams.DB_TYPE) {
            case Mysql:
                sql = "UPDATE s3files SET proccess = :uniq  WHERE status = :status and proccess is null LIMIT :qty";
                break;
            case Postgres:
                sql = "UPDATE s3files SET proccess = :uniq FROM(SELECT id from s3files WHERE status = :status and proccess is null LIMIT :qty) available WHERE s3files.id=available.id";
                break;
        }
        return sql;
    }

    public static Boolean processFiles(String uniq) {
        String sql = getUpdateSqlByDbType();
        SqlUpdate updateProcess = Ebean.createSqlUpdate(sql);
        updateProcess.setParameter("uniq", uniq);
        updateProcess.setParameter("status", UploadStatus.UPLOADED.getType());
        updateProcess.setParameter("qty", GlobalParams.LIMIT_PROCCESS_FILES);

        return Ebean.execute(updateProcess) == 1;
    }

    public static Boolean updateStatus(String id, int status) {
        String sql = "UPDATE s3files SET status=:status WHERE id=:id";
        return createStatusSQL(sql, id, status);
    }
    
    public static Boolean updateParts(String id, int status, String parts) {
        String sql = "UPDATE s3files SET status=:status";
        if(parts != null) {
            sql += " , parts='" + parts + "'";
        }
        
        sql+= "  WHERE id=:id";
        return createStatusSQL(sql, id, status);        
    }

    private static Boolean createStatusSQL(String sql, String id, int status) {
        return createStatusSQL(sql, id, status, "", "");
    }

    private static Boolean createStatusSQL(String sql, String id, int status, String jobId, String response) {
        SqlUpdate updateStatus = Ebean.createSqlUpdate(sql);
        updateStatus.setParameter("id", id);
        updateStatus.setParameter("status", status);
        if(!jobId.isEmpty()) {
            updateStatus.setParameter("job", jobId);
        }

        if(!response.isEmpty()) {
            updateStatus.setParameter("response", response);
        }

        return Ebean.execute(updateStatus) == 1;
    }

    public static Boolean updateConvertStatus(String id, int status, String jobId) {
        String sql = "UPDATE s3files SET convert_status = :status, job_id = :job WHERE id = :id";
        return createStatusSQL(sql, id, status, jobId, "");
    }

    public static Boolean updateConvertStatus(String id, int status, String jobId, String response) {
        String sql = "UPDATE s3files SET convert_status = :status, job_id = :job, convert_response = :response WHERE id = :id";
        return createStatusSQL(sql, id, status, jobId, response);
    }
}