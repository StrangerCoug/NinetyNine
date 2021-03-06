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

import ninetynine.exceptions.CardNotHeldException;

/**
 *
 * @author Jeffrey Hope
 */
public class RandomPlayer extends Player {
    
    public RandomPlayer(String name) {
        super(name);
    }

    @Override
    public void bid(CardSuit trump) throws CardNotHeldException {
        placeBid(super.getHand().subList(0, 3).toArray(new Card[3]));
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
    public Card selectCard(Card[] cardsPlayed, CardSuit trump) {
        Card[] candidateCards = super.getHand().toArray(new Card[super.getHand()
                .size()]);
        CardSuit suitLed;
        
        if (cardsPlayed[0] == null)
            suitLed = null;
        else suitLed = cardsPlayed[0].getSuit();
        
        if (super.hasSuit(suitLed)) {
            for (Card candidateCard : candidateCards) {
                if (candidateCard.getSuit() == suitLed) {
                    return candidateCard;
                }
            }
        }
        
        return candidateCards[0];
    }

    @Override
    public String getPlayerStrategy() {
        return "Random";
    }
}
