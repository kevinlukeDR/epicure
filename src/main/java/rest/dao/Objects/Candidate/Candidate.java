package rest.dao.Objects.Candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Wither;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Created by lu on 2017/1/9.
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Wither
public class Candidate {
//    public Candidate() {
//    }
//    public Candidate(int id, String email,String fname,String lname,String status,String phone,String skype,String age,String nationalty,String location,
//                     String allocated_district,String allocated_city,String allocated_school,String gender,String education,String years,String employment,
//                     String email_status,String setup_time,String sentemail_time,String responed_time,String uuid,String source){
//        this.id = id;
//        this.email = email;
//        this.age =age;
//        this.allocated_city = allocated_city;
//        this.allocated_district = allocated_district;
//        this.allocated_school = allocated_school;
//        this.education = education;
//        this.email_status = email_status;
//        this.employment = employment;
//        this.fname = fname;
//        this.lname = lname;
//        this.sentemail_time = sentemail_time;
//        this.setup_time = setup_time;
//        this.skype = skype;
//        this.source = source;
//        this.years =years;
//        this.gender = gender;
//        this.location =location;
//        this.nationalty = nationalty;
//        this.phone = phone;
//        this.responed_time = responed_time;
//        this.uuid = uuid;
//        this.status = status;
//    }
//
//    @NonNull
//    private int id;
//    @NonNull
//    private String email, fname, lname, status, phone, skype, age, nationalty, location, allocated_district, allocated_city, allocated_school, gender, education, years, employment, email_status, setup_time, sentemail_time, responed_time, uuid, source;

    @JsonProperty("id")
    @NotNull
    private int id;

    @JsonProperty("email")
    @NotNull
    private String email;

    @JsonProperty("fname")
    @NotNull
    private String fname;

    @JsonProperty("lname")
    @NotNull
    private String lname;

    @JsonProperty("status")
    @NotNull
    private int status;

    @JsonProperty("phone")
    @NotNull
    private String phone;

    @JsonProperty("skype")
    @NotNull
    private String skype;

    @JsonProperty("age")
    @NotNull
    private String age;

    @JsonProperty("nationalty")
    @NotNull
    private String nationalty;

    @JsonProperty("location")
    @NotNull
    private String location;

    @JsonProperty("allocated_district")
    @NotNull
    private String allocatedDistrict;

    @JsonProperty("allocated_city")
    @NotNull
    private String allocatedCity;

    @JsonProperty("allocated_school")
    @NotNull
    private String allocatedSchool;

    @JsonProperty("gender")
    @NotNull
    private String gender;

    @JsonProperty("education")
    @NotNull
    private String education;

    @JsonProperty("years")
    @NotNull
    private String years;

    @JsonProperty("employment")
    @NotNull
    private String employment;

    @JsonProperty("email_status")
    @NotNull
    private String emailStatus;

    @JsonProperty("setup_time")
    @NotNull
    private Timestamp setupTime;

    @JsonProperty("sentemail_time")
    @NotNull
    private Timestamp sentemailTime;

    @JsonProperty("responded_time")
    @NotNull
    private Timestamp respondedTime;

    @JsonProperty("uuid")
    @NotNull
    private String uuid;

    @JsonProperty("source")
    @NotNull
    private String source;

}
