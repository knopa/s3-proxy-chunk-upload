package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import controllers.model.DTO.CompleteUploadDTO;
import controllers.model.DTO.PartDTO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import play.Logger;
/**
 * Utility for file processing, performs operations such as combine, save, remove.
 *
 */
public class IOUploadUtils {
    
    /**
     * The operation combines a few files into one.
     * @param destination Target file.
     * @param sources Array sources files.
     * @throws IOException
     */
    public static void joinFiles(File destination, List<File> sources)
            throws IOException {
        OutputStream output = null;
        try {
            output = createAppendableStream(destination);
            for (File source : sources) {
                appendFile(output, source);
            }
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
    
    /**
     * The operation copies the file in the specified path.
     * @param dirPath Target directory
     * @param fileName File name
     * @param srcFile
     * @throws IOException
     */
    public static void saveFile(String dirPath, String fileName, File srcFile)
            throws IOException {
        
        try {
            File uploadPart = new File(dirPath, fileName);
            if (uploadPart.exists()) {
                uploadPart.delete();
            }
            FileUtils.moveFile(srcFile, uploadPart);
        } catch (IOException ex) {
            String prefix = "Save file error: '";
            Logger.error(prefix + ex.getMessage() + "'");
            throw new IOException(prefix + ex.getMessage() + "'");
        }
    }

    /**
     * The operation saves combined file.
     * @param dirPath
     * @param extensionPart Extension for parts file.
     * @param s3File Target file.
     * @param completeUploadDTO Object that contains information about the parts of the file.
     * @throws IOException
     */
    public static void combinedFile(String dirPath, String extensionPart, File s3File,
            CompleteUploadDTO completeUploadDTO) throws IOException {
        try {
            List<File> parts = new ArrayList<>();
            
            File dirParts = new File(dirPath);
            if(!dirParts.exists()) {
                throw new IOException("Directory parts not found: '" + dirParts.getAbsolutePath() + "'");
            }
            for (PartDTO part : completeUploadDTO.getParts()) {
                String numberPart = part.getPartNumber();
                File file = new File(getPaths(dirPath, numberPart + extensionPart));                
                
                if (!file.exists()) {
                    throw new IOException("File not found: '" + file.getAbsolutePath() + "'");
                }
                parts.add(file);
            }           
            IOUploadUtils.joinFiles(s3File, parts);
        } catch (IOException e) {
            String prefix = "Error combinedFile: ";
            Logger.error(prefix + e);
            throw new IOException(prefix + e);
        }
    }

    /**
     * Method checks whether there are all of the files in the server storage
     * @param dirPath - File path to parent directory
     * @param extensionPart
     * @param completeUploadDTO - Object that keeps a list of parts file
     * @throws IOException
     */
    public static void checkParts(String dirPath, String extensionPart, CompleteUploadDTO completeUploadDTO) throws IOException {
        File dirParts = new File(dirPath);
        if(!dirParts.exists()) {
            throw new IOException("Directory parts not found: '" + dirParts.getAbsolutePath() + "'");
        }

        for (PartDTO part : completeUploadDTO.getParts()) {
            String numberPart = part.getPartNumber();
            File file = new File(getPaths(dirPath, numberPart + extensionPart));

            if (!file.exists()) {
                throw new IOException("File not found: '" + file.getAbsolutePath() + "'");
            }
        }
    }
    
    /**
     * The operation removes the folder and all its contents
     * @param dirPath Target path.
     * @throws IOException
     */
    public static void removeDir(String dirPath) throws IOException {
        File uploadDir = new File(dirPath);
        if (uploadDir.exists()) {
            FileUtils.deleteDirectory(uploadDir);
        }
    }
    
    /**
     * Operation connects the segments of the path of the file.
     * @param first
     * @param more
     * @return
     */
    public static String getPaths(String first, String... more) {
        return Paths.get(first, more).normalize().toString();
    }
    
    public static String getMD5(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        String md5 = DigestUtils.md5Hex(fis);
        fis.close();
        return md5;
    }

    private static BufferedOutputStream createAppendableStream(File destination)
            throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(destination, true));
    }

    private static void appendFile(OutputStream output, File source)
            throws IOException {
        try (InputStream input = new BufferedInputStream(new FileInputStream(source))) {
            IOUtils.copy(input, output);
        }
    }
}