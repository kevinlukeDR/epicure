package rest.business.representation.POJO.FormPage;

import lombok.*;
import lombok.experimental.Wither;
import org.hibernate.validator.constraints.NotEmpty;

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
public class PageThreePOJO {
    @NotEmpty
    private String uuid;
    private List<String> ageGroup;
    private List<String> subject;
}
