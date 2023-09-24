package doc.house.business;

import doc.house.contract.Participant;
import doc.house.dataTypes.State;

/**
 * This class represents a player in the game.
 */
public class Player implements Participant {

	private static final int INITIAL_CHIPS = 1000;

	private String name;
	private State state;
	private int chips;
	private int bet;
	private final Hand hand;
	private Participant participant;

	/**
	 * Constructor for Player.
	 */
	public Player() {
		participant = this;
		state = State.STANDARD;
		hand = new Hand();
		chips = INITIAL_CHIPS;
	}

	/**
	 * Sets the name of the player.
	 * @param name The name of the player.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the player.
	 * @return The name of the player.
	 */
	public String getName() {
		return this.name == null ? "no name" : this.name;
	}

	/**
	 * Gets the state of the player.
	 * @return The current state of the player.
	 */
	public State getState() {
		return this.state;
	}
	/**
	 * Sets the state of the player.
	 * @param state The state of the player.
	 */
	public void setState(State state) {
		this.state = state;

	}

	/**
	 * Adds a card to the player's hand.
	 * @param card The card to add to the player's hand.
	 */


	/**
	 * Clears the player's hands and bets.
	 */
	public void clear() {
		participant = this; // Reset to original state
		hand.clear();
		bet = 0;
		state = State.STANDARD;
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
	public boolean isBusted() {
		this.getScore();
		return this.state == State.BUSTED;
	}
	@Override
	public boolean isStanding() {
		return this.state == State.STANDING;
	}

	/**
	 * Initiates a split, decorating the participant with a SplitHandDecorator.
	 */
	public void split() {
		participant = new SplitHandDecorator((Player)participant);
	}

	/**
	 * Gets the player's bet.
	 * @return The player's bet.
	 */
	public int getBet() {
		return bet;
	}
	/**
	 * Sets the player's bet.
	 * @param bet The player's bet.
	 */
	public void setBet(int bet) {
		this.bet = bet;
		this.chips -= bet;
	}

	/**
	 * doubles the player's bet.
	 */
	public void doubleBet() {
		this.chips -= this.bet;
		this.bet *= 2;
	}

	/**
	 * Gets the player's chips.
	 * @return The player's chips.
	 */
	public int getChips() {
		return this.chips;
	}
	/**
	 * returns the players hand
	 * @return  string representation of the hand
	 */
	public String displayHand() {
		return this.hand.toString();
	}
	public SplitHandDecorator splitHand() {
		SplitHandDecorator splitPlayer = new SplitHandDecorator(this);
		splitPlayer.addCardToSplitHand(hand.split());// Transfer one card to the split hand
		this.chips -= this.bet;
		return splitPlayer;

	}
	/**
	 * adds chips according to the bet multiplier
	 */
	public void payout(double betMultiplier) {
		this.chips +=  (int) (this.bet * betMultiplier);
		this.bet = 0;
	}
	/**
	 * adds chips to the player
	 * @param chips the amount of chips to add
	 */
	public void addChips(int chips) {
		this.chips += chips;
	}
}
