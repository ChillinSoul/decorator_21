package doc.house.logic;


import doc.house.business.*;
import doc.house.contract.Participant;
import doc.house.helpers.RuleBook;
import doc.house.ui.Terminal;
import doc.house.dataTypes.Move;
import doc.house.dataTypes.State;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Dealer dealer;
    private final List<Participant> players;
    private final Deck deck;
    private final Terminal terminal;
    private final LegalCheck legalCheck;
    private final RuleBook rules;

    public Game() {
        this.rules = new RuleBook();
        this.dealer = new Dealer();
        this.terminal = new Terminal();
        this.legalCheck = new LegalCheck(rules);
        this.deck = new Deck(rules.getRule("NumberOfDecks"), rules.getRule("NumberOfBurnCards"));
        this.players = new ArrayList<>();
    }

    public void initializeGame() {
        addPlayers();
    }

    public void play() {
        boolean playAgain = true;
        initializeGame();
        while (playAgain) {
            resetForNewRound();
            collectBets();
            dealInitialCards();
            executePlayerTurns();
            executeDealerTurn();
            evaluateRound();
            displayRoundResults();
            playAgain = terminal.requestBoolean("playAgain");
        }
    }

    private void resetForNewRound() {
        dealer.clear();

        List<Participant> newPlayers = new ArrayList<>();
        for (Participant player : players) {
            player.clear();
            if (player instanceof SplitHandDecorator) {
                player = ((SplitHandDecorator) player).getOriginalParticipant();
            }
            newPlayers.add(player);
        }
        players.clear();
        players.addAll(newPlayers);
    }


    private void collectBets() {
        for (Participant player : players) {
            displayInfo(player);
            int bet = terminal.requestInt("BET");
            ((Player)player).setBet(bet);
        }
    }

    private void dealInitialCards() {
        // First round of dealing
        dealer.addCard(deck.draw(true));
        players.forEach(player -> player.addCard(deck.draw(true)));
        // Second round of dealing
        dealer.addCard(deck.draw(false));
        players.forEach(player -> player.addCard(deck.draw(true)));
    }

    private void executePlayerTurns() {
        for (Participant player : players) {
            displayInfo(player);
            playerLoop(player);
        }
    }

    private void playerLoop(Participant participant) {
        while (!participant.isBusted() && !participant.isStanding()) {
            terminal.display(participant.displayHand());
            terminal.display("Score: " + participant.getScore());
            Move action = terminal.requestMove(legalCheck.getLegalMoves(participant)); // Get player action from terminal
            switch (action) {
                case BUST:
                    break;
                case HIT:
                    handleHit(participant);
                    break;
                case STAND:
                    handleStand(participant);
                    break;
                case DOUBLE_DOWN:
                    handleDoubleDown((Player) participant);
                    return;  // Ends player's turn
                case SPLIT:
                    handleSplit(participant);
                    return;  // Ends player's turn
                default:
                    terminal.display("Invalid action. Try again.");
                    break;
            }
            terminal.display(participant.displayHand());
        }
    }


    private void handleDoubleDown(Player player) {
        // Double player's bet
        player.doubleBet();
        // Deal one more card
        player.addCard(deck.draw(true));
        // Ends player's turn - standing is set to true
        player.setState(State.STANDING);
    }

    private void handleSplit(Participant player) {
        if (player instanceof Player) {
            players.remove(player);
            player = ((Player)player).splitHand();
            players.add(player);
            ((SplitHandDecorator) player).getOriginalParticipant().addCard(deck.draw(true));
            ((SplitHandDecorator) player).getSplitParticipant().addCard(deck.draw(true));

            // Now you have two separate hands: player and splitPlayer
            // Proceed with the game logic for each hand
            playerLoop(((SplitHandDecorator) player).getOriginalParticipant());
            playerLoop(((SplitHandDecorator) player).getSplitParticipant());

        } else {
            terminal.display("Cannot split hand for non-player participants.");
        }
    }



    private void handleHit(Participant player) {
        player.addCard(deck.draw(true));
    }

    private void handleStand(Participant player) {
        player.setState(State.STANDING);
    }



    private void executeDealerTurn() {
        Move dealerMove = dealerPlay();
        while (dealerMove == Move.HIT) {
            dealerMove = dealerPlay();
        }
    }

    private Move dealerPlay() {
        Move dealerMove = dealer.getMove();
        if (dealerMove == Move.HIT)
            dealer.addCard(deck.draw(false));
        return dealerMove;
    }

    public void evaluateRound() {
        boolean dealerBusted = dealer.getHand().isBusted();
        boolean dealerBlackJack = dealer.getHand().getSize() == 2 && dealer.getScore() == rules.getRule("BlackJackScore");

        for (Participant player : players) {
            evaluatePlayerRound(player, dealerBusted, dealerBlackJack);
        }
    }

    /**
     * Evaluates the round for a given participant based on the state of the dealer's hand.
     * The method determines the outcome for standard players and players with a split hand,
     * computing the bet multiplier for their respective situations, displaying the state,
     * and issuing a payout based on the round results.
     *
     * @param player         The participant whose round is being evaluated.
     * @param dealerBusted   A boolean indicating whether the dealer has busted in this round.
     * @param dealerBlackJack A boolean indicating whether the dealer has a blackjack in this round.
     */
    private void evaluatePlayerRound(Participant player, boolean dealerBusted, boolean dealerBlackJack) {
        if (player instanceof Player) {
            double betMultiplier = evaluateHand(player.getState(), player.getHand(), dealerBusted, dealerBlackJack);
            terminal.display(player.getState().toString());
            ((Player)player).payout(betMultiplier);
        } else if (player instanceof SplitHandDecorator) {
            evaluatePlayerRound(((SplitHandDecorator) player).getOriginalParticipant(), dealerBusted, dealerBlackJack);
            Participant splitParticipant = ((SplitHandDecorator) player).getSplitParticipant();
            double betMultiplier = evaluateHand(splitParticipant.getState(), splitParticipant.getHand(), dealerBusted, dealerBlackJack);
            terminal.display(player.getState().toString());
            ((SplitHandDecorator)player).payout(betMultiplier);
        } else {
            System.err.println("Cannot evaluate round for non-player participants.");
        }
    }


    private double evaluateHand(State state, Hand hand, boolean dealerBusted, boolean dealerBlackJack) {
        if (state == State.NULL) {
            return this.rules.getRule("LOSS_MULTIPLIER");
        }

        if (dealerBlackJack) {
            return handleDealerBlackjack(state, hand);
        }

        return handleStandardOutcome(state, hand, dealerBusted);
    }

    private double handleDealerBlackjack(State state, Hand hand) {
        if (state == State.STANDING && hand.isBlackJack()) {
            return rules.getRule("BLACKJACK_MULTIPLIER");
        }
        return this.rules.getRule("LOSS_MULTIPLIER");
    }

    private double handleStandardOutcome(State state, Hand hand, boolean dealerBusted) {
        return switch (state) {
            case STANDING -> handleStanding(hand, dealerBusted);
            default -> this.rules.getRule("LOSS_MULTIPLIER");
        };
    }


    private double handleStanding(Hand hand, boolean dealerBusted) {
        if (hand.isBlackJack()) {
            return this.rules.getRule("BLACKJACK_MULTIPLIER");
        }

        if (hand.isBusted()) {
            return this.rules.getRule("LOSS_MULTIPLIER");
        }

        if (dealerBusted || hand.getScore() > dealer.getScore()) {
            return this.rules.getRule("WIN_MULTIPLIER");
        }

        if (hand.getScore() == dealer.getScore()) {
            return this.rules.getRule("TIE_MULTIPLIER");
        }

        return this.rules.getRule("LOSS_MULTIPLIER");
    }


    private void displayRoundResults() {
        terminal.display("Dealer: " + dealer.getHand());
        for (Participant player : players) {
            displayInfo(player);
        }
    }

    private void displayInfo(Participant player) {
        if (player instanceof SplitHandDecorator) {
            player = ((SplitHandDecorator) player).getOriginalParticipant();
        }
        terminal.display("Player: " + ((Player)player).getName());
        terminal.display("Chips: " + ((Player)player).getChips());
        terminal.display("Bet: " + ((Player)player).getBet());
        terminal.display("Hand: " + player.getHand());

    }

    public void addPlayer() {
        Player player = new Player();
        player.setName(terminal.requestString("enterName"));
        this.players.add(player);
    }
    public void addPlayers() {
        while (terminal.requestBoolean("addPlayer")) {
            addPlayer();
        }
    }
}

