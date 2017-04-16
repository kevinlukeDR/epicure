package rest;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lu on 2017/1/3.
 */
public class S3Client {
    private static String accessKey;
    private static String secretKey;
    private static AmazonS3 s3client;
    private static String privatebucketName;
    private static String publicbucketName;
    private static String privatevideobucketName;
    private static AWSCredentials credentials;
    private static final String SUFFIX = "/";
    public S3Client(String accessKey, String secretKey, String privatebucketName, String publicbucketName,
                    String privatevideobucketName){
        S3Client.accessKey = accessKey;
        S3Client.secretKey = secretKey;
        S3Client.privatebucketName = privatebucketName;
        S3Client.privatevideobucketName = privatevideobucketName;
        S3Client.publicbucketName = publicbucketName;
        credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3client = new AmazonS3Client(credentials);
    }

//    public void createFolder(String folderName) {
//        // create meta-data for your folder and set content-length to 0
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(0);
//        // create empty content
//        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
//        // create a PutObjectRequest passing the folder name suffixed by /
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
//                folderName + SUFFIX, emptyContent, metadata);
//        // send request to S3 to create folder
//        s3client.putObject(putObjectRequest);
//    }

    public void uploadFileToPublic(String folder, String filename, File file){
        String fileName = folder + SUFFIX + filename;
        PutObjectResult result = s3client.putObject(new PutObjectRequest(publicbucketName, fileName,file));
        System.out.println(result.toString());
    }

    public void uploadFileToPrivate(String folder, String filename, File file){
        String fileName = folder + SUFFIX + filename;
        PutObjectResult result = s3client.putObject(new PutObjectRequest(privatebucketName, fileName,file));
        System.out.println(result.toString());
    }

    public void uploadFileToPrivateVideo(String folder, String filename, File file){
        String fileName = folder + SUFFIX + filename;
        PutObjectResult result = s3client.putObject(new PutObjectRequest(privatevideobucketName, fileName,file));
        System.out.println(result.toString());
    }

    public S3Object getObject(String path){
        S3Object object = s3client.getObject(
                new GetObjectRequest("esl-private", path));
        return object;
    }


    public Map<String,String> getObjects(String bucketName, String folderKey) throws IOException {
        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest()
                        .withBucketName(bucketName)
                        .withPrefix(folderKey + "/");
        List<String> keys = new ArrayList<>();
        Map<String, String> s3objects = new HashMap<>();
        ObjectListing objects = s3client.listObjects(listObjectsRequest);
        for (;;) {
            List<S3ObjectSummary> summaries = objects.getObjectSummaries();
            if (summaries.size() < 1) {
                break;
            }
            summaries.forEach(s -> keys.add(s.getKey()));
            objects = s3client.listNextBatchOfObjects(objects);
        }
        int count = 0;
        for(String key : keys){
            String[] temp = key.split(folderKey+"/");
            if(temp.length==0)
                continue;
            String uuid = temp[1].substring(temp[1].length()-41, temp[1].length()-5);
            S3Object object = s3client.getObject(
                    new GetObjectRequest(bucketName, key));
            InputStream inputStream = object.getObjectContent();
            String StringFromInputStream = IOUtils.toString(inputStream);
            s3objects.put(uuid,StringFromInputStream);
            object.close();
            System.out.println(count);

            count++;
        }
        return s3objects;
    }

//    public String getBucket(){
//        List<Bucket> buckets = s3client.listBuckets();
//
//        return buckets.get(1).getName();
//    }
}

