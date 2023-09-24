package doc.house.business;

import doc.house.dataTypes.Move;
import doc.house.dataTypes.State;

public class Dealer {
	private final Hand hand;
	private State state;

	public Dealer() {
		this.hand = new Hand();
	}

	public void addCard(Card card) {
		hand.addCard(card);
		getScore();
	}

	public Hand getHand() {
		return hand;
	}

	public int getScore() {
		var score =  hand.getScore();
		if (score > 21) {
			this.state = State.BUSTED;
		}
		return score;
	}

	public void clear() {
		hand.clear();
	}
	public Move getMove() {
		var score = this.getScore();
		if (score > 21) {
			return Move.BUST;
		}
		if (score < 17) {
			return Move.HIT;
		}
		return Move.STAND;
	}
	public String displayHand() {
		return hand.toString();
	}


}

