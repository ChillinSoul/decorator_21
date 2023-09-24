package doc.house.business;

import doc.house.dataTypes.Suit;

import java.util.Collections;
import java.util.Stack;
import java.util.stream.Collectors;

public class Deck{

	private Stack <Card> cards = new Stack<>();

	public Deck(int numberOfDecks, int numberOfBurnCards){
		Build(numberOfDecks, numberOfBurnCards);
	}

	/**
	 * creates the deck
	 */
	public void Build(int numberOfDecks, int numberOfBurnCards){
		boolean dev = false;
		if(dev) {
			for (int i = 1; i <= numberOfDecks; i++)
				for (Suit suit : Suit.values())
					for (int rank = 1; rank <= 13; rank++)
						this.cards.add(new Card(suit, rank));
		}
		else{
			for (int i = 1; i <= numberOfDecks; i++)
				for (Suit suit : Suit.values())
					for (int rank = 1; rank <= 13; rank++)
						this.cards.add(new Card(suit, 8));
		}
		shuffle();
		discard(numberOfBurnCards);
		shuffle();
	}

	/**
	 * retrieves first card from the deck
	 * returns the {@link Card}, if the deck is empty returns null
	 */
	public Card draw(boolean faceUp){
		if (cards.isEmpty()) return null;
		Card card = cards.pop();
		card.setFace(faceUp);
		return card;

	}

	/**
	 * shuffles the deck using built-in function Collection.shuffle()
	 */
	public void shuffle(){
		Collections.shuffle(cards);
	}

	public  void discard(int amount){
		this.cards = cards.stream()
				.limit(cards.size()-amount)
				.collect(Collectors
						.toCollection(Stack<Card> :: new));
	}


}