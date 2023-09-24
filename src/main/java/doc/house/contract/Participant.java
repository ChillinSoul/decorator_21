package doc.house.contract;

import doc.house.business.Hand;
import doc.house.business.Card;
import doc.house.dataTypes.State;
public interface Participant {
    void clear();
    void addCard(Card card);
    Hand getHand();
    int getScore();
    boolean isBusted();
    boolean isStanding();
    State getState();
    void setState(State state);
    String displayHand();

}

