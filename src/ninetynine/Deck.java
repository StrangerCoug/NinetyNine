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

import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author Jeffrey Hope
 */
public class Deck {
    LinkedList<Card> deck;
    private final int size;
    
    public Deck(int numPlayers) {
        if (numPlayers < 3 || numPlayers > 5) {
            throw new IllegalArgumentException("The number of players must be" +
                    " between 3 and 5.");
        }
        
        int tentativeSize = numPlayers * 12;
        
        if (numPlayers == 4)
            tentativeSize += 4;
        
        size = tentativeSize;
        
        deck = new LinkedList<>();
    }

    public LinkedList<Card> getCardOrder() {
        return (LinkedList<Card>) deck.clone();
    }
    
    public final void populateDeck() {
        CardRank[] ranks = {CardRank.ACE, CardRank.KING, CardRank.QUEEN,
            CardRank.JACK, CardRank.TEN, CardRank.NINE, CardRank.EIGHT,
            CardRank.SEVEN, CardRank.SIX, CardRank.FIVE, CardRank.FOUR,
            CardRank.THREE, CardRank.TWO, CardRank.TWELVE, CardRank.ELEVEN};
        CardSuit[] suits = {CardSuit.CLUBS, CardSuit.HEARTS, CardSuit.SPADES,
            CardSuit.DIAMONDS};
        
        for (int i = 0; i < size; i++) {
            deck.add(new Card(ranks[i/4], suits[i%4]));
        }
    }
    
    public void shuffleDeck() {
        deck.clear();
        populateDeck();
        Collections.shuffle(deck);
    }
    
    public void dealCardTo(Player player) {
        player.receiveCard(deck.pop());
    }
}
