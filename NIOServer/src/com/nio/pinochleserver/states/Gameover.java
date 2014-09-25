package com.nio.pinochleserver.states;

import org.json.JSONObject;

import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;

public class Gameover implements iPinochleState {
	Pinochle mP;
	public Gameover(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Game Over!");
		mP.notification();
		mP.gameOver();
	}
}
