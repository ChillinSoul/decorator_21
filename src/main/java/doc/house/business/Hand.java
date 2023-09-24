package doc.house.business;

import java.util.Stack;

public class Hand{
	private final Stack<Card> hand;

	public Hand(){
		this.hand = new Stack<>();
	}
	public void clear(){
		this.hand.clear();
	}
	public void addCard(Card card){
		this.hand.push(card);
	}

	/**
	 * using the stream API creates a stream from the cards in the hand then sums up the value
	 * returns the score of the hand
	 * @param isHigh if true, returns the highest score using the ternary "?" operator
	 * @return the score
	 */

	private int getScoreEval(boolean isHigh){
        return hand.stream()
				.mapToInt(card -> card.getValue() == 1 && isHigh ? 11 : card.getValue())
				.sum();

	}
	public int getScore(){
		int highScore = this.getScoreEval(true);
		int lowScore = this.getScoreEval(false);
		if (highScore <= 21) {
			return highScore;
		}
		return lowScore;
	}

	public Card split(){
		return this.hand.pop();
	}

	public boolean isBusted(){
        return this.getScore() > 21;
	}
	public boolean isBlackJack(){
		return this.getScore() == 21 && this.getSize() == 2;
	}

	public int getSize(){
		return hand.size();
	}
	public Card getCard(int index){
		return hand.get(index);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(Card card : hand)
			sb.append(card.toString()).append(" ");
		return sb.toString();
	}

}
