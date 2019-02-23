package de.htw.ai.loz.gpan.lpan.web;

import de.htw.ai.loz.gpan.lpan.msg.ComposedPacket;
import de.htw.ai.loz.gpan.lpan.msg.DataFrame;
import de.htw.ai.loz.gpan.lpan.transform.FrameToPacket;
import de.htw.ai.loz.gpan.lpan.transform.PacketToFrame;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("lpan")
public class LowPanAdaptationService {

    @Inject
    private FrameToPacket composer;

    @Inject
    private PacketToFrame fragmenter;

    @POST
    @Path("/toFrame")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postDatagramGetFrame(ComposedPacket datagram) {
        System.out.println("DATAGRAM::: " + datagram.getPayload().getPayload());
        return Response.ok(fragmenter.toCompressedFragmented(datagram)).build();
    }

    @POST
    @Path("/toPacket/{channel}/{panId}/{address}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postFrameCommands(
            @PathParam("channel") int logicalChannel, @PathParam("panId") int panId, @PathParam("address") int address, DataFrame frame) {
        String macUrl = logicalChannel + "/" + panId + "/" + address;
        if (frame.getLinkHeader() == null) {
            System.out.println("ALARM LINKHEADER NULL");
        }
        ComposedPacket datagram = composer.toComposedPacket(macUrl, frame);
        if (datagram.getLinkHeader() == null) {
            System.out.println("linkheader was null...");
            datagram.setLinkHeader(frame.getLinkHeader());
        }
        return Response.ok().entity(datagram).build();
    }
}
