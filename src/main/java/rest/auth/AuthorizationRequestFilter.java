package rest.auth;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by lu on 2017/1/24.
 */
public class AuthorizationRequestFilter implements ContainerRequestFilter {
    @Context
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;
    private static Logger logger = Logger.getLogger(AuthorizationRequestFilter.class);
    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {

        logger.info(servletRequest.getRemoteAddr());
        logger.info(servletRequest.getRequestURL());
        logger.info(servletRequest.getHeaderNames());

        logger.info(requestContext.getUriInfo().getBaseUri());

//        final SecurityContext securityContext =
//                requestContext.getSecurityContext();
//        if (securityContext == null ||
//                !securityContext.isUserInRole("privileged")) {
//
//            requestContext.abortWith(Response
//                    .status(Response.Status.UNAUTHORIZED)
//                    .entity("User cannot access the resource.")
//                    .build());
//        }
    }
}
