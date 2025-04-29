import java.util.*;

public class PokerHandEvaluator {

    // Enum for Suit
    enum Suit {
        CLUBS, DIAMONDS, HEARTS, SPADES, JOKER
    }

    // Enum for Rank
    enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
        EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12),
        KING(13), ACE(14), JOKER(0);

        private final int value;

        Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Card class
    static class Card {
        private final Suit suit;
        private final Rank rank;

        Card(Suit suit, Rank rank) {
            this.suit = suit;
            this.rank = rank;
        }

        public Suit getSuit() {
            return suit;
        }

        public Rank getRank() {
            return rank;
        }

        public boolean isJoker() {
            return rank == Rank.JOKER;
        }

        @Override
        public String toString() {
            return isJoker() ? "JOKER" : rank + " of " + suit;
        }
    }

    // Hand class
    static class Hand {
        private final List<Card> cards;

        Hand(List<Card> cards) {
            if (cards.size() != 5) {
                throw new IllegalArgumentException("A hand must contain exactly 5 cards.");
            }
            this.cards = cards;
        }

        public List<Card> getCards() {
            return cards;
        }

        public void printHand() {
            for (Card card : cards) {
                System.out.println("  " + card);
            }
        }
    }

    // Evaluator methods
    public static boolean isFlush(List<Card> cards) {
        int jokerCount = 0;
        Map<Suit, Integer> suitCounts = new HashMap<>();

        for (Card card : cards) {
            if (card.isJoker()) {
                jokerCount++;
            } else {
                Suit suit = card.getSuit();
                suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
            }
        }

        for (Map.Entry<Suit, Integer> entry : suitCounts.entrySet()) {
            if (entry.getValue() + jokerCount >= 5) {
                return true;
            }
        }

        return jokerCount == 5;
    }

    public static boolean isStraight(List<Card> cards) {
        Set<Integer> uniqueRanks = new HashSet<>();
        int jokerCount = 0;

        for (Card card : cards) {
            if (card.isJoker()) {
                jokerCount++;
            } else {
                uniqueRanks.add(card.getRank().getValue());
            }
        }

        List<Integer> ranks = new ArrayList<>(uniqueRanks);
        Collections.sort(ranks);

        // Check for Ace-low straight
        if (ranks.contains(14)) {
            ranks.add(1);
            Collections.sort(ranks);
        }

        for (int i = 0; i <= ranks.size() - 1; i++) {
            int count = 1;
            int current = ranks.get(i);
            int jokersUsed = 0;

            for (int j = i + 1; j < ranks.size(); j++) {
                if (ranks.get(j) == current + 1) {
                    count++;
                    current = ranks.get(j);
                } else if (ranks.get(j) > current + 1) {
                    int gap = ranks.get(j) - current - 1;
                    if (gap <= jokerCount - jokersUsed) {
                        jokersUsed += gap;
                        count += gap + 1;
                        current = ranks.get(j);
                    } else {
                        break;
                    }
                }
                if (count + (jokerCount - jokersUsed) >= 5) {
                    return true;
                }
            }
        }

        return jokerCount == 5;
    }

    public static boolean isFullHouse(List<Card> cards) {
        Map<Rank, Integer> rankCounts = new HashMap<>();
        int jokerCount = 0;

        for (Card card : cards) {
            if (card.isJoker()) {
                jokerCount++;
            } else {
                Rank rank = card.getRank();
                rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            }
        }

        List<Integer> counts = new ArrayList<>(rankCounts.values());
        Collections.sort(counts, Collections.reverseOrder());

        if (counts.size() >= 2) {
            int three = counts.get(0);
            int two = counts.get(1);
            if (three + two + jokerCount >= 5) {
                return true;
            }
        } else if (counts.size() == 1) {
            int count = counts.get(0);
            if (count + jokerCount >= 5) {
                return true;
            }
        } else {
            return jokerCount >= 5;
        }

        return false;
    }

    public static boolean isFourOfAKind(List<Card> cards) {
        Map<Rank, Integer> rankCounts = new HashMap<>();
        int jokerCount = 0;

        for (Card card : cards) {
            if (card.isJoker()) {
                jokerCount++;
            } else {
                Rank rank = card.getRank();
                rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            }
        }

        for (Map.Entry<Rank, Integer> entry : rankCounts.entrySet()) {
            if (entry.getValue() + jokerCount >= 4) {
                return true;
            }
        }

        return jokerCount == 5;
    }

    public static boolean isFiveOfAKind(List<Card> cards) {
        Map<Rank, Integer> rankCounts = new HashMap<>();
        int jokerCount = 0;

        for (Card card : cards) {
            if (card.isJoker()) {
                jokerCount++;
            } else {
                Rank rank = card.getRank();
                rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            }
        }

        for (Map.Entry<Rank, Integer> entry : rankCounts.entrySet()) {
            if (entry.getValue() + jokerCount >= 5) {
                return true;
            }
        }

        return jokerCount == 5;
    }

    // Main method for testing
    public static void main(String[] args) {
        // Example hand with a Joker
        List<Card> handCards = new ArrayList<>();
        handCards.add(new Card(Suit.HEARTS, Rank.TEN));
                handCards.add(new Card(Suit.HEARTS, Rank.JACK));
        handCards.add(new Card(Suit.HEARTS, Rank.QUEEN));
        handCards.add(new Card(Suit.HEARTS, Rank.KING));
        handCards.add(new Card(Suit.JOKER, Rank.JOKER)); // Joker as wildcard

        Hand hand = new Hand(handCards);

        System.out.println("Hand:");
        hand.printHand();

        System.out.println("\nEvaluations:");
        System.out.println("Flush: " + isFlush(hand.getCards()));
        System.out.println("Straight: " + isStraight(hand.getCards()));
        System.out.println("Full House: " + isFullHouse(hand.getCards()));
        System.out.println("Four of a Kind: " + isFourOfAKind(hand.getCards()));
        System.out.println("Five of a Kind: " + isFiveOfAKind(hand.getCards()));
    }
}
