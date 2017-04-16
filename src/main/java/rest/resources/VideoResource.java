package rest.resources;

import com.teachoversea.rest.S3Client;
import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.business.representation.POJO.ErrorPOJO;
import com.teachoversea.rest.business.representation.POJO.ResultPOJO;
import com.teachoversea.rest.dao.Mappers.ProfileDao;
import com.teachoversea.rest.dao.Mappers.ProfileStrengthDao;
import com.teachoversea.rest.dao.Mappers.VideoDao;
import com.teachoversea.rest.dao.Objects.Job.Video;
import io.dropwizard.auth.Auth;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.skife.jdbi.v2.DBI;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by lu on 2017/2/24.
 */
@Path("/video")
@Produces(MediaType.APPLICATION_JSON)
public class VideoResource {
    private static Logger logger = Logger.getLogger(VideoResource.class);
    private final VideoDao videoDao;
    private final ProfileDao profileDao;
    private final ProfileStrengthDao profileStrengthDao;
    private S3Client s3Client;

    public VideoResource(DBI jdbi, S3Client s3Client) {
        this.videoDao = jdbi.onDemand(VideoDao.class);
        this.profileDao = jdbi.onDemand(ProfileDao.class);
        this.profileStrengthDao = jdbi.onDemand(ProfileStrengthDao.class);
        this.s3Client = s3Client;
    }

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadVideo(
            @Auth AccountPrinciple principle,
            @FormDataParam("video") InputStream uploadedInputStream) {
        try {
            long profileId = principle.getId();
            UUID uuid = UUID.randomUUID();
            String name = uuid.toString()+".webm";
            String filename = name;
            String path = writeVideoToFile(uploadedInputStream, uuid + filename,
                    profileId, 10);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("path", path);
            profileStrengthDao.video(1, profileId);
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error("VideoResource error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something wrong here"));
            return Response.ok(resultPOJO).build();
        }
    }

    private String writeVideoToFile(InputStream uploadedInputStream, String filename, long profile_id, int status) throws IOException {
        int read;
        final int BUFFER_LENGTH = 1024;
        final byte[] buffer = new byte[BUFFER_LENGTH];
        File file = new File("./src/main/resources/doc/profile");
        OutputStream out = new FileOutputStream(file);
        while ((read = uploadedInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.flush();
        out.close();
        //TODO need to be got from config.yml
        String folder = "ProfileVideo";
        s3Client.uploadFileToPublic(folder, filename, file);
        videoDao.inactiveByProfileId(profile_id);
        videoDao.insertVideo(folder+"/"+filename, false, new Timestamp((new java.util.Date()).getTime()),
                status,"random question", profile_id);

        file.delete();
        return folder+"/"+filename;
    }

    @GET
    @Path("/")
    public Response getVideo(@Auth AccountPrinciple principle){
        Video video = videoDao.getVideoByProfileID(principle.getId());
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        resultPOJO.getData().put("video", video);
        return Response.ok(resultPOJO).build();
    }
}
