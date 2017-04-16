package rest.business.representation.POJO.FormPage;

import lombok.*;
import lombok.experimental.Wither;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by lu on 2017/3/31.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Wither
public class PageFourPOJO {
    @NotEmpty
    private String uuid;

    private boolean visa;

    @NotNull
    private Integer salaryMin;

    @NotNull
    private Integer salaryMax;

    private List<String> location;

    private boolean family;
    private String spouse;
}
