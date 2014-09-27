package com.nio.pinochleserver.states;

import org.json.JSONObject;

import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;

public class Round implements iPinochleState {
	Pinochle mP;
	public Round(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Playing Round...");
		mP.notifyObservers();
		mP.setCurrentState(mP.getGameoverState());
		mP.Play(null);
	}
}
