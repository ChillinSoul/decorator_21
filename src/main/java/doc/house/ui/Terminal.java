package doc.house.ui;

import doc.house.helpers.MessageDictionary;
import doc.house.business.*;
import doc.house.dataTypes.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Terminal {

    private final MessageDictionary dictionary = new MessageDictionary("messages");
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Requests an {@link Integer} input from the user.
     *
     * @param message The message to display to prompt the user for input.
     * @return The {@link Integer} input from the user.
     */
    public int requestInt(String message) {
        System.out.print(message);
        try {
            int inInt = scanner.nextInt();
            scanner.nextLine();
            return inInt;
        } catch (Exception e) {
            System.out.println(dictionary.getMessage("errorMustBeNumber"));
            scanner.nextLine();
            return requestInt(message);
        }
    }

    /**
     * Requests a {@link String} input from the user.
     *
     * @param message The message to display to prompt the user for input.
     * @return The {@link String} input from the user.
     */
    public String requestString(String message) {
        System.out.print(dictionary.getMessage(message));
        return scanner.nextLine();
    }

    /**
     * Requests a YES/NO input from the user.
     *
     * @param message The message to display to prompt the user for input.
     * @return {@link Boolean} true if the user input is YES, false if the user input is NO.
     */
    public Boolean requestBoolean(String message) {
        System.out.print(dictionary.getMessage(message));
        String response = scanner.nextLine().toUpperCase();

        if ("YES".equals(response))
            return true;
        else if ("NO".equals(response))
            return false;
        else
            return requestBoolean("errorMustBeYesOrNo");

    }

    public Pair<Card,Card> requestPair(Hand hand){
        display("chooseTwo");
        display(hand.toString());

        int first = requestInt("chooseFirst")-1;

        while (first >= hand.getSize() || first < 0){
            first = requestInt("errChooseFirst")-1;
        }

        int second = requestInt("chooseSecond")-1;

        while (second >= hand.getSize() || second < 0){
            if(second <= 0){
                second = requestInt("chooseSecond")-1;
            }
            if(second >= hand.getSize()){
                second = requestInt("errChooseSecond")-1;
            }

            if (second == first){
                display("errChooseOtherCard");
                display(hand.toString());
                second =-1;
            }
        }
        return new Pair<>(hand.getCard(first),hand.getCard(second));
    }

    /**
     * Requests a legal move from the user.
     *
     * @param legalMoves A list of {@link Move} that the user can choose from.
     * @return The legal {@link Move} chosen by the user.
     */
    public Move requestMove(List<Move> legalMoves) {
        StringBuilder message = new StringBuilder(dictionary.getMessage("requestMove"));
        Map<String, Move> stringToMoveMap = new HashMap<>();

        for (Move move : legalMoves) {
            message.append(move.toString()).append(" ");
            stringToMoveMap.put(move.toString(), move);
        }

        String response;
        do {
            response = this.requestString(message.toString()).toUpperCase();
        } while (!stringToMoveMap.containsKey(response));

        return stringToMoveMap.get(response);
    }

    /**
     * Displays a message on the terminal.
     *
     * @param message The message to display.
     */
    public void display(String message) {
        System.out.println(message);
    }
}


