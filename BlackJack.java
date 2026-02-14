// Group members: Nanak Barring, John Chiero, Victor Alvarez, Goutham Mahesh
// Meeting Date: 2/13/2026
// Meeting Time: 6:30 PM
import java.util.Random;
import java.util.Scanner;

public class BlackJack {
    // Aray holding the suits icluding hearts, diamonds, clubs, and spades.
    private static final String[] SUITS = { "Hearts", "Diamonds", "Clubs", "Spades" };
    // Aray holding the ranks from 2 through Ace
    private static final String[] RANKS = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King",
            "Ace" };
            //Represents the full deck of 52 cards using numbers 0-51
    private static final int[] DECK = new int[52];
    //Keeps track of which card index is currently being dealt
    private static int currentCardIndex = 0;

    // Counter for number of rounds, wins, losses, ties, and win ratio
    private static int roundsPlayed = 0;
    private static int roundsWon = 0;
    private static int roundsLost = 0;
    private static int roundsTied = 0;
    private static int winRatio = 0;
    
    // Main method that initializes the game
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Introduction message
        System.out.println("Welcome to BlackJack!");
        System.out.println("Choose game mode: 1 for Single Player, 2 for Multiplayer");
        String mode = scanner.nextLine().trim();
        
        // User input to choose between singleplayer and multiplaye
        if (mode.equals("2")) {
            playMultiplayer(scanner);
        } else {
            playSingleplayer(scanner);
        }
        
        scanner.close();
    }
    
    // Singleplayer mode - original game
    private static void playSingleplayer(Scanner scanner) {
        //Intitialize the deck in order before shuffling
        initializeDeck();
        // Shuffle the deck randomly so cards are not in order 
        shuffleDeck();
        // Deal two starting cards to the player 
        int playerTotal = dealInitialPlayerCards();
        //Deal one wisible card on the deal at the start
        int dealerTotal = dealInitialDealerCards();
        //Makes it so if the player goes over 21 they automatically lose (Fixed singleplayer Call)
        playerTotal = playerTurn(scanner, playerTotal, "Player");
        //If player goes over 21, they bust and lose immediately
        if (playerTotal > 21) {
            System.out.println("You busted! Dealer wins.");
            return;
        }
        //Start the dealers's turn after the player finishes
        dealerTotal = dealerTurn(dealerTotal);
        //Compare player and dealer totals to determine winner 
        determineWinner(playerTotal, dealerTotal);
    }
    
    // Multiplayer mode - multiple players against the dealer
    private static void playMultiplayer(Scanner scanner) {
        System.out.println("Enter number of players (2-4): ");
        int numPlayers = Integer.parseInt(scanner.nextLine().trim());
        
        if (numPlayers < 2 || numPlayers > 4) {
            System.out.println("Invalid number of players. Defaulting to 2 players.");
            numPlayers = 2;
        }
        
        // Initialize deck and shuffle
        initializeDeck();
        shuffleDeck();
        
        // Store player hands and totals
        int[] playerTotals = new int[numPlayers];
        String[] playerNames = new String[numPlayers];
        boolean[] playerBusted = new boolean[numPlayers];
        
        // Get player names and deal initial cards
        for (int i = 0; i < numPlayers; i++) {
            System.out.println("\nEnter name for Player " + (i + 1) + ": ");
            playerNames[i] = scanner.nextLine().trim();
            if (playerNames[i].isEmpty()) {
                playerNames[i] = "Player " + (i + 1);
            }
        }
        
        // Deal initial cards for dealer
        int dealerTotal = dealInitialDealerCards();
        
        // Deal initial cards and run turns for each player
        for (int i = 0; i < numPlayers; i++) {
            System.out.println("\n=== " + playerNames[i] + "'s Turn ===");
            int card1 = dealCard();
            int card2 = dealCard();
            playerTotals[i] = cardValue(card1) + cardValue(card2);
            System.out.println(playerNames[i] + "'s cards: " + RANKS[card1] + " and " + RANKS[card2]);
            System.out.println("Initial total: " + playerTotals[i]);
            
            // Fixed Multiplayer Call
            playerTotals[i] = playerTurn(scanner, playerTotals[i], playerNames[i]);
            
            // Check if player busted
            if (playerTotals[i] > 21) {
                System.out.println(playerNames[i] + " busted with " + playerTotals[i] + "!");
                playerBusted[i] = true;
            }
        }
        
        // Dealer's turn
        System.out.println("\n=== Dealer's Turn ===");
        dealerTotal = dealerTurn(dealerTotal);
        
        // Determine winner for each player
        System.out.println("\n=== Game Results ===");
        for (int i = 0; i < numPlayers; i++) {
            System.out.println("\n" + playerNames[i] + ": " + playerTotals[i]);
            if (playerBusted[i]) {
                System.out.println("Result: BUST - Dealer wins!");
            } else if (dealerTotal > 21) {
                System.out.println("Result: WIN - Dealer busted!");
            } else if (playerTotals[i] > dealerTotal) {
                System.out.println("Result: WIN - Beat the dealer!");
            } else if (playerTotals[i] == dealerTotal) {
                System.out.println("Result: TIE - Push!");
            } else {
                System.out.println("Result: LOSS - Dealer wins!");
            }
        }
    }

    // Initializes the deck
    private static void initializeDeck() {
        //Loop through the deck and assign each card number
        for (int i = 0; i < DECK.length; i++) {
            DECK[i] = i;
        }
    }
    // Shuffles the deck by randomizing every value in DECK
    private static void shuffleDeck() {
        Random random = new Random();
        for (int i = 0; i < DECK.length; i++) {
            int index = random.nextInt(DECK.length);
            int temp = DECK[i];
            DECK[i] = DECK[index];
            DECK[index] = temp;
        }
        // Displays the full deck
        System.out.println("printed deck");
        for (int i = 0; i < DECK.length; i++) {
            System.out.println(DECK[i] + " ");
        }
    }
    
    // Deals initial card for the player and shows the player
    private static int dealInitialPlayerCards() {
        int card1 = dealCard();
        int card2 = dealCard();
        System.out.println("Your cards: " + RANKS[card1] + " of " + SUITS[DECK[currentCardIndex] % 4] + " and "
                + RANKS[card2] + " of " + SUITS[card2 / 13]);
        return cardValue(card1) + cardValue(card2);
    }

    // Deals initial card for the dealer and shows a card to the player
    private static int dealInitialDealerCards() {
        int card1 = dealCard();
        System.out.println("Dealer's card: " + RANKS[card1] + " of " + SUITS[DECK[currentCardIndex] % 4]);
        return cardValue(card1);
    }
//Changed the method definiton
    private static int playerTurn(Scanner scanner, int playerTotal, String playerName) {
        while (true) {
            //tells the palyer what their total is after combineing all the cards.
            //it also adds a two functions stand or hit if stand player total stays the same if hit it goes up by number drawn.
            //changed the print inside playerTurn
            System.out.println("Your total is " + playerTotal + ". Hit or stand?");                     
            String action = scanner.nextLine().toLowerCase();
            if (action.equals("hit")) {
                int newCard = dealCard();
                playerTotal += cardValue(newCard);
                System.out.println("new card index is " + newCard);
                System.out.println("You drew a " + RANKS[newCard] + " of " + SUITS[DECK[currentCardIndex] % 4]);
                if (playerTotal > 21) {
                    break;
                }
            } else if (action.equals("stand")) {
                break;
            } else {
                System.out.println("Invalid action. Please type 'hit' or 'stand'.");
            }
        }
        return playerTotal;
    }

    // Gives the dealer cards until the value is 17 or greater
    private static int dealerTurn(int dealerTotal) {
        while (dealerTotal < 17) {
            int newCard = dealCard();
            dealerTotal += cardValue(newCard);
        }
        System.out.println("Dealer's total is " + dealerTotal);
        return dealerTotal;
    }

    // Determines if the player or dealer won, depending on who has a greater score,
    // or declares the game a tie.
    private static void determineWinner(int playerTotal, int dealerTotal) {
        if (dealerTotal > 21 || playerTotal > dealerTotal) {
            System.out.println("You win!");
        } else if (dealerTotal == playerTotal) {
            System.out.println("It's a tie!");
        } else {
            System.out.println("Get better you lose :( !");
        }
    }

    // Deals a card by returning value of card and increasing the current card index
    private static int dealCard() {
        return DECK[currentCardIndex++] % 13;
    }

    private static int cardValue(int card) {
        return card < 9 ? card + 2 : 10;
    }

    int linearSearch(int[] numbers, int key) {
        int i = 0;
        for (i = 0; i < numbers.length; i++) {
            if (numbers[i] == key) {
                return i;
            }
        }
        return -1; // not found
    }
}