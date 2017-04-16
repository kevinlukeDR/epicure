package rest.business.representation.POJO.FormPage;

import lombok.*;
import lombok.experimental.Wither;
import org.hibernate.validator.constraints.NotEmpty;

import java.sql.Timestamp;

/**
 * Created by lu on 2017/3/31.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Wither
public class PageOnePOJO {
    @NotEmpty
    private String email;
    @NotEmpty
    private String fname;
    @NotEmpty
    private String lname;

    private String mname;

    private String skype;

    private String phone;

    @NotEmpty
    private String gender;
    @NotEmpty
    private String nationality;
    @NotEmpty
    private String birth;

    @NonNull
    private Timestamp availableDate;
    @NotEmpty
    private String major;
    @NonNull
    private Timestamp graduateDate;
    @NotEmpty
    private String language;
    @NotEmpty
    private String highestEdu;
}
