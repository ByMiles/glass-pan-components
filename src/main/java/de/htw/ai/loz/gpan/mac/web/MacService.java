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
        ConfirmationResult result =
                macBroker.startAMac(macId);
        if (result == ConfirmationResult.SUCCESS)
            return Response.ok(macId).build();
        return Response.status(result.getResponseStatus()).entity(result.name()).build();
    }

    @Override
    @Path("{channel}/{panId}/{address}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postFrameCommands(
            @PathParam("channel") int logicalChannel, @PathParam("panId") int panId, @PathParam("address") int address, MacDataCmd[] cmds) {
            String macId = logicalChannel + "/" + panId + "/" + address;
            ConfirmationResult result = macBroker.postFrameCommands(macId, cmds);
            if (result == ConfirmationResult.SUCCESS)
                return Response.ok().entity(result.name()).build();
            else
                return Response.status(result.getResponseStatus()).entity(result.name()).build();
    }
}
