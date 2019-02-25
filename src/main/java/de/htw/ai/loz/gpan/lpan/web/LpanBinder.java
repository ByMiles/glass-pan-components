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

import de.htw.ai.loz.gpan.lpan.transform.FrameToPacket;
import de.htw.ai.loz.gpan.lpan.transform.PacketToFrame;
import de.htw.ai.loz.gpan.lpan.imp.transform.FrameComposer;
import de.htw.ai.loz.gpan.lpan.imp.transform.PacketComposer;
import org.glassfish.jersey.internal.inject.AbstractBinder;

import javax.inject.Singleton;

public class LpanBinder extends AbstractBinder {


    @Override
    protected void configure() {
        bind(PacketComposer.class).to(FrameToPacket.class).in(Singleton.class);
        bind(FrameComposer.class).to(PacketToFrame.class).in(Singleton.class);
    }
}
