package com.nio.pinochleserver.helperfunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import naga.NIOSocket;

import org.json.JSONException;
import org.json.JSONObject;

import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.player.Player;

public class PinochleMessage {
	
	public PinochleMessage() {}

	public Map<NIOSocket, JSONObject> updateGetMap(Pinochle pinochle) {
		Map<NIOSocket, JSONObject> playerMSG = new HashMap<NIOSocket, JSONObject>();
		
		//** METADATA FOR EVERYONE
		try {
			
			//** PLAYER RELEVANT INFO , "Cards", "MyPosition", "MyTurn"
			for (Player player : pinochle.getPlayers()) {
				JSONObject pinochleMessage = new JSONObject();
				pinochleMessage
				.put("team1Score", pinochle.getTeam1Score())
				.put("team2Score", pinochle.getTeam2Score())
				.put("currentTurn", pinochle.getCurrentTurn())
				.put("currentRequest", pinochle.getCurrentRequest())
				.put("lastMove", pinochle.getLastMove())
				.put("currentState", pinochle.getCurrentState())
				.put("currentMessage", pinochle.getCurrentMessage());
				pinochleMessage.put("Cards", pinochle.getjConvert().convertCardsToJSON(player.getCurrentCards()));
				pinochleMessage.put("MyPosition", player.getPosition());
				
				if(pinochle.getCurrentTurn() == player.getPosition())
					pinochleMessage.put("MyTurn", true);
				else
					pinochleMessage.put("MyTurn", false);
				
				//** SAVE TO MAP	
				playerMSG.put(player.getSocket(), pinochleMessage);
			}
		} catch (JSONException e) {
			playerMSG = null;
			e.printStackTrace();
		}
		
		//** RETURN MAP
		return playerMSG;
	}
}
