package com.nio.pinochleserver.pinochlegames;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.GameResponse;

public interface iPinochleState {
	
	public GameResponse Play(JSONObject response);
}
