package com.nio.pinochleserver.pinochlegames;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.GameResponse;
import com.nio.pinochleserver.enums.Move;
import com.nio.pinochleserver.player.Player;

public interface iPinochleState {
	public GameResponse Play(JSONObject response);
}
