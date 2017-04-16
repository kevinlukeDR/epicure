package rest.business.representation.POJO.FormPage;

import lombok.*;
import lombok.experimental.Wither;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by lu on 2017/3/31.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Wither
public class PageTwoPOJO {
    @NotEmpty
    private String uuid;

    private String certification;

    private int hours;

    private boolean online;

    private boolean practicum;

    private boolean license;
    @NotEmpty
    private String experience;
}
