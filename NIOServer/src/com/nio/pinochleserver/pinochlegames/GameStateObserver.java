package com.nio.pinochleserver.pinochlegames;

import java.util.Map;
import org.json.JSONObject;
import naga.NIOSocket;


public interface GameStateObserver {
	public void update(NIOSocket socket, String msg);
}
