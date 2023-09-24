package doc.house.business;

import doc.house.helpers.MessageDictionary;
import doc.house.dataTypes.Suit;

public class Card{
	private final Suit suit;
	private final int rank;
	private final MessageDictionary messageDictionary = new MessageDictionary("messages");

	private boolean faceUp = false;

	public Card( Suit s, int r){
		suit = s;
		rank = r;
	}
	

	@Override
	public String toString(){
		if(this.faceUp) {
			String r = switch (rank) {
				case 1 -> messageDictionary.getMessage("ACE");
				case 11 -> messageDictionary.getMessage("JACK");
				case 12 -> messageDictionary.getMessage("QUEEN");
				case 13 -> messageDictionary.getMessage("KING");
				default -> Integer.toString(rank);
			};
			return r + messageDictionary.getMessage("OF") + messageDictionary.getMessage(suit.toString());
		}
		else return messageDictionary.getMessage("CARD_BACK");
	}
	/**
	 * displays the card in the terminal
	 */

	public int getValue(){
        return Math.min(rank, 10);
	}
	public void setFace(boolean faceUp){
		this.faceUp = faceUp;
	}

	public boolean isEquivalentTo(Card card){
		return this.rank == card.rank;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
