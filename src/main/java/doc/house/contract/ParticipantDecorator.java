package doc.house.contract;

import doc.house.business.Hand;
import doc.house.business.Card;
public abstract class ParticipantDecorator implements Participant {
    protected Participant participant;

    public ParticipantDecorator(Participant participant) {
        this.participant = participant;
    }

    @Override
    public void addCard(Card card) {
        participant.addCard(card);
    }

    @Override
    public Hand getHand() {
        return participant.getHand();
    }

    @Override
    public int getScore() {
        return participant.getScore();
    }

    @Override
    public boolean isBusted() {
        return participant.isBusted();
    }

    @Override
    public boolean isStanding() {
        return participant.isStanding();
    }

    protected Participant getDecoratedParticipant() {
        return participant;
    }


}
