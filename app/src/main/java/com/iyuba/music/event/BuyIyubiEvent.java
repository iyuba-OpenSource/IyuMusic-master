package com.iyuba.music.event;

public class BuyIyubiEvent {
    public String price;
    public String amount;
    public BuyIyubiEvent(String pp, String aa) {
        price = pp;
        amount = aa;
    }
}
