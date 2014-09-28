package com.nio.pinochleserver.states;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;

/*
 * Start State
 */
public class Start implements iPinochleState {
	Pinochle mP;
	public Start(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Starting Game!");
		mP.notifyObservers();
		mP.setState(mP.getDealState());
		mP.Play(null);
	}
}
