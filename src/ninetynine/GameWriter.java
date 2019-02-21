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
import java.io.PrintWriter;
import java.util.Arrays;

/**
 *
 * @author Jeffrey Hope
 */
public class GameWriter {
    File file;
    PrintWriter writer;
    
    public GameWriter(String filename) throws IOException {
        try {
            file = new File(filename);
            file.createNewFile();
            writer = new PrintWriter(file, "UTF-8");
        }
        catch (IOException e) {
            
        }
    }
    
   /**
     * Wrapper method to PrintWriter's print function.
     * 
     * @param s the string to be printed
     */
    public void print(String s) {
        writer.print(s);
        System.out.print(s);
    }
    
    /**
     * Wrapper method to PrintWriter's println function.
     * 
     * @param s the string to be printed
     */
    public void println(String s) {
        writer.println(s);
        System.out.println(s);
    }
    
    /**
     * Wrapper method to close the game file. Should be called after the
     * conclusion of the game.
     * 
     * @throws IOException 
     */
    public void close() throws IOException {
        writer.close();
    }

     public void printHandHeader(int handNo) throws IOException {
        writer.print("HAND " + handNo + "\n======");
        System.out.print("HAND " + handNo + "\n======");    
            for (int i = 0; i < Math.floor(Math.log10(handNo)); i++)
                {
            		writer.print("=");
            		System.out.print("=");
                }
            
        writer.print("\n");
        System.out.print("\n");
    }
    
    public void printTrickHeader(int trickNo) throws IOException {
        writer.print("Trick " + trickNo + "\n-------");
        System.out.print("Trick " + trickNo + "\n-------");   
            for (int i = 0; i < Math.floor(Math.log10(trickNo)); i++)
                {
            		writer.print("-");
            		System.out.print("-");
                }
            
        writer.print("\n");
        System.out.print("\n");
    }
    
    /* I'm not 100% sure I like this here, to be honest, since it's not properly
     * a writing function. I'm open to alternative placements, but it's here for
     * the time being.
     */
    
    public String cardsToString(Card[] cards) {
        String cardsAsText = "";
        Card[] sortedCards = Arrays.copyOf(cards, cards.length);
        Arrays.sort(sortedCards);
        
        for (Card sortedCard : sortedCards)
            cardsAsText = cardsAsText + (sortedCard.toStringShort() + " ");
        
        return cardsAsText;
    }
    
    
    
    protected void announcePlay(Player player, Card card) {
        writer.println(player.getName() + " plays the " + card.toString() +
                ".");
        System.out.println(player.getName() + " plays the " + card.toString() +
                ".");
    }
}
