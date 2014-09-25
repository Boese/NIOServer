package com.nio.pinochleserver.states;

import org.json.JSONObject;

import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;

public class Meld implements iPinochleState {
	Pinochle mP;
	public Meld(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Melding Cards...");
		mP.notification();
		mP.setState(mP.getRoundState());
		mP.Play(null);
	}
	
}
