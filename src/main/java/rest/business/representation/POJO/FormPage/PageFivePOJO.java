package rest.business.representation.POJO.FormPage;

import lombok.*;
import lombok.experimental.Wither;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by lu on 2017/4/3.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Wither
public class PageFivePOJO {
    @NotEmpty
    private String uuid;
    @NotEmpty
    private String password;
}
