package com.example.speedcardgame;

import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class GameStrategy {
    private Queue<Cards> cards = new LinkedList<Cards>();
    private Cards[] cardsArr = new Cards[54];
    private boolean win;
    private boolean loss;
    public Queue<Cards> centerCards = new LinkedList<Cards>();
    public Queue<Cards> pOneCards = new LinkedList<Cards>();
    public Queue<Cards> pTwoCards = new LinkedList<Cards>();

    private static final int allCards = 54;
    private static final int player1Cards = 20;
    private static final int player2Cards = 40;



    public GameStrategy() {
        win = false;
        loss = false;

        // Creating All Cards
        Cards card;
        for (int i = 0; i < allCards; i++) {
            card = new Cards(i);
            cardsArr[i] = card;
        }

        Random rnd = new Random();
        // Shuffle Cards
        Cards temp;
        for (int j = 0; j < allCards; j++) {
            int num = j + rnd.nextInt(allCards - j);
            temp = cardsArr[num];
            cardsArr[num] = cardsArr[j];
            cardsArr[j] = temp;
        }

        // Shuffle cards to players and desk
        for (int x = 0; x < allCards; x++) {
            cards.add(cardsArr[x]);
            if (x < player1Cards)
                pOneCards.add(cardsArr[x]);
            else if (x < player2Cards)
                pTwoCards.add(cardsArr[x]);
            else
                centerCards.add(cardsArr[x]);
        }
    }

    public boolean playable(int num1, int num2) {
        if ((Math.abs(num2 - num1) == 1) || (Math.abs(num2 - num1) == 12) || (num2 == 69) || (num1 == 69)) {
            return true;
        }
        return false;
    }

    public boolean allPlayable(Cards[] p1, Cards[] p2,Cards d1, Cards d2) {
        for (int i = 0; i < 5; i++) {
            if (p1[i] != null ) {
                if (playable(p1[i].getCardNum(), d1.getCardNum()) || playable(p1[i].getCardNum(), d2.getCardNum()))
                    return true;
            }
            if (p2[i] != null) {
                if (playable(p2[i].getCardNum(), d1.getCardNum()) || playable(p2[i].getCardNum(), d2.getCardNum()))
                    return true;
            }
        }
        return false;
    }

    public boolean isWon() {
        return win;
    }

    public void setWon() {
        this.win = true;
    }

    public boolean isLost() {
        return loss;
    }

    public void setLost() {
        this.loss = true;
    }



    public Queue<Cards> getCenterCards() {
        return centerCards;
    }

    public void setCenterCards(Queue<Cards> centerCards) {
        this.centerCards = centerCards;
    }

    public Queue<Cards> getpOneCards() {
        return pOneCards;
    }

    public void setpOneCards(Queue<Cards> pOneCards) {
        this.pOneCards = pOneCards;
    }

    public Queue<Cards> getpTwoCards() {
        return pTwoCards;
    }

    public void setpTwoCards(Queue<Cards> pTwoCards) {
        this.pTwoCards = pTwoCards;
    }
}
