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
import ninetynine.exceptions.CardNotHeldException;
import ninetynine.exceptions.IllegalCardException;

/**
 *
 * @author Jeffrey Hope
 */
public class Game {
    private Player[] players;
    private Deck deck;
    private CardSuit currentTrump; // for no-trump, this is null
    private enum PremiumBid{DECLARE, REVEAL};
    private PremiumBid premiumBid;
    private Player premiumBidder;
    private final GameWriter gameWriter;
    
    public Game (Player[] players, String filename) throws IOException {
        if (players.length < 3 || players.length > 5) {
            throw new IllegalArgumentException("Wrong number of players.");
        }
        
        this.players = new Player[players.length];
        System.arraycopy(players, 0, this.players, 0, players.length);
        deck = new Deck(players.length);
        currentTrump = CardSuit.DIAMONDS;
        premiumBid = null;
        premiumBidder = null;
        gameWriter = new GameWriter(filename);
    }
    
    public Player[] getPlayers() {
        Player[] returnedPlayers = new Player[players.length];
        System.arraycopy(players, 0, returnedPlayers, 0, players.length);
        
        return returnedPlayers;
    }
    
    public Player getPlayer(int index) {
        return players[index];
    }

    public Deck getDeck() {
        return deck;
    }
    
    public CardSuit getCurrentTrump() {
        return currentTrump;
    }
    
    private PremiumBid getPremiumBid() {
        return premiumBid;
    }
    
    private Player getPremiumBidder() {
        return premiumBidder;
    }
    
    public void play() throws IOException, CardNotHeldException, IllegalCardException {
        int numPlayers = players.length; // for code legibility
        boolean gameWon = false;
        int dealerIndex;
        int handCounter = 0; /* since it's forced to increment at the beginning
                                of the game loop */
        boolean isHumanPlaying = false;
        
        gameWriter.println("PLAYERS\n=======");
        for (int i = 0; i < numPlayers; i++) {
            players[i].resetScore();
            gameWriter.println("Player #" + (i + 1) + ": " +
                    players[i].getName() + " (Strategy: " +
                    players[i].getPlayerStrategy() + ")");
            if (players[i].getPlayerStrategy() == "Human") isHumanPlaying = true;
        }
        gameWriter.print("\n");
        
        while(!gameWon) {
            dealerIndex = handCounter % numPlayers;
            gameWriter.printHandHeader(++handCounter);
            
            dealCards(dealerIndex);
            
            // Print the dealt cards.
            if (!isHumanPlaying) {
	            gameWriter.println("Initial deal\n------------");
	            
	            for (int i = 0; i < numPlayers; i++) {
	                players[i].resetTricksWon();
	                gameWriter.print(players[i].getName());
	                
	                if (i == dealerIndex)
	                    gameWriter.print(" (Dealer)");
	                
	                gameWriter.println(": " + gameWriter.cardsToString(
	                        players[i].getHand().toArray(new Card
	                                [players[i].getHand().size()])));
	            }
            }
            else {
            	for (int i = 0; i < numPlayers; i++) players[i].resetTricksWon();
            }
            gameWriter.print("Current trump: ");
            
            if (getCurrentTrump() == null)
                gameWriter.print("No Trump\n\n");
            
            else {
                switch(getCurrentTrump()) {
                    case DIAMONDS: gameWriter.println("Diamonds\n");
                    break;
                    case SPADES: gameWriter.println("Spades\n");
                    break;
                    case HEARTS: gameWriter.println("Hearts\n");
                    break;
                    default: gameWriter.println("Clubs\n");
                }
            }
            
            // Have each player bid and then print each bid.
            for (int i = 0; i < numPlayers; i++) {
                players[i].bid(currentTrump);
                if(!isHumanPlaying) {
	                gameWriter.print(players[i].getName() + "'s bid: " +
	                        gameWriter.cardsToString(players[i].getCardsBid()) + "("
	                        + players[i].getTricksBid() + " trick");
	                
	                // Make sure it's grammatically correct!
	                if (players[i].getTricksBid() != 1)
	                    gameWriter.print("s");
	                
	                gameWriter.print(")\n");
                }
            }
            
            gameWriter.print("\n");
                
            /* This is an "informal" way to do the premium bidding for now. I
             * do eventually want to upgrade this to the "formal" way described
             * at https://www.pagat.com/exact/99.html once it's clear to me how
             * to do it.
             */
            for (int i = 0; i < numPlayers; i++) {
                if (players[i].wantsToReveal()) {
                    premiumBid = PremiumBid.REVEAL;
                    break;
                }
                else if (players[i].wantsToDeclare()) {
                    premiumBid = PremiumBid.DECLARE;
                    break;
                }
            }
            
            // Initialize variables for trick-taking loop.
            byte indexOfTrickLeader = (byte) ((dealerIndex + 1) %
                    players.length);
            byte indexOfCurrentPlayer = indexOfTrickLeader;
            byte trickCounter = 0;
            Card[] cardsPlayed = new Card[numPlayers];
            Card winningCard;
            
            while (!getPlayer(0).getHand().isEmpty()) {
                gameWriter.printTrickHeader(++trickCounter);
                
                // Clear the past played cards.
                for (int i = 0; i < numPlayers; i++) {
                    cardsPlayed[i] = null;
                }
                
                for (int i = 0; i < numPlayers; i++) {
                    getPlayer(indexOfCurrentPlayer).selectAndPlayCard
                            (cardsPlayed, getCurrentTrump());
                    cardsPlayed[i] = getPlayer(indexOfCurrentPlayer)
                            .getCardPlayed();
                    gameWriter.announcePlay(getPlayer(indexOfCurrentPlayer),
                            cardsPlayed[i]);
                    indexOfCurrentPlayer = (byte) ((indexOfCurrentPlayer + 1) %
                            players.length);
                }
                
                winningCard = findWinningCard(cardsPlayed,
                        getCurrentTrump());
                
                for (int i = 0; i < numPlayers; i++) {
                    if (players[i].getCardPlayed().equals(winningCard)) {
                        gameWriter.println("\n" + players[i].getName() +
                                " wins the trick.\n");
                        players[i].awardTrick();
                        indexOfCurrentPlayer = indexOfTrickLeader = (byte) i;
                        break;
                    }
                }
            }
            
            scoreGame();
            gameWriter.println("Results\n-------");

            for (int i = 0; i < numPlayers; i++) {
                gameWriter.print(players[i].getName() + " won " +
                        players[i].getTricksWon() + " trick");
                
                // Again, we want to be grammatical
                if (players[i].getTricksWon() != 1)
                    gameWriter.print("s");
                
                gameWriter.print(" and so ");
                
                if (players[i].madeBid())
                    gameWriter.print("made");
                else gameWriter.print("failed");
                
                gameWriter.println(" their bid and now has " +
                        players[i].getScore() + " points.");
            }

            gameWriter.print("\n");
            gameWon = isThereAWinner();
        }
        
        gameWriter.print(getFirstPlacePlayer().getName() + " wins the game!");
        gameWriter.close();
    }
    
    /**
     * Deals out the entire deck of cards to each player.
     * 
     * @param dealerPos the position of the current dealer
     */
    
    public void dealCards(int dealerPos) {
        deck.shuffleDeck();
         
        for (int i = dealerPos + 1; !deck.deck.isEmpty(); i++) {
            deck.dealCardTo(players[i % players.length]);
        }
    }
    
    /**
     * Determines which card won the trick.
     * 
     * @param cards  the cards played in order
     * @param trump  the trump suit
     * @return the highest trump if a card was played, the highest card of the
     * suit led otherwise
     */
    
    public Card findWinningCard(Card[] cards, CardSuit trump) {
        boolean trumpFound = cards[0].getSuit().equals(trump);
        int winningCardIndex = 0;
        
        for (int i = 1; i < cards.length; i++) {
            if (trump != null) {
                if (!trumpFound && cards[i].getSuit().equals(trump)) {
                    trumpFound = true;
                    winningCardIndex = i;
                    continue;
                }
            }   
                
            if (!cards[i].getSuit().equals(cards[winningCardIndex].getSuit()))
                continue;
            
            if (cards[i].outranks(cards[winningCardIndex]))
                winningCardIndex = i;
        }
        
        return cards[winningCardIndex];
    }
    
    /**
     * Tabulates the results of the hand. Currently does not support
     * declarations or revelations.
     */
    public void scoreGame() {
        boolean[] madeBid = new boolean[players.length];
        byte numWinners = 0;
        byte winBonus;
        
        for (int i = 0; i < players.length; i++) {
            players[i].addToScore(players[i].getTricksWon());
            madeBid[i] = players[i].getTricksWon() == players[i].getTricksBid();
            
            if (madeBid[i]) {
                numWinners++;
            }
        }
        
        switch (numWinners) {
            case 0: currentTrump = CardSuit.DIAMONDS;
                break;
            case 1: currentTrump = CardSuit.SPADES;
                break;
            case 2: currentTrump = CardSuit.HEARTS;
                break;
            case 3: currentTrump = CardSuit.CLUBS;
                break;
            default: currentTrump = null;
        }
        
        if (numWinners == 0) {
            return; // since there's no point in continuing
        }
        
        if (players.length == 5) {
            winBonus = (byte) ((6 - numWinners) * 10);
        }
        
        else {
            if (numWinners == 4) {
                winBonus = 0;
            }
            
            else winBonus = (byte) ((4 - numWinners) * 10);
        }
        
        for (int i = 0; i < players.length; i++) {
            if (madeBid[i]) {
                players[i].addToScore(winBonus);
            }
        }
    }
    
    public boolean isThereAWinner() {
        for (Player player : players) {
            if (player.getScore() >= 100) {
                return true;
            }
        }
        
        return false;
    }
    
    public Player getFirstPlacePlayer() {
        Player leader = players[0];
        
        for (int i = 1; i < players.length; i++) {
            if (players[i].getScore() > leader.getScore())
                leader = players[i];
        }
        
        return leader;
    }
}