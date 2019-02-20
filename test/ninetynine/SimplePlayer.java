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
import java.util.Collections;

/**
 *
 * @author Jeffrey Hope
 */
public class SimplePlayer extends Player {
    boolean isTryingToWin;
    int numPlayers;
    int[] suitLengths;
    
    public SimplePlayer(String name) {
        this(name, 3);
    }

    public SimplePlayer(String name, int numPlayers) {
        super(name);
        isTryingToWin = true;
        this.numPlayers = numPlayers;
        suitLengths = new int[4];
    }
    
    @Override
    public void bid(CardSuit trump) {
        Card[] bidCards = new Card[3];
        int minBid = 0, maxBid;
        
        if (numPlayers == 4)
            maxBid = 10;
        else maxBid = 9;

        ArrayList<Card> hand = super.getHand();
        Card[] sortedCards = hand.toArray(new Card[hand.size()]);
        
        // Reset the suit lengths.
        for (int i = 0; i < suitLengths.length; i++)
            suitLengths[i] = 0;
        
        for (int i = 0; i < super.getHand().size(); i++) {
            suitLengths[super.getHand().get(i).getSuit().ordinal()]++;

            if (isLowCard(super.getHand().get(i)))
                maxBid--;
            else if (isHighCard(super.getHand().get(i)))
                minBid++;
        }
        
        /* Modified knapsack problem; not only is there a weight limit (maxBid);
         * there is a weight requirement (minBid) and the constraint that it
         * pick three cards.
         */
        
        sortCardsByRatio(sortedCards, 0, sortedCards.length - 1);
        bidCards = getBidCandidates(sortedCards, minBid, maxBid);
        
        if (bidCards[0] != null) {
            placeBid(bidCards);
            return;
        }
        
        /* The above search should come up with a bid in most cases; this next
         * step tests if it is reasonable to bid zero if the above doesn't find
         * anything.
         */
        
        ArrayList<Card> cardsForNull = new ArrayList<>();
        cardsForNull.add(new Card(CardRank.ACE, CardSuit.DIAMONDS));
        cardsForNull.add(new Card(CardRank.KING, CardSuit.DIAMONDS));
        cardsForNull.add(new Card(CardRank.QUEEN, CardSuit.DIAMONDS));
        cardsForNull.trimToSize();
        
        if (hand.containsAll(cardsForNull)) {
            
        }
        
        /* This is a last resort bid. It eats up lines of code and is probably
         * not memory-efficient, but I hope these lines don't end up executing
         * that often.
         */
        sortCardsByValue(sortedCards, 0, sortedCards.length - 1);
        
        int lowestPossibleBid = sortedCards[0].getBidValue() +
                sortedCards[1].getBidValue() +
                sortedCards[2].getBidValue();
        
        if (lowestPossibleBid <= 3) {
            bidCards = getBidCandidates(sortedCards, 0, 3);
            if (bidCards[0] != null) {
                placeBid(bidCards);
                return;
            }
        }
        else {
            reverseSortCardsByValue(sortedCards, 0, sortedCards.length - 1);
            bidCards = getBidCandidates(sortedCards, lowestPossibleBid,
                    lowestPossibleBid);
            placeBid(bidCards);
        }
    }
    
    private Card[] getBidCandidates(Card[] cards, int minBid, int maxBid) {
        Card[] bidCards = new Card[3];
        
        for (int i = 0; i < bidCards.length; i++)
            bidCards[i] = null;
        
        for (int i = cards.length - 1; i >= 2; i--) {
            for (int j = i - 1; j >= 1; j--) {
                for (int k = j - 1; k >= 0; k--) {
                    int tentativeBid = cards[i].getBidValue() +
                            cards[j].getBidValue() +
                            cards[k].getBidValue();
                    
                    if (tentativeBid >= minBid && tentativeBid <= maxBid) {
                        bidCards[0] = cards[i];
                        bidCards[1] = cards[j];
                        bidCards[2] = cards[k];
                    }
                }
            }
        }
        
        return bidCards;
    } 
    
    // From http://stackoverflow.com/questions/29609909/inplace-quicksort-in-java
    private void sortCardsByRatio (Card[] cards, int left, int right) {
        if (left > right) {
            int i = left, j = right;
            Card temp; 
        
            double pivotRatio = getWeightToValueRatio(cards[right/2]);
            double pivotValue = getCardBiddingValue(cards[right/2]);
        
            do {
                while (getWeightToValueRatio(cards[i]) < pivotRatio ||
                        (getWeightToValueRatio(cards[i]) == pivotRatio &&
                        getCardBiddingValue(cards[i]) < pivotValue))
                    i++;
                while (getWeightToValueRatio(cards[j]) > pivotRatio ||
                        (getWeightToValueRatio(cards[j]) == pivotRatio &&
                        getCardBiddingValue(cards[j]) > pivotValue))
                    j--;
                
                if (i <= j) {
                    temp = cards[i];
                    cards[i] = cards[j];
                    cards[j] = temp;
                    i++;
                    j--;
                }
            } while (i <= j);
            
            if (left < j)
                sortCardsByRatio(cards, left, j);
            if (i < right)
                sortCardsByRatio(cards, i, right);
        }
    }
    
    /* Similar to the above, but the bidding value and the value ratio are
     * switched.
     */
    private void sortCardsByValue (Card[] cards, int left, int right) {
        if (left > right) {
            int i = left, j = right;
            Card temp; 
        
            double pivotValue = getCardBiddingValue(cards[right/2]);
            double pivotRatio = getWeightToValueRatio(cards[right/2]);
        
            do {
                while (getCardBiddingValue(cards[i]) < pivotValue ||
                        (getCardBiddingValue(cards[i]) == pivotValue &&
                        getWeightToValueRatio(cards[i]) < pivotRatio))
                    i++;
                while (getCardBiddingValue(cards[j]) > pivotValue ||
                        (getCardBiddingValue(cards[j]) == pivotValue &&
                        getWeightToValueRatio(cards[j]) > pivotRatio))
                    j--;
                
                if (i <= j) {
                    temp = cards[i];
                    cards[i] = cards[j];
                    cards[j] = temp;
                    i++;
                    j--;
                }
            } while (i <= j);
            
            if (left < j)
                sortCardsByValue(cards, left, j);
            if (i < right)
                sortCardsByValue(cards, i, right);
        }
    }
    
    /* Similar to the above, but returns cards in highest to lowest order; ties
     * still break for highest weight-to-value ratio.
     */
    private void reverseSortCardsByValue (Card[] cards, int left, int right) {
        if (left > right) {
            int i = left, j = right;
            Card temp; 
        
            double pivotValue = getCardBiddingValue(cards[right/2]);
            double pivotRatio = getWeightToValueRatio(cards[right/2]);
        
            do {
                while (getCardBiddingValue(cards[i]) > pivotValue ||
                        (getCardBiddingValue(cards[i]) == pivotValue &&
                        getWeightToValueRatio(cards[i]) < pivotRatio))
                    i++;
                while (getCardBiddingValue(cards[j]) < pivotValue ||
                        (getCardBiddingValue(cards[j]) == pivotValue &&
                        getWeightToValueRatio(cards[j]) > pivotRatio))
                    j--;
                
                if (i <= j) {
                    temp = cards[i];
                    cards[i] = cards[j];
                    cards[j] = temp;
                    i++;
                    j--;
                }
            } while (i <= j);
            
            if (left < j)
                reverseSortCardsByValue(cards, left, j);
            if (i < right)
                reverseSortCardsByValue(cards, i, right);
        }
    }
    
    /* It might be useful to define the following three methods in part of a
     * nested class extending Card that only this class can access. If only I
     * knew exactly how or even if it can be done, though...
     */
    private double getWeightToValueRatio(Card card) {
        return getCardBiddingValue(card) / getCardBiddingWeight(card);
    }
    
    private int getCardBiddingValue(Card card) {
        switch (numPlayers) {
            case 3:
                switch (card.getRank()) {
                    case SIX: return 2;
                    case SEVEN: return 4;
                    case EIGHT: return 6;
                    case NINE: return 8;
                    case TEN: return 9;
                    case JACK: return 7;
                    case QUEEN: return 5;
                    case KING: return 3;
                    case ACE: return 1;
                    default: throw new IllegalArgumentException();
                }
            case 4:
                switch (card.getRank()) {
                    case TWO: return 2;
                    case THREE: return 4;
                    case FOUR: return 6;
                    case FIVE: return 8;
                    case SIX: return 10;
                    case SEVEN: return 12;
                    case EIGHT: return 13;
                    case NINE: return 11;
                    case TEN: return 9;
                    case JACK: return 7;
                    case QUEEN: return 5;
                    case KING: return 3;
                    case ACE: return 1;
                    default: throw new IllegalArgumentException();
                }
            default: // i.e. players == 5
                switch (card.getRank()) {
                    case TWO: return 2;
                    case THREE: return 4;
                    case FOUR: return 6;
                    case FIVE: return 8;
                    case SIX: return 10;
                    case SEVEN: return 12;
                    case EIGHT: return 14;
                    case NINE: return 15;
                    case TEN: return 13;
                    case ELEVEN: return 11;
                    case TWELVE: return 9;
                    case JACK: return 7;
                    case QUEEN: return 5;
                    case KING: return 3;
                    default: return 1; // since all 15 ranks are allowed
                }
        }
    }
     
    private int getCardBiddingWeight(Card card) {
        return card.getSuit().ordinal() + 1; // avoids division by zero
    }

    @Override
    protected void placeBid(Card[] cards) {
        super.placeBid(cards);
        
        for (Card card : cards) {
            suitLengths[card.getSuit().ordinal()]--;
        }
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
    public void play(Card[] cardsPlayed, CardSuit trump) {
        if (isTryingToWin && super.getTricksBid() == super.getTricksWon()) {
            isTryingToWin = false;
        }
        
        if (cardsPlayed[0] == null) { // i.e. the player is leading
            
            // Try to get rid of non-trump middle cards first.
            for (int i = 0; i < super.getHand().size(); i++) {
                if (super.getHand().get(i).getSuit() != trump && isMidCard(super.getHand().get(i))) {
                    playCard(super.getHand().get(i));
                    return;
                }
            }
            
            if (isTryingToWin) {
                
                /* Go through each suit in descending order and play the highest
                 * card from it. */
                if (hasSuit(CardSuit.CLUBS)) {
                    playCard(getHighCard(CardSuit.CLUBS));
                    return;
                }
            
                if (hasSuit(CardSuit.HEARTS)) {
                    playCard(getHighCard(CardSuit.HEARTS));
                    return;
                }
            
                if (hasSuit(CardSuit.SPADES)) {
                    playCard(getHighCard(CardSuit.SPADES));
                    return;
                }
            
                if (hasSuit(CardSuit.DIAMONDS)) {
                    playCard(getHighCard(CardSuit.DIAMONDS));
                    return;
                }
            }
            
            /* Go through each suit in descending order and play the lowest card
             * from it. */
            if (hasSuit(CardSuit.CLUBS)) {
                playCard(getLowCard(CardSuit.CLUBS));
                return;
            }
            
            if (hasSuit(CardSuit.HEARTS)) {
                playCard(getLowCard(CardSuit.HEARTS));
                return;
            }
            
            if (hasSuit(CardSuit.SPADES)) {
                playCard(getLowCard(CardSuit.SPADES));
                return;
            }
            
            if (hasSuit(CardSuit.DIAMONDS)) {
                playCard(getLowCard(CardSuit.DIAMONDS));
                return;
            }
        }
        
        // The below is for if the player is not leading.
        
        // FIXME: Problem is suspected to be here.
        if (isTryingToWin) {
            if (hasSuit(cardsPlayed[0].getSuit())) {
                if (cardsPlayed[0].outranks(getHighCard(cardsPlayed[0].getSuit()))) {
                    playCard(getLowCard(cardsPlayed[0].getSuit()));
                    return;
                }
                playCard(getHighCard(cardsPlayed[0].getSuit()));
                return;
            }
            
            if (trump != null) {
                if (hasSuit(trump)) {
                    playCard(getLowCard(trump));
                    return;
                }
            }
        }
        
        // The remainder is if trying to lose.
        
        if (hasSuit(cardsPlayed[0].getSuit())) {
            playCard(getHighCard(cardsPlayed[0].getSuit()));
            return;
        }
        
        if (trump != null && hasSuit(trump)) {
            for (int i = 1; i < cardsPlayed.length; i++) {
                if (cardsPlayed[i] != null && cardsPlayed[i].getSuit() == trump)
                {
                    playCard(getHighCard(trump));
                    return;
                }
            }
        }
    }
    
    @Override
    protected void playCard(Card card) {
        super.playCard(card);
        suitLengths[card.getSuit().ordinal()]--;
    }
    
    public boolean isLowCard(Card card){
        switch (card.getRank()) {
            case TWO: return true;
            case THREE: return true;
            case FOUR: return true;
            case FIVE: return true;
            case SIX: return numPlayers != 4;
            case SEVEN: return numPlayers == 3;
            case EIGHT: return numPlayers == 3;
            default: return false;
        }
    }
    
    public boolean isMidCard(Card card){
        switch (card.getRank()) {
            case SIX: return numPlayers == 4;
            case SEVEN: return numPlayers > 3;
            case EIGHT: return numPlayers > 3;
            case NINE: return true;
            case TEN: return true;
            case ELEVEN: return true;
            case JACK: return numPlayers == 3;
            default: return false;
        }
    }
    
    public boolean isHighCard(Card card){
        switch (card.getRank()) {
            case TWELVE: return true;
            case JACK: return numPlayers > 3;
            case QUEEN: return true;
            case KING: return true;
            case ACE: return true;
            default: return false;
        }
    }
    
    /**
     * This method is nearly identical to {@code getHighCard(Card o)}; the
     * difference is in the return statement. This method should not be called
     * outside of the {@code SimplePlayer} class.
     * 
     * @param suit  the suit from which the lowest card should be selected
     * @return the lowest card in said suit
     */
    private Card getHighCard(CardSuit suit) {
        if (!hasSuit(suit)) {
            throw new IllegalArgumentException("Suit not held.");
        }
        
        ArrayList<Card> handCopy = new ArrayList<>(super.getHand());
        
        handCopy.removeIf(p -> p.getSuit() != suit);
        
        Collections.sort(handCopy);
        return handCopy.get(handCopy.size() - 1);
    }
    
    /**
     * This method is nearly identical to {@code getHighCard(Card o)}; the
     * difference is in the return statement. This method should not be called
     * outside of the {@code SimplePlayer} class.
     * 
     * @param suit  the suit from which the lowest card should be selected
     * @return the lowest card in said suit
     */
    private Card getLowCard(CardSuit suit) {
        if (!hasSuit(suit)) {
            throw new IllegalArgumentException("Suit not held.");
        }
        
        ArrayList<Card> handCopy = new ArrayList<>(super.getHand());
        
        handCopy.removeIf(p -> p.getSuit() != suit);
        
        Collections.sort(handCopy);
        return handCopy.get(0);
    }

    @Override
    public String getPlayerStrategy() {
        return "Simple";
    }
}
