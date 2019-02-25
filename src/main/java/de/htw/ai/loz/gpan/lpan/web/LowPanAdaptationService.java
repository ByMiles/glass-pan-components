/*
Copyright 2019 Miles Lorenz

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
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
        return Response.ok(fragmenter.toCompressedFragmented(datagram)).build();
    }

    @POST
    @Path("/toPacket/{channel}/{panId}/{address}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postFrameCommands(
            @PathParam("channel") int logicalChannel, @PathParam("panId") int panId, @PathParam("address") int address, DataFrame frame) {
        String macUrl = logicalChannel + "/" + panId + "/" + address;

        ComposedPacket datagram = composer.toComposedPacket(macUrl, frame);

        datagram.setLinkHeader(frame.getLinkHeader());

        return Response.ok().entity(datagram).build();
    }
}
