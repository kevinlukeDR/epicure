package rest.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by lu on 2017/2/5.
 */
public class MailUtil {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CandidateResource.class);
    private static Properties props = new Properties();
    private static TeachOverseaConfiguration configuration;
    public MailUtil(TeachOverseaConfiguration configuration){
        MailUtil.configuration = configuration;
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }
    public static void sendEmailRegistrationLink(String email, String uuid, String user, String type, String head, String where) throws MessagingException, UnsupportedEncodingException {
        EmailYAML emailYAML = configuration.getRegistrationemail();
        head = head.replaceAll("/api", "");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailYAML.getAccount(), emailYAML.getPassword());
                    }
                });
        if(type.equals("candidate"))
            type = "user";
        else
            type = "recruiter";
        String link = head+"index.html#activate/"+type+"/"+uuid+"/"+where;

        StringBuilder bodyText = new StringBuilder();

        bodyText.append("<div>")
                .append("  Dear "+user+"<br/><br/>")
                .append("  Thank you for registration. Your mail ("+email+") is under verification<br/>")
                .append("  Please click <a href=\""+link+"\">here</a> or open below link in browser<br/>")
                .append("  <a href=\""+link+"\">"+link+"</a>")
                .append("  <br/><br/>")
                .append("  Thanks,<br/><br/>")
                .append("  TeachOversea Team")
                .append("</div>");
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailYAML
                .getAccount(), "TeachOversea"));
        javax.mail.internet.InternetAddress[] replytoAddress = new javax.mail.internet.InternetAddress[1];

        replytoAddress[replytoAddress.length-1] = new javax.mail.internet.InternetAddress(emailYAML.getReplyto());
        message.setReplyTo(replytoAddress);
        //TODO may design as do not reply
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(email));
        message.setSubject(emailYAML.getSubject());
        message.setContent(bodyText.toString(), "text/html; charset=utf-8");
        Transport.send(message);
        System.out.println("Send Successful");
    }

    public static void sendEmailForgetPassword(String email, String uuid, String user, String type, String head) throws MessagingException, UnsupportedEncodingException {
        EmailYAML emailYAML = configuration.getForgetpasswordemail();

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailYAML.getAccount(), emailYAML.getPassword());
                    }
                });

        String link = head+"index.html#resetPw/"+type+"/"+uuid;

        StringBuilder bodyText = new StringBuilder();
        bodyText.append("<div>")
                .append("  Hi "+user+"<br/><br/>")
                .append("  Sorry for this inconvenience. Your mail ("+email+") is under verification " +
                        "of changing password.<br/><br/>")
                .append("  Please click <a href=\""+link+"\">here</a> or open below link in browser.<br/><br/>")
                .append("  <a href=\""+link+"\">"+link+"</a>")
                .append("  <br/><br/>")
                .append("  Best Wishes,<br/><br/>")
                .append("  TeachOversea Team")
                .append("</div>");
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailYAML.getAccount(),"TeachOversea"));
        javax.mail.internet.InternetAddress[] replytoAddress = new javax.mail.internet.InternetAddress[1];

        replytoAddress[replytoAddress.length-1] = new javax.mail.internet.InternetAddress(emailYAML.getReplyto());
        message.setReplyTo(replytoAddress);
        //TODO may design as do not reply
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(email));
        message.setSubject(emailYAML.getSubject());
        message.setContent(bodyText.toString(), "text/html; charset=utf-8");
        Transport.send(message);
    }

    public static void sendContact(String email, String uuid, String user, String type, String head, String recruiter){
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("no_reply@teachoversea.com", "AeS.Com@@..~");
                    }
                });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            EmailYAML emailYAML = configuration.getContactemail();

            message.setFrom(new InternetAddress(emailYAML.getAccount(), "TeachOversea"));

            String[] replyto = emailYAML.getReplyto().split(";");

            javax.mail.internet.InternetAddress[] replytoAddress = new javax.mail.internet.InternetAddress[replyto.length+1];
            for(int i = 0; i < replyto.length; i++){
                replytoAddress[i] = new javax.mail.internet.InternetAddress(replyto[i]);
            }
            replytoAddress[replytoAddress.length-1] = new javax.mail.internet.InternetAddress(email);
            message.setReplyTo(replytoAddress);

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
//            message.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress(
//                    "jxie@teachoversea.com"));

            // Set Subject: header field
            String subject = emailYAML.getSubject();
            subject = subject.replaceAll("FNAME",user);

            message.setSubject(subject);
            //TODO template here
            // Two parameter, First name and source
            String template = emailYAML.getTemplate();
            File directory = new File("./src/main/resources/doc/"+template);//设定为当前文件夹
            System.out.println(directory.getAbsolutePath());
            FileReader fr = new FileReader(directory.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);
            String temp = br.readLine(), text = "";
            StringBuilder sb = new StringBuilder();
            while(temp!=null){
                sb.append(temp);
                temp = br.readLine();
            }
            text = sb.toString();
            text = text.replaceAll("FNAME", user);
            text = text.replaceAll("RECRUITER", recruiter);
            message.setText(text,"utf-8", "html");
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Message: "+e.getMessage(), e);
        }
    }

    public static void sendContactUs(String name, String email, String usermessage){
        EmailYAML emailYAML = configuration.getContactUsEmail();
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailYAML.getAccount(), emailYAML.getPassword());
                    }
                });
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(emailYAML.getAccount(), "TeachOversea"));

            javax.mail.internet.InternetAddress[] replytoAddress = new javax.mail.internet.InternetAddress[1];

            replytoAddress[replytoAddress.length-1] = new javax.mail.internet.InternetAddress(email);
            message.setReplyTo(replytoAddress);

            // Set To: header field of the header.
            String to = emailYAML.getReplyto();
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
//            message.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress(
//                    "jxie@teachoversea.com"));

            // Set Subject: header field
            String subject = emailYAML.getSubject();

            message.setSubject(subject);
            //TODO template here
            // Two parameter, First name and source
            String template = emailYAML.getTemplate();
            File directory = new File("./src/main/resources/doc/"+template);//设定为当前文件夹
            System.out.println(directory.getAbsolutePath());
            FileReader fr = new FileReader(directory.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);
            String temp = br.readLine(), text = "";
            StringBuilder sb = new StringBuilder();
            while(temp!=null){
                sb.append(temp);
                temp = br.readLine();
            }

            Date date = new Date();
            text = sb.toString();
            text = text.replaceAll("NAME", name);
            text = text.replaceAll("MESSAGE", usermessage);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            text= text.replaceAll("TIME", sdf.format(date));

            message.setText(text,"utf-8", "html");
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Message: "+e.getMessage(), e);
        }
    }

    public static void sendApplyConfirm(String email, String fname, String recruiter, String joblink, String position, List<Job> jobs){
        EmailYAML emailYAML = configuration.getApplyconfirmemail();
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailYAML.getAccount(), emailYAML.getPassword());
                    }
                });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.

            message.setFrom(new InternetAddress(emailYAML.getAccount(), "TeachOversea Recruiting Team"));

            String[] replyto = emailYAML.getReplyto().split(";");

            javax.mail.internet.InternetAddress[] replytoAddress = new javax.mail.internet.InternetAddress[replyto.length+1];
            for(int i = 0; i < replyto.length; i++){
                replytoAddress[i] = new javax.mail.internet.InternetAddress(replyto[i]);
            }
            replytoAddress[replytoAddress.length-1] = new javax.mail.internet.InternetAddress(email);
            message.setReplyTo(replytoAddress);

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(
                    "recruiter@teachoversea.com"));

            // Set Subject: header field
            String subject = emailYAML.getSubject();
            subject = subject.replaceAll("FNAME",fname);

            message.setSubject(subject);
            //TODO template here
            // Two parameter, First name and source
            String template = emailYAML.getTemplate();
            File directory = new File("./src/main/resources/doc/"+template);//设定为当前文件夹
            System.out.println(directory.getAbsolutePath());
            FileReader fr = new FileReader(directory.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);
            String temp = br.readLine(), text = "";
            StringBuilder sb = new StringBuilder();
            while(temp!=null){
                sb.append(temp);
                temp = br.readLine();
            }
            text = sb.toString();
            text = text.replaceAll("FNAME", fname);
            text = text.replaceAll("RECRUITER", recruiter);
            text = text.replaceAll("JOBLINK", joblink);
            String joblinktemp = joblink.substring(0, joblink.lastIndexOf("/")+1);
            text = text.replaceAll("JPOSTION1", jobs.get(0).getPosition());
            text = text.replaceAll("JPOSTION2", jobs.get(1).getPosition());
            text = text.replaceAll("JPOSTION3", jobs.get(2).getPosition());
            text = text.replaceAll("JLINK1", joblinktemp+jobs.get(0).getId());
            text = text.replaceAll("JLINK2", joblinktemp+jobs.get(1).getId());
            text = text.replaceAll("JLINK3", joblinktemp+jobs.get(2).getId());
            text = text.replaceAll("JLOCATION1", jobs.get(0).getCity());
            text = text.replaceAll("JLOCATION2", jobs.get(1).getCity());
            text = text.replaceAll("JLOCATION3", jobs.get(2).getCity());
            text = text.replaceAll("\\bJP\\b", position);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date date = new Date(jobs.get(0).getStartDate().getTime());

            text = text.replaceAll("JAVAILABLE1", sdf.format(date));
            date = new Date(jobs.get(1).getStartDate().getTime());
            text = text.replaceAll("JAVAILABLE2", sdf.format(date));
            date = new Date(jobs.get(2).getStartDate().getTime());
            text = text.replaceAll("JAVAILABLE3", sdf.format(date));

            message.setText(text,"utf-8", "html");
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Message: "+e.getMessage(), e);
        }
    }

    public static void nextStep(String email, String fname){
        EmailYAML emailYAML = configuration.getApplicationEmail();
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailYAML.getAccount(), emailYAML.getPassword());
                    }
                });
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(emailYAML.getAccount(), "TeachOversea Recruiting Team"));

            javax.mail.internet.InternetAddress[] replytoAddress = new javax.mail.internet.InternetAddress[1];

            replytoAddress[replytoAddress.length-1] = new javax.mail.internet.InternetAddress(emailYAML.getReplyto());
            message.setReplyTo(replytoAddress);

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
//            message.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress(
//                    "jxie@teachoversea.com"));

            // Set Subject: header field
            String subject = emailYAML.getSubject();

            message.setSubject(subject);
            //TODO template here
            // Two parameter, First name and source
            String template = emailYAML.getTemplate();
            File directory = new File("./src/main/resources/doc/"+template);//设定为当前文件夹
            System.out.println(directory.getAbsolutePath());
            FileReader fr = new FileReader(directory.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);
            String temp = br.readLine(), text = "";
            StringBuilder sb = new StringBuilder();
            while(temp!=null){
                sb.append(temp);
                temp = br.readLine();
            }

            text = sb.toString();
            text = text.replaceAll("FNAME", fname);


            message.setText(text,"utf-8", "html");
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Message: "+e.getMessage(), e);
        }
    }

    public static void qualify(String email, String fname){
        EmailYAML emailYAML = configuration.getQualifiedEmail();
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailYAML.getAccount(), emailYAML.getPassword());
                    }
                });
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(emailYAML.getAccount(), "TeachOversea Recruiting Team"));

            javax.mail.internet.InternetAddress[] replytoAddress = new javax.mail.internet.InternetAddress[1];

            replytoAddress[replytoAddress.length-1] = new javax.mail.internet.InternetAddress(emailYAML.getReplyto());
            message.setReplyTo(replytoAddress);

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(
                    "recruiter@teachoversea.com"));

            // Set Subject: header field
            String subject = emailYAML.getSubject();
            subject = subject.replaceAll("FNAME", fname);
            message.setSubject(subject);
            //TODO template here
            // Two parameter, First name and source
            String template = emailYAML.getTemplate();
            File directory = new File("./src/main/resources/doc/"+template);//设定为当前文件夹
            System.out.println(directory.getAbsolutePath());
            FileReader fr = new FileReader(directory.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);
            String temp = br.readLine(), text = "";
            StringBuilder sb = new StringBuilder();
            while(temp!=null){
                sb.append(temp);
                temp = br.readLine();
            }

            text = sb.toString();
            text = text.replaceAll("FNAME", fname);


            message.setText(text,"utf-8", "html");
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            logger.error("Message: "+e.getMessage(), e);

        } catch (FileNotFoundException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Message: "+e.getMessage(), e);
        }
    }

    public static void unqualify(String email, String fname){
        EmailYAML emailYAML = configuration.getUnqualifiedEmail();
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailYAML.getAccount(), emailYAML.getPassword());
                    }
                });
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(emailYAML.getAccount(), "TeachOversea Recruiting Team"));

            javax.mail.internet.InternetAddress[] replytoAddress = new javax.mail.internet.InternetAddress[1];

            replytoAddress[replytoAddress.length-1] = new javax.mail.internet.InternetAddress(emailYAML.getReplyto());
            message.setReplyTo(replytoAddress);

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
//            message.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress(
//                    "jxie@teachoversea.com"));

            // Set Subject: header field
            String subject = emailYAML.getSubject();
            subject = subject.replaceAll("FNAME", fname);
            message.setSubject(subject);
            //TODO template here
            // Two parameter, First name and source
            String template = emailYAML.getTemplate();
            File directory = new File("./src/main/resources/doc/"+template);//设定为当前文件夹
            System.out.println(directory.getAbsolutePath());
            FileReader fr = new FileReader(directory.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);
            String temp = br.readLine(), text = "";
            StringBuilder sb = new StringBuilder();
            while(temp!=null){
                sb.append(temp);
                temp = br.readLine();
            }

            text = sb.toString();
            text = text.replaceAll("FNAME", fname);


            message.setText(text,"utf-8", "html");
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            logger.error("Message: "+e.getMessage(), e);

        } catch (FileNotFoundException e) {
            logger.error("Message: "+e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Message: "+e.getMessage(), e);
        }
    }

}
