package com.nio.pinochleserver.states;

import org.json.JSONObject;

import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;

public class Pause implements iPinochleState {
	static boolean gameHasStarted;
	
	Pinochle mP;
	public Pause(Pinochle p){
		this.mP = p;
		gameHasStarted = false;
	}
	@Override
	public void Play(JSONObject response) {
		if(mP.gameFull()) {
			gameHasStarted = true;
			mP.setCurrentMessage("Game is about to start...");
			mP.notification();
			mP.setState(mP.getStartState());
			mP.Play(null);
		}
		else {
			int playersNeeded = 4 - mP.getPlayers().size();
			if(gameHasStarted)
				mP.setCurrentMessage("**PAUSED - RESTARTING ROUND** Waiting for " + playersNeeded + " more player(s)");
			else
				mP.setCurrentMessage("Waiting for " + playersNeeded + " more player(s) to start game");
			mP.notification();
		}
	}
}
