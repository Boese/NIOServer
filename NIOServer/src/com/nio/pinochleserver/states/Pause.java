package com.nio.pinochleserver.states;

import org.json.JSONObject;

import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;

public class Pause implements iPinochleState {
	Pinochle mP;
	public Pause(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		if(mP.gameFull()) {
			mP.setCurrentMessage("Game is about to start...");
			mP.notification();
			mP.setNewBidTurn(true);
			mP.setState(mP.getStartState());
			mP.Play(null);
		}
		else {
			int playersNeeded = 4 - mP.getPlayers().size();
			mP.setCurrentMessage("**PAUSED** Waiting for " + playersNeeded + " players");
			mP.notification();
		}
	}
}
