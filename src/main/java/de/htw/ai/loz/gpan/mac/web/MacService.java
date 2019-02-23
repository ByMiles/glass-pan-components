package de.htw.ai.loz.gpan.mac.web;

import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.broker.MacBroker;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.msg.MacId;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("mac")
public class MacService implements de.htw.ai.loz.gpan.mac.broker.MacService {

    @Inject
    MacBroker macBroker;

    public MacService() {
    }

    @GET
    @Produces (MediaType.APPLICATION_JSON)
    public Response getAllMacIds(){
        MacId[] ids = macBroker.getAllMacIds();
        return Response.ok().entity(ids).build();
    }

    @POST
    @Path("start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response tryStart(MacId macId) {
        System.out.println("START: " + macId.toString());
        ConfirmationResult result =
                macBroker.startAMac(macId);
        if (result == ConfirmationResult.SUCCESS)
            return Response.ok(macId).build();
        System.out.println("START: FAILED " + result.name());
        return Response.status(result.getResponseStatus()).entity(result.name()).build();
    }

    @Override
    @Path("{channel}/{panId}/{address}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postFrameCommands(
            @PathParam("channel") int logicalChannel, @PathParam("panId") int panId, @PathParam("address") int address, MacDataCmd[] cmds) {
            String macId = logicalChannel + "/" + panId + "/" + address;
        System.out.println("POST: " + macId);
            ConfirmationResult result = macBroker.postFrameCommands(macId, cmds);
        System.out.println("... " + result.name());
            if (result == ConfirmationResult.SUCCESS)
                return Response.ok().entity(result.name()).build();
            else
                return Response.status(result.getResponseStatus()).entity(result.name()).build();
    }
}
