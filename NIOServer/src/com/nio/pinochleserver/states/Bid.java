package com.nio.pinochleserver.states;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;
import com.nio.pinochleserver.player.PlayerResponse;

public class Bid implements iPinochleState{
	List<Position> bidders;
	Iterator<Position> bidTurn;
	Position lastBidder;
	int currentBid;
	
	Pinochle mP;
	public Bid(Pinochle p){
		this.mP = p;
		this.lastBidder = mP.getCurrentTurn();
	}
	@Override
	public void Play(JSONObject response) {
		try {
			// Try to get bid from JSONObject
			PlayerResponse playerresponse = new PlayerResponse();
			playerresponse = mP.getMapper().readValue(response.toString(), PlayerResponse.class);
			
			int bid = playerresponse.getBid();
			
			// If bid == 0, player passes. Notify and remove player from bidders. Increment turn.
			if(bid == 0) {
				mP.setCurrentMessage("Bid from player " + mP.getCurrentTurn() + " : pass");
				mP.notifyObservers();
				bidTurn.remove();
				incTurn();
			}
			
			// If bid > currentBid, set currentBid = bid. Notify players. Increment turn.
			else if(bid > currentBid) {
				currentBid = bid;
				mP.setCurrentMessage("Bid from player " + mP.getCurrentTurn() + " : " + currentBid);
				mP.notifyObservers();
				incTurn();
			}
			
			// Check if there is one bidder left and at least one bid
			if(bidders.size() == 1 && currentBid != 0) {
				lastBidder = lastBidder.getNext(1);
				mP.setCurrentTurn(bidders.get(0));
				mP.setState(mP.getTrumpState());
				mP.setCurrentMessage("Player " + mP.getCurrentTurn() + " won bid at " + currentBid + ", Selecting Trump...");
				mP.notifyObservers();
				mP.Play(null);
			}
			// Check if everyone passed
			else if(bidders.size() == 0) {
				lastBidder = lastBidder.getNext(1);
				mP.setCurrentTurn(lastBidder);
				mP.setState(mP.getDealState());
				mP.setCurrentMessage("Everyone passed! Redeal...");
				mP.notifyObservers();
				mP.Play(null);
			}
			
			// Bid not high enough
			else
				requestBid();
			
		// Invalid response from player
		} catch (Exception e) {
			requestBid();
		} 
	}
	
	private void requestBid() {
		mP.notifyObservers(Request.Bid);
	}
	
	private void incTurn() {
		//Check if iterator is at end of bidders
		if(!bidTurn.hasNext())
			bidTurn = bidders.listIterator();
		
		// Make sure bidders is not empty
		if(bidders.size() > 0)
			mP.setCurrentTurn(bidTurn.next());
	}
	
	public void startBid() {
		
		// Set current turn to last bidder. Initialize currentBid to 0.
		mP.setCurrentTurn(lastBidder);
		currentBid = 0;
		
		// Notify players that the bidding round is starting and which turn it is
		mP.setCurrentMessage("Starting bidding round with : " + mP.getCurrentTurn());
		mP.notifyObservers();
		
		// Initialize bidders list with the correct order of players starting with last bidder. Set bidTurn to bidders.listIterator
		bidders = new ArrayList<Position>();
		for(int i=0;i<4;i++)
			bidders.add(lastBidder.getNext(i));
		bidTurn = bidders.listIterator();
		bidTurn.next();
	}
}
