package rest.util.Email;

import lombok.*;
import lombok.experimental.Wither;

/**
 * Created by lu on 2017/2/27.
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Wither
public class EmailYAML {
    private String account;
    private String password;
    private String subject;
    private String template;
    private String replyto;
}
