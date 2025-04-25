import java.util.*;

class Card implements Comparable<Card> {
    int value;

    Card(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(Card other) {
        return Integer.compare(this.value , other.value);
    }
}

class Player {
    List<Card> hand = new ArrayList<>();

    void drawCard(Deque<Card> deck) {
        if (!deck.isEmpty()) {
            hand.add(deck.poll());
            Collections.sort(hand);
        }
    }

    Card playCard() {
        if (!hand.isEmpty()) {
            return hand.remove(0);
        }

        return null;
    }
}

public class Game {
    int playerCount;
    int lives;
    int skipLimit;
    int rounds;
    List<Player> players = new ArrayList<>();
    Deque<Card> deck = new ArrayDeque<>();
    
    public Game(int playerCount, int skipLimit, int rounds) {
        this.playerCount = playerCount;
        this.lives = playerCount + 1;
        this.skipLimit = skipLimit;
        this.rounds = rounds;
        initializePlayers();
    }
        
    void initializePlayers() {
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player());
        }
    }

    void initializeDeck() {
        List<Card> deckList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            deckList.add(new Card(i));
        }
        Collections.shuffle(deckList);
        deck = new ArrayDeque<>(deckList);
    }

    void dealCards() {
        for (Player player : players) {
            player.hand.clear();
            for (int i = 0; i < 3; i++) {
                player.drawCard(deck);
            }
        }
    }

    boolean playRound() {
        List<Card> playedCards = new ArrayList<>();

        for (Player player : players) {
            Card card = player.playCard();
            if (card != null) {
                playedCards.add(card);
            } else {
                // 玩家没有牌可出，自动失败
                return false;
            }
        }

        for (int i = 1; i < playedCards.size(); i++) {
            if (playedCards.get(i).value <= playedCards.get(i - 1).value) {
                return false; // 出牌不严格递增，失败
            }
        }

        return true;
    }

    void startGame() {
        initializeDeck();
        for (int round = 1; round <= rounds; round++) {
            dealCards();
            boolean success = playRound();
            if (!success) {
                if (skipLimit > 0) {
                    skipLimit--;
                    System.out.println("第 " + round + " 轮失败，使用一次跳过机会。剩余跳过次数：" + skipLimit);
                } else if (lives > 0) {
                    lives--;
                    System.out.println("第 " + round + " 轮失败，失去一条生命。剩余生命数：" + lives);
                } else {
                    System.out.println("游戏失败，无法继续。");
                    return;
                }
            } else {
                System.out.println("第 " + round + " 轮成功！");
            }
        }
        System.out.println("游戏成功完成所有轮次！");
    }

    public static void main(String[] args) {
        Game game = new Game(3, 2, 5); // 3位玩家，2次跳过机会，5轮游戏
        game.startGame();
    }

}
