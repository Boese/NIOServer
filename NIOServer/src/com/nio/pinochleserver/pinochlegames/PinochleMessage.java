package com.nio.pinochleserver.pinochlegames;

import java.util.List;

import naga.NIOSocket;

import org.json.JSONException;
import org.json.JSONObject;

import com.nio.pinochleserver.enums.JSONConvert;
import com.nio.pinochleserver.player.Player;

public class PinochleMessage {
	
	private JSONObject pinochleMessage;
	private List<Player> players;
	JSONConvert jConvert = new JSONConvert();
	
	public PinochleMessage() {
		pinochleMessage = new JSONObject();
	}
	
	public JSONObject getPinochleMessage(NIOSocket socket) {
		Player p = null;
		for (Player player : players) {
			if(player.getSocket() == socket)
				p = player;
		}
		try {
			pinochleMessage.putOpt("Cards", jConvert.convertCardsToJSON(p.getCurrentCards()));
			pinochleMessage.putOpt("MyTurn", true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return pinochleMessage;
	}
	
	public JSONObject getPinochleMessage() {
		return pinochleMessage;
	}

	public void update(Pinochle pinochle) {
		players = pinochle.players;
		
		try {
			pinochleMessage = new JSONObject()
			.put("team1Score", pinochle.team1Score)
			.put("team2Score", pinochle.team2Score)
			.put("currentTurn", pinochle.currentTurn)
			.put("currentTrump", pinochle.currentTrump)
			.put("currentRequest", pinochle.currentRequest)
			.put("lastMove", pinochle.lastMove)
			.put("currentState", pinochle.currentState)
			.put("currentBid", pinochle.currentBid)
			.put("currentMessage", pinochle.currentMessage);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
