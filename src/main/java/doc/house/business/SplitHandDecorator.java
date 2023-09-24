package doc.house.business;

import doc.house.contract.Participant;
import doc.house.contract.ParticipantDecorator;
import doc.house.dataTypes.State;
public class SplitHandDecorator extends ParticipantDecorator {
    private  Hand splitHand;
    private State splitState;
    private final SplitParticipant splitParticipant;
    private int splitBet;
    public void clear() {
        super.getDecoratedParticipant().clear();
        this.splitHand = null;
        this.splitState = null;
        this.splitBet = 0;
    }

    public SplitHandDecorator(Participant player) {
        super(player);
        this.splitHand = new Hand();
        this.splitState = State.STANDARD;
        this.splitBet = ((Player)player).getBet();

        this.splitParticipant = new SplitParticipant(this.splitHand, this.splitState, this.splitBet);
    }

    public void addCardToSplitHand(Card card) {
        this.splitHand.addCard(card);
        if (this.splitHand.isBusted()) {
            this.splitState = State.BUSTED;
        }
    }

    public void setState(State state){
        this.splitState = state;
    }
    public String displayHand() {
        return this.splitHand.toString();
    }


    public Participant getOriginalParticipant() {
        return super.getDecoratedParticipant();
    }

    public Participant getSplitParticipant() {
        return this.splitParticipant;
    }
    public void payout(double splitBetMultiplier) {
        this.splitParticipant.payout(splitBetMultiplier);
    }

    @Override
    public State getState() {
        return this.splitState;
    }



    private class SplitParticipant implements Participant {
        private final Hand hand;
        private State state;
        private int bet;

        @Override
        public void clear() {
            this.hand.clear();
            this.state = State.STANDARD;
            this.bet = 0;
        }

        public SplitParticipant(Hand hand, State state, int bet) {
            this.hand = hand;
            this.state = state;
            this.bet = bet;
        }

        @Override
        public void setState(State state) {
            this.state = state;
        }

        @Override
        public void addCard(Card card) {
            this.hand.addCard(card);
            if (this.hand.isBusted()) {
                this.state = State.BUSTED;
            }
        }

        @Override
        public Hand getHand() {
            return this.hand;
        }

        @Override
        public int getScore() {
            var score = this.hand.getScore();
            if (score > 21) {
                this.state = State.BUSTED;
            }
            return score;
        }

        @Override
        public State getState() {
            return this.state;
        }

        @Override
        public boolean isBusted() {
            this.getScore();
            return this.state == State.BUSTED;
        }
        @Override
        public boolean isStanding() {
            return this.state == State.STANDING;
        }
        @Override
        public String displayHand() {
            return this.hand.toString();
        }
        public void payout(double splitBetMultiplier) {
            Participant originalParticipant = getDecoratedParticipant();
            if (originalParticipant instanceof Player player) {
                player.addChips((int) (splitBetMultiplier * this.bet));
                this.bet = 0;
            } else {
                // Handle the case where the original participant is not a Player
                System.err.println("The original participant is not a Player instance.");
            }
        }

    }
}
