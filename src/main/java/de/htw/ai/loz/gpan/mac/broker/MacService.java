package de.htw.ai.loz.gpan.mac.broker;

import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.msg.MacId;

import javax.ws.rs.core.Response;

public interface MacService {

    Response tryStart(MacId request);

    Response postFrameCommands(int logicalChannel, int panId, int address, MacDataCmd[] cmds);
}
