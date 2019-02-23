/*
 * Copyright (c) 2016-2017, Jeffrey Hope
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ninetynine;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import ninetynine.exceptions.CardNotHeldException;

/**
 *
 * @author Jeffrey Hope
 */
public class HumanPlayer extends Player {
    
    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public void bid(CardSuit trump) throws CardNotHeldException, IOException {
    	Scanner KEYBOARD = new Scanner(System.in);
    	Card[] bidCards = new Card[3];
    	System.out.println("Please select 3 cards to bid.");
    	String cardNumString;
    	int cardnum;
     	Card[] myHand = super.getHand().toArray(new Card[super.getHand().size()]);
        Arrays.sort(myHand);
    	System.out.println(GameWriter.cardsToNumberedString(myHand));   	
    	
    	for (int i=0; i<3; i++) {            
    		System.out.print("Select a card: ");
    		cardNumString = KEYBOARD.nextLine()  ;
    		cardnum = Integer.parseInt(cardNumString) ;
    		while (cardnum < 0 || cardnum >= myHand.length) {
    			System.out.print("Invalid card. Please pick again: ");
        		cardNumString = KEYBOARD.nextLine()  ;
        		cardnum = Integer.parseInt(cardNumString) ;
    		}
    		for (int j=0; j<=i; j++) {
    			while( cardnum < 0 || cardnum >= myHand.length || bidCards[j]==myHand[cardnum] ) {
    				System.out.print("Invalid card. Please pick again: ");
    	    		cardNumString = KEYBOARD.nextLine()  ;
    	    		cardnum = Integer.parseInt(cardNumString) ;
    			}
    		}
    		bidCards[i] = myHand[cardnum];  		
    	}
    	
        placeBid(bidCards);
        //KEYBOARD.close();
    }

    @Override
    public boolean wantsToDeclare() {
        return false;
    }

    @Override
    public boolean wantsToReveal() {
        return false;
    }

    @Override
    public Card selectCard(Card[] cardsPlayed, CardSuit trump) throws IOException {
    	Scanner KEYBOARD = new Scanner(System.in);
    	Card[] myHand = super.getHand().toArray(new Card[super.getHand().size()]);
    	Arrays.sort(myHand);
		System.out.println(GameWriter.cardsToNumberedString(myHand));

		int cardnum;
		System.out.print("Select a card: ");
		String cardNumString = KEYBOARD.nextLine() ;
		
		cardnum = Integer.parseInt(cardNumString) ;
		while(cardnum < 0 || cardnum >= myHand.length) {
			System.out.print("Invalid card. Please select again");
			cardNumString = KEYBOARD.nextLine() ;			
			cardnum = Integer.parseInt(cardNumString) ;
		}
		
		//KEYBOARD.close();
		return myHand[cardnum];
    }

    @Override
    public String getPlayerStrategy() {
        return "Human";
    }
}
