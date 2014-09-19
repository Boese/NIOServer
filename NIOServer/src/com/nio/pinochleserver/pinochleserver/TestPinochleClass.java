package com.nio.pinochleserver.pinochleserver;

import static java.util.Arrays.asList;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.Face;
import com.nio.pinochleserver.enums.JSONConvert;
import com.nio.pinochleserver.enums.Position;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.helperfunctions.CalculateMeld;

public class TestPinochleClass {

	public static void main(String[] args) throws Exception {
//		List<Card> cards = new ArrayList<Card>();
//		cards = asList(
//				new Card(Suit.Hearts, Face.Queen),
//				new Card(Suit.Hearts, Face.King),
//				new Card(Suit.Hearts, Face.Ten),
//				new Card(Suit.Spades, Face.Nine),
//				new Card(Suit.Spades, Face.Queen),
//				new Card(Suit.Diamonds, Face.Nine),
//				new Card(Suit.Diamonds, Face.Jack),
//				new Card(Suit.Diamonds, Face.Queen),
//				new Card(Suit.Diamonds, Face.King),
//				new Card(Suit.Diamonds, Face.Ten),
//				new Card(Suit.Clubs, Face.Jack),
//				new Card(Suit.Clubs, Face.Queen)
//				);
////		
//		CalculateMeld c = new CalculateMeld(Suit.Spades, cards);
//		int x = c.calculate();
//		System.out.println(x);
		
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient("localhost");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		DB db = mongoClient.getDB( "test" );
		
		db.getCollection("testCollection").drop();
		
		DBCollection coll = db.getCollection("testCollection");
		
		BasicDBObject doc = new BasicDBObject()
        .append("username", "chris")
        .append("password", "pass")
        .append("email", "chris107565@gmail.com");
		coll.insert(doc);
		
		DBCursor d = coll.find();
		
		do {
			d.next();
			System.out.println(d.curr());
		}while(d.hasNext());
		
		String user = "chris";
		String pass = "pass";
		BasicDBObject temp = new BasicDBObject("username", user);
		temp.append("password", pass);
		
		BasicDBObject dob = new BasicDBObject("user","chris");
		System.out.println(coll.find(dob));
		
		if(coll.findOne(temp) != null)
			System.out.println("success");
		else
			System.out.println("fail");
		//JSONConvert jConvert = new JSONConvert();
//		JSONObject object = new JSONObject();
//		
//		object = jConvert.convertCardsToJSON(cards);
//		
//		System.out.println(object.toString(3));
//		
//		cards = jConvert.getCardsFromJSON(object);
//		System.out.println(cards);
		
		
		
//		System.out.println(cards);
//		System.out.println("Score : " + new CalculateMeld(Suit.Hearts,cards).calculate());
//		Move m = new Move();
//		m.setCards(null);
//		System.out.println(m.getCards());
	}
}
