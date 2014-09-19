package com.nio.pinochleserver.pinochlegames;

import java.util.ArrayList;
import java.util.List;

import naga.NIOSocket;

import org.json.JSONException;
import org.json.JSONObject;

import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.helperfunctions.CalculateMeld;
import com.nio.pinochleserver.player.Player;

public class PinochleMessage implements PinochleGameObserver {
	
	private JSONObject pinochleMessage;
	private Position currentTurn;
	private List<Player> players;
	
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
			pinochleMessage.putOpt("Cards", p.getCurrentCards());
			if(p.getPosition() == currentTurn)
				pinochleMessage.putOpt("MyTurn", true);
			else
				pinochleMessage.putOpt("MyTurn", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return pinochleMessage;
	}
	
	public void updateJSONObject() {
//		JSONObject playersMeldArray = new JSONObject();
//		for (Player player : players) {
//			int x = new CalculateMeld(currentTrump, player.getCurrentCards()).calculate();
//			try {
//				playersMeldArray.put("Player : " + player.getPosition(), x);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		try {
//			pinochleMessage.put("Meld", playersMeldArray);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void update(Pinochle pinochle) {
		currentTurn = pinochle.currentTurn;
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
