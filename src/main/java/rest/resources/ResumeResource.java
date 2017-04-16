package rest.resources;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.teachoversea.rest.S3Client;
import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.business.representation.POJO.ErrorPOJO;
import com.teachoversea.rest.business.representation.POJO.ResultPOJO;
import com.teachoversea.rest.dao.Mappers.ProfileDao;
import com.teachoversea.rest.dao.Mappers.ProfileStrengthDao;
import com.teachoversea.rest.dao.Mappers.ResumeDao;
import com.teachoversea.rest.dao.Objects.Job.Profile;
import com.teachoversea.rest.dao.Objects.Job.Resume;
import com.teachoversea.rest.util.PDFUtil.DocToPDFConverter;
import com.teachoversea.rest.util.PDFUtil.DocxToPDFConverter;
import io.dropwizard.auth.Auth;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.skife.jdbi.v2.DBI;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;


/**
 * Created by lu on 2017/2/5.
 */
@Path("/resume")
public class ResumeResource {
    private static Logger logger = Logger.getLogger(ResumeResource.class);
    private final ResumeDao resumeDao;
    private final ProfileStrengthDao profileStrengthDao;
    private S3Client s3Client;
    private final ProfileDao profileDao;

    public ResumeResource(DBI jdbi, S3Client s3Client) {
        this.profileStrengthDao = jdbi.onDemand(ProfileStrengthDao.class);
        this.resumeDao = jdbi.onDemand(ResumeDao.class);
        this.s3Client = s3Client;
        this.profileDao = jdbi.onDemand(ProfileDao.class);
    }

    //TODO need parser to generate information
    @PUT
//    @RolesAllowed("CANDIDATE")
    @Path("/update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(
            @Auth AccountPrinciple accountPrinciple,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail){
        // TODO: uploadFileLocation should come from config.yml
        // save it
        try {
            long profile_id = accountPrinciple.getId();
            String filename = fileDetail.getFileName();
            Profile profile = profileDao.findById(profile_id);
            if (!fileDetail.getFileName().equals("")) {
                UUID uuid = UUID.randomUUID();
                String ext = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
                filename = String.valueOf(profile.getFname().toUpperCase().charAt(0))+"."+String.valueOf(profile.getLname().toUpperCase().charAt(0))+"'s Resume.pdf";
                long resume_id;
                if (ext.equals("pdf")) {
                    resume_id = writeToFile(uploadedInputStream, uuid + filename, profile_id, 30);
                } else if (ext.equals("doc")) {
                    resume_id = convertToPdf(uploadedInputStream, uuid + filename, profile_id, 30, "doc");

                } else if (ext.equals("docx")) {
                    resume_id = convertToPdf(uploadedInputStream, uuid + filename, profile_id, 30, "docx");

                }
//                else if (ext.equals("docx")) {
//                    resume_id = writeConvertToFile(uploadedInputStream, uuid + filename, profile_id, 30,"docx");
//
//                } else if(ext.equals("txt")){
//                    resume_id = writeConvertToFile(uploadedInputStream, uuid + filename, profile_id, 30,"txt");
//
//                }
                else {
                    ErrorPOJO errorPOJO = new ErrorPOJO("NOSU-PPOR-TFIL",
                            "This type of file is not supported");
                    ResultPOJO resultPOJO = new ResultPOJO("error");
                    resultPOJO.getMetadata().put("error", errorPOJO);
                    return Response.ok(resultPOJO).build();
                }
                if (resume_id == 0) {
                    ErrorPOJO errorPOJO = new ErrorPOJO("MAXF-ILE-SIZE",
                            "Your file size is bigger than 2MB");
                    ResultPOJO resultPOJO = new ResultPOJO("error");
                    resultPOJO.getMetadata().put("error", errorPOJO);
                    return Response.ok(resultPOJO).build();
                }
                ResultPOJO resultPOJO = new ResultPOJO();
                resultPOJO.getMetadata().put("status", "ok");
                Resume resume = resumeDao.findById(resume_id);
                resultPOJO.getMetadata().put("resumeId", resume_id);
                resultPOJO.getData().put("resume", resume);
                profileStrengthDao.resume(1, profile_id);
                return Response.ok(resultPOJO).build();
            } else {
                ErrorPOJO errorPOJO = new ErrorPOJO("NOFI-LEUP-LOAD",
                        "You need upload a file first");
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", errorPOJO);
                return Response.ok(resultPOJO).build();
            }
        }catch (Exception e){
            logger.error("ResumeResource error; Message:"+e.getMessage(), e);
            ErrorPOJO errorPOJO = new ErrorPOJO("CONV-ERTE-RROR",
                    "This file is under a converting error");
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", errorPOJO);
            return Response.ok(resultPOJO).build();
        }
    }
    // save uploaded file to new location
    private long writeToFile(InputStream uploadedInputStream, String filename, long profile_id, int status) throws IOException {
        int read;
        final int BUFFER_LENGTH = 1024;
        final byte[] buffer = new byte[BUFFER_LENGTH];
        UUID uuid = UUID.randomUUID();
        File file = new File("./src/main/resources/doc/"+uuid.toString());
        OutputStream out = new FileOutputStream(file);
        while ((read = uploadedInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.flush();
        out.close();
        int test = (int) Math.floor(file.length()*0.001);

        if(test > 2048){
            return 0;
        }
        if(resumeDao.findLatestById(profile_id)!=null)
            resumeDao.setStatus(10, resumeDao.findLatestById(profile_id).getId());
        //TODO need to be got from config.yml
        String folder = "Resume";
        s3Client.uploadFileToPrivate(folder, filename, file);
        int version = resumeDao.findMaxVersionById(profile_id);
        resumeDao.updateResume(folder+"/"+filename, status,version+1,
                profile_id);
        file.delete();
        Resume resume = resumeDao.findLatestById(profile_id);
        return resume.getId();
    }

    //upload doc
//    @Transaction
//    private long writeConvertToFile(InputStream uploadedInputStream, String filename, long profile_id, int status, String type) throws Exception {
//        int read;
//        final int BUFFER_LENGTH = 1024;
//        final byte[] buffer = new byte[BUFFER_LENGTH];
//        UUID src = UUID.randomUUID();
//        UUID dest = UUID.randomUUID();
//        File file = new File("./src/main/resources/doc/"+src);
//        OutputStream out = new FileOutputStream(file);
//        while ((read = uploadedInputStream.read(buffer)) != -1) {
//            out.write(buffer, 0, read);
//        }
//        out.flush();
//        out.close();
//        int test = (int) Math.floor(file.length()*0.001);
//
//        File pdf = new File("./src/main/resources/doc/"+dest+".pdf");
//
//        InputStream in = new BufferedInputStream(new FileInputStream("./src/main/resources/doc/"+src));
//        //TODO How to use local converter in AWS
//        IConverter converter = LocalConverter.builder()
//                .baseFolder(new File("./src/main/resources/doc/"))
//                .workerPool(20, 25, 2, TimeUnit.SECONDS)
//                .processTimeout(5, TimeUnit.SECONDS)
//                .build();
//        Future<Boolean> conversion;
//        if(type.equals("docx")) {
//            conversion = converter
//                    .convert(in).as(DocumentType.DOCX)
//                    .to(pdf).as(DocumentType.PDF)
//                    .prioritizeWith(1000)
//                    .schedule();
//        }
//        else if(type.equals("doc")) {
//            conversion = converter
//                    .convert(in).as(DocumentType.DOC)
//                    .to(pdf).as(DocumentType.PDF)
//                    .prioritizeWith(1000)
//                    .schedule();
//        }
//        else  {
//            conversion = converter
//                    .convert(in).as(DocumentType.TEXT)
//                    .to(pdf).as(DocumentType.PDF)
//                    .prioritizeWith(1000)
//                    .schedule();
//        }
//
//        if(test > 2048){
//            return 0;
//        }
//        if(resumeDao.findLatestById(profile_id)!=null)
//            resumeDao.setStatus(10, resumeDao.findLatestById(profile_id).getId());
//        //TODO need to be got from config.yml
//        String folder = "Resume";
//        while(!conversion.isDone()){
//
//        }
//        filename = filename.replaceAll("."+type,".pdf");
//        s3Client.uploadFileToPrivate(folder, filename, pdf);
//        int version = resumeDao.findMaxVersionById(profile_id);
//        resumeDao.updateResume(folder+"/"+filename, status,version+1,
//                profile_id);
//        Resume resume = resumeDao.findLatestById(profile_id);
//        pdf.delete();
//        file.delete();
//        converter.shutDown();
//        return resume.getId();
//    }

    private long convertToPdf(InputStream uploadedInputStream, String filename, long profile_id, int status, String type) throws Exception {
        int read;
        final int BUFFER_LENGTH = 1024;
        final byte[] buffer = new byte[BUFFER_LENGTH];
        UUID src = UUID.randomUUID();
        UUID dest = UUID.randomUUID();
        File file = new File("./src/main/resources/doc/"+src.toString());
        OutputStream out = new FileOutputStream(file);
        while ((read = uploadedInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

        out.flush();
        out.close();
        int test = (int) Math.floor(file.length()*0.001);

        if(test > 2048){
            return 0;
        }
        File pdf = new File("./src/main/resources/doc/"+dest.toString()+".pdf");
        OutputStream pdfout = new FileOutputStream(pdf);
        if(type.equals("doc")) {
            DocToPDFConverter converter = new DocToPDFConverter(new FileInputStream(new File("./src/main/resources/doc/" + src.toString())), pdfout, true, true);
            converter.convert();
        }
        else {
            DocxToPDFConverter converter = new DocxToPDFConverter(new FileInputStream(new File("./src/main/resources/doc/" + src.toString())), pdfout, true, true);
            converter.convert();
        }
        if(resumeDao.findLatestById(profile_id)!=null)
            resumeDao.setStatus(10, resumeDao.findLatestById(profile_id).getId());
        //TODO need to be got from config.yml
        String folder = "Resume";
        s3Client.uploadFileToPrivate(folder, filename, pdf);
        int version = resumeDao.findMaxVersionById(profile_id);
        resumeDao.updateResume(folder+"/"+filename, status,version+1,
                profile_id);
        pdfout.close();
        file.delete();
        pdf.delete();
        Resume resume = resumeDao.findLatestById(profile_id);
        return resume.getId();
    }


    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveResume(@Auth AccountPrinciple accountPrinciple) throws IOException {
        try {
            long profile_id = accountPrinciple.getId();
            Resume resume = resumeDao.findLatestById(profile_id);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            Map<String, Object> map;
            String name;
            String[] names;
            map = new HashMap<>();
            name = resume.getPath();
            map.put("id", resume.getId());
            names = name.split("/");
            name = names[names.length - 1].substring(36);
            map.put("name", name);
            map.put("date", resume.getSetDate());
            map.put("status", resume.getStatus());
            resultPOJO.getMetadata().put("name",name);
            resultPOJO.getData().put("latest", resume);
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            ErrorPOJO errorPOJO = new ErrorPOJO("SOME-THIN-GERR",
                    "Something wrong in database");
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", errorPOJO);
            return Response.ok(resultPOJO).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@Auth AccountPrinciple accountPrinciple,
                            @PathParam("id") long id) throws IOException {
        long profile_id = accountPrinciple.getId();
        List<Long> ids = resumeDao.findAllIdById(profile_id);
        if(!ids.contains(id)){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("UNAU-THOR-IZED", "" +
                    "This source is not yours"));
            return Response.ok(resultPOJO).build();
        }
        else{
            String path = resumeDao.findById(id).getPath();
            S3Object s3Object = s3Client.getObject(path);
            byte[] test = IOUtils.toByteArray(s3Object.getObjectContent());
            test = Base64.getEncoder().encode(test);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("resume",test);
            String[] split = s3Object.getKey().split("/");
//            byte[] filename = Base64.getEncoder().encode((split[split.length-1].substring(36)).getBytes("UTF-8"));

            String filename = split[split.length-1].substring(36);
            return Response.ok(test, "application/pdf").header("content-disposition","attachment; filename ="
                    + URLEncoder.encode(filename, "UTF-8"))
                    .header("Accept-Ranges","bytes").build();
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResume(@Auth AccountPrinciple accountPrinciple) throws IOException {
        long profile_id = accountPrinciple.getId();
        List<Long> ids = resumeDao.findAllIdById(profile_id);
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        Map<String, Object> map;
        List<Resume> resumes = new ArrayList<>();
        Resume resume;
        String name;
        String[] names;
        List<Map<String, Object>> res = new ArrayList<>();
        for(long id: ids){
//            map = new HashMap<>();
//            resume = resumeDao.findById(id);
//            name = resume.getPath();
//            map.put("id", id);
//            names = name.split("/");
//            name = names[names.length-1].substring(36);
//            map.put("name", name);
//            map.put("date", resume.getSetDate());
//            map.put("path", resume.getPath());
//            map.put("status", resume.getStatus());
//            res.add(map);

            resumes.add(resumeDao.findById(id));
        }
        resultPOJO.getMetadata().put("total", ids.size());
        resultPOJO.getData().put("resumes", resumes);
        return Response.ok(resultPOJO).build();
    }

    @PUT
    @Path("/inactive/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response inactiveResume(@Auth AccountPrinciple accountPrinciple,
                                  @PathParam("id") long id){
        long profile_id = accountPrinciple.getId();
        List<Long> ids = resumeDao.findAllIdById(profile_id);
        if(!ids.contains(id)){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("UNAU-THOR-IZED", "" +
                    "This source is not yours"));
            return Response.ok(resultPOJO).build();
        }
        else{
            resumeDao.setStatus(20, id);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            return Response.ok(resultPOJO).build();
        }
    }

    @PUT
    @Path("/latest/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeResume(@Auth AccountPrinciple accountPrinciple,
                                   @PathParam("id") long id){
        long profile_id = accountPrinciple.getId();
        List<Long> ids = resumeDao.findAllIdById(profile_id);
        if(!ids.contains(id)){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("UNAU-THOR-IZED", "" +
                    "This source is not yours"));
            return Response.ok(resultPOJO).build();
        }
        else{
            resumeDao.setStatus(10, resumeDao.findLatestById(profile_id).getId());
            resumeDao.setStatus(30, id);
            ResultPOJO resultPOJO = new ResultPOJO("ok");

            return Response.ok(resultPOJO).build();
        }
    }

}
