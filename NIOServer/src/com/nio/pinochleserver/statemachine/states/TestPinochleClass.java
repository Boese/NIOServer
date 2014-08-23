package com.nio.pinochleserver.statemachine.states;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import naga.NIOService;
import naga.NIOSocket;

import com.nio.pinochleserver.Player;
import com.nio.pinochleserver.pinochledriver.PinochleDriver;
import com.nio.pinochleserver.statemachine.GameStateMachine;
import com.nio.pinochleserver.statemachine.card.Card;
import com.nio.pinochleserver.statemachine.card.Face;
import com.nio.pinochleserver.statemachine.card.Position;
import com.nio.pinochleserver.statemachine.card.Suit;

public class TestPinochleClass {

	public static void main(String[] args) throws Exception {
		PinochleDriver p = new PinochleDriver();
		p.startGame();
	}

}
