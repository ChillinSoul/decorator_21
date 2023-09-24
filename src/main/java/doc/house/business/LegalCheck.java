package doc.house.business;

import doc.house.contract.Participant;
import doc.house.helpers.RuleBook;
import doc.house.dataTypes.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LegalCheck {

    private final RuleBook rules;
    public LegalCheck(RuleBook rules) {
        this.rules = rules;
    }
    /**
     * Get a list of legal moves for a given player state.
     *
     * @param player the player to get the legal moves for.
     * @return a list of legal moves.
     */
    public List<Move> getLegalMoves(Participant player) {
        return Arrays.stream(Move.values())
                .filter(move -> isLegal(move, player))
                .collect(Collectors.toList());
    }

    /**
     * Check if a move is legal for a given player state.
     *
     * @param move   the move to be checked.
     * @param player the player making the move.
     * @return true if the move is legal, false otherwise.
     */
    public boolean isLegal(Move move, Participant player) {
        if (player.getScore() > 21){
            player.setState(State.BUSTED);
        }
        switch (player.getState()) {
            case BUSTED:
                return false;
            case STANDING:
                return move == Move.STAND;
            default:
                break;
        }

        return switch (move) {
            case HIT, STAND -> true;
            case DOUBLE_DOWN -> player.getHand().getSize() == 2;
            case SPLIT -> player.getHand().getSize() == 2 &&
                    player.getHand().getCard(0).isEquivalentTo(player.getHand().getCard(1)) &&
                    player.getHand().getCard(0).getValue() != 1;
            default -> false;
        };

    }
    public Pair<Integer,Integer> getBetRange(Player player){
        return new Pair<>(
                rules.getRule("MinBet"),
                Math.min(player.getChips(),rules.getRule("MaxBet")));
    }
}




