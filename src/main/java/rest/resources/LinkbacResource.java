package rest.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.dao.DeviceStatusDao;
import com.teachoversea.rest.dao.Objects.DeviceStatus;
import com.teachoversea.rest.dao.Objects.Saying;
import io.dropwizard.auth.Auth;
import org.skife.jdbi.v2.DBI;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by Yiyu Jia on 11/3/15.
 */


@Path("/linkbac")
@Produces(MediaType.APPLICATION_JSON)
public class LinkbacResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    private final DeviceStatusDao deviceStatusDao;

    public LinkbacResource(String template, String defaultName, DBI jdbi) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
        this.deviceStatusDao = jdbi.onDemand(DeviceStatusDao.class);
    }


    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.or(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }

    @POST
    @Timed
    @Path("statusEcho/")
    @Consumes(MediaType.APPLICATION_JSON)
    public int receiveDeviceStatus(DeviceStatus status) {
        System.out.println(status.getMac());
        return 0;
    }

    @PermitAll
    //@RolesAllowed("ADMIN")
    @GET
    @Path("/{id}")
    public Response getContact(@Auth AccountPrinciple principle, @PathParam("id") int id) {
        // retrieve information about the device status with the provided id
        DeviceStatus deviceStatus = deviceStatusDao.findById(id);
        return Response
                .ok(deviceStatus)
                .build();
    }

    @POST
    public Response createDeviceStatus(DeviceStatus status) throws
            URISyntaxException {
// store the new contact
        int newDeviceStatusId = deviceStatusDao.createDeviceStatus(status.getIp(), status.getMac(), status.isScreen());
        return Response.created(new URI(String.valueOf(newDeviceStatusId))).build();
    }

    //getSchedule
    //getUserProfile
    //uploadDeviceInfo
    //getUser

}