package com.example.speedcardgame;

import android.util.Log;

public class Cards {
    private int cardId; // 0-53
    private int cardNum;
    private String number;
    private String print;
    private String card;


    public Cards(int cardId) {
        this.cardId = cardId;
        int num = this.cardId;
        if (num / 4 == 13) {
            this.cardNum = 69;
            this.print = "joker";
            this.card = this.print;
        }
        else {
            cardNum = (num / 4) + 1;
            if (num % 4 == 0)
                this.print = "club";
            if (num % 4 == 1)
                this.print = "diamond";
            if (num % 4 == 2)
                this.print = "heart";
            if (num % 4 == 3)
                this.print = "spade";
            this.card = this.cardNum + " " + this.print;


        }
        Log.d("card", "card created");
    }

    public int getCardId() {
        return cardId;
    }

    public String getCard() {
        Log.d("string", "string created 1");
        return card;
    }

    public int getCardNum(){
        return cardNum;
    }

}
