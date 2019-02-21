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

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Jeffrey Hope
 */
public abstract class Player {
    private final String name;
    private ArrayList<Card> hand;
    private Card[] cardsBid;
    private Card cardPlayed;
    private byte tricksWon;
    private short score;
    
    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>(13);
        cardsBid = new Card[3];
        tricksWon = 0;
        score = 0;
        cardPlayed = null;
    }
    
    public String getName() {
        return name;
    }
    
    public ArrayList<Card> getHand() {
        return (ArrayList<Card>) hand.clone();
    }
    
    public byte getTricksWon() {
        return tricksWon;
    }
    
    public Card getCardPlayed() {
        return cardPlayed;
    }
    
    public short getScore() {
        return score;
    }
    
    public void receiveCard(Card card) {
        hand.add(card);
    }
    
    public abstract void bid(CardSuit trump);
    
    protected void placeBid(Card[] cards) {
        if (cards.length != cardsBid.length) {
            throw new IllegalArgumentException("Wrong number of cards bid.");
        }
        
        for (int i = 0; i < cards.length; i++) {
            cardsBid[i] = cards[i];
            removeCard(cards[i]);
        }
    }
    
    public abstract boolean wantsToDeclare();
    
    public abstract boolean wantsToReveal();
    
    public Card[] getCardsBid() {
        Card[] cards = Arrays.copyOf(cardsBid, cardsBid.length);
        
        return cards;
    }
    
    public byte getTricksBid() {
        byte tricksBid = 0;
        
        for (int i = 0; i < cardsBid.length; i++) {
            tricksBid += cardsBid[i].getBidValue();
        }
        
        return tricksBid;
    }
    
    public void selectAndPlayCard(Card[] cardsPlayed, CardSuit trump) {
        playCard(selectCard(cardsPlayed, trump));
    }
    
    public abstract Card selectCard(Card[] cardsPlayed, CardSuit trump);
    
    protected void playCard(Card card) {
        if (!hand.contains(card)) {
            throw new IllegalArgumentException(name + " tried to play the "
                    + card.toString() + ", but doesn't have that card.");
        }
        
        cardPlayed = card;
        removeCard(card);
    }
    
    protected void playCard(Card card, CardSuit suitLed){ 
        if (!hand.contains(card)) {
            throw new IllegalArgumentException("You don't have that card.");
        }
        
        if (hasSuit(suitLed) && card.getSuit() != suitLed) {
            throw new IllegalArgumentException("Please follow suit.");
        }
        
        playCard(card);
    }
    
    public void awardTrick() {
        tricksWon++;
    }
    
    public void resetTricksWon() {
        tricksWon = 0;
    }
    
    public void addToScore(short score) {
        this.score += score;
    }
    
    public void subtractFromScore(short score) {
        this.score -= score;
    }
    
    public void resetScore() {
        score = 0;
    }
    
    public boolean madeBid() {
        return tricksWon == getTricksBid();
    }
    
    protected boolean hasSuit(CardSuit suit) {
        if (suit == null)
            return false;
        
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getSuit() == suit)
                return true;
        }
        
        return false;
    }
    
    private void removeCard(Card card) {
        int index = hand.indexOf(card);
        
        if (index == -1)
            throw new IllegalArgumentException("Card not held.");
        else {
            hand.remove(index);
        }
    }
    
    public abstract String getPlayerStrategy();
}
