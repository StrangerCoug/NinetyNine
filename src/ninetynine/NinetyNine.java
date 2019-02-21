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

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import ninetynine.exceptions.CardNotHeldException;
import ninetynine.exceptions.IllegalCardException;

/**
 *
 * @author Jeffrey Hope
 */
public class NinetyNine {
    static final Scanner KEYBOARD = new Scanner(System.in);
    
    /**
     * 
     * @param args  the command line arguments
     */
    public static void main(String[] args) throws IOException,
            CardNotHeldException, IllegalCardException {
        int numPlayers = promptNumberOfPlayers();
        
        Player[] players = new Player[numPlayers];
        
        for (int i = 0; i < players.length; i++) {
            System.out.print("Please give player #" + (i + 1) + " a name: ");
            String name = KEYBOARD.nextLine();
            
            boolean isInputValid = false;
            
            while(!isInputValid) {
                System.out.print("Select (R)andom, (S)imple, or (M)onte Carlo: ");
                char strategy = KEYBOARD.nextLine().charAt(0);
                
                switch (strategy) {
                    case 'r':
                    case 'R':
                        players[i] = new RandomPlayer(name);
                        isInputValid = true;
                        break;
                    case 's':
                    case 'S':
                        players[i] = new SimplePlayer(name);
                        isInputValid = true;
                        break;
                    case 'm':
                    case 'M':
                        players[i] = new MonteCarloPlayer(name);
                        isInputValid = true;
                        break;
                    default:
                        System.out.println("Invalid strategy.");
                        break;
                }
            }
        }
        
        int numGames = promptNumberOfGames();
        
        System.out.println("Running games...");
        
        File directory = new File("runs/");
        if(!directory.exists())
            directory.mkdir();
        
        Game[] games = new Game[numGames];
        String filenamePrefix = "runs/game";
        
        for (int i = 0; i < numGames; i++) {
            String filename = filenamePrefix;
            
            if (i == 0)
                filename += "0000.txt";
            else {
                for (int j = 3; j > (int) Math.floor(Math.log10(i)); j--)
                    filename += "0";
            
                filename += i + ".txt";
            }
                    
            games[i] = new Game(players, filename);
            games[i].play();
        }
        System.out.println("Complete.");
        KEYBOARD.close();
    }
    
    public static int promptNumberOfPlayers() {
        while (true) {
            try {
                System.out.print("How many players would you like? (3-5): ");
                int numPlayers = Integer.parseInt(KEYBOARD.nextLine());
                
                if (numPlayers < 3 || numPlayers > 5) {
                    System.out.println("Wrong number of players.");
                }
                else return numPlayers;
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }
    
    public static int promptNumberOfGames() {
        while (true) {
            try {
                System.out.print("How many games would you like to run? " + 
                        "(1-10000): ");
                int numGames = Integer.parseInt(KEYBOARD.nextLine());
                
                if (numGames < 1 || numGames > 10000) {
                    System.out.println("Wrong number of games.");
                }
                else return numGames;
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }
}
