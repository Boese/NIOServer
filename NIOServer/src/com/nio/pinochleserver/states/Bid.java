package com.nio.pinochleserver.states;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.iPinochleState;

public class Bid implements iPinochleState{
	private List<Position> bidders;
	private ListIterator<Position> biddersIterator;
	private int i = 0;
	
	Pinochle mP;
	public Bid(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		if(mP.getNewBidTurn()) {
			startBid(); 
			mP.setNewBidTurn(false);
			mP.setCurrentMessage("Starting bidding round with : " + mP.getCurrentTurn());
			mP.notification();
		}

		int move = mP.getjConvert().getBidFromJSON(response);
		if(move != -1)
		{
			mP.setLastMove(move);
			boolean result = bid(move);
			if(result && mP.getHighestBidder() != null) {
				mP.setNewBidTurn(true);
				mP.setCurrentMessage("Selecting Trump...");
				mP.notification();
				mP.setState(mP.getTrumpState());
				mP.Play(null);
				return;
			}
	
			else if(result && mP.getHighestBidder() == null) {
				mP.setNewBidTurn(true);
				mP.setCurrentMessage("Everyone passed! Redeal...");
				mP.notification();
				mP.setState(mP.getDealState());
				mP.Play(null);
				return;
			}
		}
		
		mP.setCurrentMessage("current bid ('0' to pass) : " + mP.getCurrentBid());
		mP.notification();
		mP.setCurrentRequest(Request.Bid);
		mP.playerRequest();
	}
	
	private void incTurn() {
		mP.setBidTurn(mP.getBidTurn().getNext(1));
	}
	
	private void startBid() {
		bidders = new ArrayList<Position>();
		for(int i=0;i<4;i++)
			bidders.add(mP.getBidTurn().getNext(1));
		biddersIterator = bidders.listIterator();
		mP.setCurrentTurn(mP.getBidTurn());
		mP.setCurrentBid(0);
		mP.setHighestBidder(null);
	}
	
	private boolean bid(int bid) {
		Position currentPosition = biddersIterator.next();
		
		// bidder passed remove bidder
		if(bid == 0) {
			biddersIterator.remove();
		}
		// bidder bid
		else if(bid > mP.getCurrentBid()) {
			mP.setCurrentBid(bid);
			mP.setHighestBidder(currentPosition);
		}
		// bid not high enough prompt again
		else {
			mP.setCurrentTurn(biddersIterator.previous());
			return false;
		}
		
		//one bidder left and at least one bid
		if(bidders.size() == 1 && mP.getCurrentBid() != 0) {
			mP.setCurrentTurn(mP.getHighestBidder());
			incTurn();
			return true;
		}
		//everyone passed
		if(bidders.size() == 0) {
			mP.setCurrentTurn(mP.getBidTurn());
			incTurn();
			return true;
		}
		
		//Check if iterator is at end of bidders
		if(!biddersIterator.hasNext())
			biddersIterator = bidders.listIterator();
		
		//set the current turn to next available bidder
		mP.setCurrentTurn(biddersIterator.next());
		biddersIterator.previous();
		return false;
	}
}
