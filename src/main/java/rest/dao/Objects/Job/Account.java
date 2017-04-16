package rest.dao.Objects.Job;

import lombok.*;
import lombok.experimental.Wither;

import java.sql.Timestamp;

/**
 * Created by lu on 2017/1/13.
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Wither
public class Account {
    private long id;
    private String email;
    private String password;
    private long profile_id;
    private int status;
    private Timestamp createDate, activeDate, disableDate;
    private Timestamp lastLoginDate;
    private int loginTimes;
}
