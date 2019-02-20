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

/**
 *
 * @author Jeffrey Hope
 */
public class MonteCarloPlayer extends Player {

    public MonteCarloPlayer(String name) {
        super(name);
    }
    
    @Override
    public void bid(CardSuit trump) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /*
    Code from Homework 4: should be modified
    
    Debating how to attack this: Multithread? Pass copies of the game?
 
    public static void run(ArrayList<State> map) {
        State currentState;
        double[] stateValues = new double[map.size() - 1];
        boolean[] visited = new boolean [map.size() - 1];
        ArrayList<Transition> possibleTransitions;
        int reward;
        int sumOfRewards = 0;
        double averageReward = 0;
        
        // initialize variables
        for (int i = 0; i < map.size() - 1; i++) {
            stateValues[i] = 0;
        }
        
        for (int i = 1; i <= 50; i++) {
            System.out.print(i + ".) ");
            
            //initialize for the episode
            reward = 0;

            for (int j = 0; j < stateValues.length; j++)
                visited[j] = false;
            
            currentState = map.get(0);
            printState(currentState);
            
            while (!currentState.isFinalState()) {
                visited[map.indexOf(currentState)] = true;
                possibleTransitions = currentState.getTransitions();
                
                int rand = (int)(Math.random() *
                        possibleTransitions.size());
                
                printAction(possibleTransitions.get(rand).getAction());
                sumOfRewards += reward +=
                        possibleTransitions.get(rand).getReward();
                currentState = currentState.act
                        (possibleTransitions.get(rand).getAction());
            
                printState(currentState);
            }
            
            System.out.println(" (Reward: " + reward +")");
            
            for (int j = 0; j < stateValues.length; j++)
                if (visited[j] == true)
                    stateValues[j] = stateValues[j] + 0.1 *
                            (reward - stateValues[j]);
            
            averageReward = sumOfRewards / i;
        }
        
        System.out.println("\nState values:");
        
        for (int i = 0; i < stateValues.length; i++) {
            System.out.println("V(" + map.get(i).getName() + ") = " +
                    stateValues[i]);
        }
        
        System.out.println("\nAverage reward: " + averageReward);
    }
    */

    @Override
    public String getPlayerStrategy() {
        return "Monte Carlo";
    }
}
