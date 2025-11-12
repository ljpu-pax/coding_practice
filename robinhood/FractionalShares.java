// On our journey to democratize finance for all, we’ve created the concept of fractional shares. Fractional shares are pieces, or fractions, of whole shares of a company or ETF.

// However, exchanges only trade in whole shares. Robinhood is required to manage the fractional portion of each trade.

// If Robinhood has 0 shares of AAPL and then a customer wishes to purchase 1.5 AAPL shares, Robinhood will need to request 2 shares from the exchange and hold on to the remaining 0.5 shares.
// If another customer requests to purchase 0.4 shares of AAPL, Robinhood can use its inventory (0.5 shares) instead of going out to the exchange and will have 0.1 shares of AAPL remaining.
// If the third customer requests 0.5 shares, Robinhood can fill 0.1 shares out of inventory but will need to go to the exchange for an additional share leaving Robinhood's inventory at 0.6 shares.
// If a customer requests a dollar based order, we need to convert it to the relevant number of shares and run through the above steps.
// Always ensure the firm has a positive quantity in inventory and has under one share after handling an order. There's no need for us to hold onto whole shares!
// Steps:

// Handle buying fractional shares.
// Handle selling fractional shares.
// Ensure inventory is less than 1 after each order.
// e.g. Customer sells AAPL for 0.75 and then another sells AAPL for 0.50 -- we have 1.25 inventory. We can sell 1 share to the market and keep our inventory small at 0.25.
// Ensure inventory is always non-negative after each order.
// e.g. Inventory is 0.2 and the customer buys 0.5 shares: ensure we end up with 0.7 shares in inventory.
// Always “flatten”! (steps 3+4)
// The final 2 digits of every integer is the decimal. e.g. 1000 = 10.00, 20 = 0.20, 100 = 1.

// Example scenario:

// Input:
// // One AAPL buy order for 0.42 shares. AAPL is currently worth $1.
// orders: [["AAPL","B","42","100"]]

// // Inventory for AAPL is currently 0.99 shares.
// inventory: [["AAPL","99"]]


// Expected Output:
// // The users buys 0.42 shares from inventory, leaving us with 0.57 shares.
// [["AAPL","57"]]
// Another example scenario:

// Input:
// // One AAPL buy order for $0.42. AAPL is currently worth $1, so that's 0.42 shares.
// orders: [["AAPL","B","$42","100"]]
// // Existing AAPL inventory is 0.50 shares.
// inventory: [["AAPL","50"]]

// Expected Output:
// // 0.50 - 0.42 = 0.08 shares leftover.
// [["AAPL","8"]]

// [execution time limit] 3 seconds (java)

// [memory limit] 1 GB

// [input] array.array.string orders

// A list of orders in the format of [$SYMBOL, $BUY_OR_SELL, $QUANTITY, $CURRENT_PRICE]. Each parameter is a string.

// $SYMBOL: Can be "AAPL", "GOOGL", "MEOOOOOW" or anything really.
// $BUY_OR_SELL: "B" or "S". B for BUY, S for SELL.
// $QUANTITY: Can be a number or a dollar amount (prefixed with $). e.g. "100" for 1 quantity or "$150" for $1.50.
// $CURRENT_PRICE: Current price of the symbol with no $ sign. e.g. "1000" for $10.

// ** All numbers are multiplied by 100 to store two significant digits. e.g. 100 = 1.00, 150 = 1.50, 1025 = 10.25 **

// [input] array.array.string inventory

// Inventory is a list of the inventory of each symbol. Each element in the list a 2 item list of [$SYMBOL, $QUANTITY] (remember quantity is multiplied by 100!).

// An example for AAPL of 0.50 shares and GOOGL of 0.75 shares would be:

// [["AAPL","50"], 
//  ["GOOG","75"]]
// [output] array.array.string

// The output is the final inventory of each symbol after iterating through each trade. This is expected to be in the same order and format as the inventory parameter.

// e.g.

// ["AAPL","58"], 
//  ["GOOG","50"]]

package robinhood;

import java.util.*;

public class FractionalShares {
    public static String[][] processOrders(String[][] orders, String[][] inventory) {
        // 把库存放到 map 中，方便快速查找
        Map<String, Integer> invMap = new HashMap<>();
        for (String[] inv : inventory) {
            invMap.put(inv[0], Integer.parseInt(inv[1]));
        }

        for (String[] order : orders) {
            String symbol = order[0];
            String type = order[1]; // B 或 S
            String qtyStr = order[2];
            int price = Integer.parseInt(order[3]); // 价格，已乘 100
            
            // 如果该 symbol 不在库存，初始化为 0
            invMap.putIfAbsent(symbol, 0);

            // 将订单数量转换为整数（已乘 100）
            int qty;
            if (qtyStr.startsWith("$")) {
                int dollarAmount = Integer.parseInt(qtyStr.substring(1));
                qty = (int) Math.round((dollarAmount * 1.0 / price) * 100);
            } else {
                qty = Integer.parseInt(qtyStr);
            }

            int currentInv = invMap.get(symbol);

            if (type.equals("B")) {
                // 买入逻辑
                if (currentInv >= qty) {
                    // 全部用库存填充
                    currentInv -= qty;
                } else {
                    // 用掉库存，剩下的去市场买整股
                    int remaining = qty - currentInv;
                    int wholeShares = (int) Math.ceil(remaining / 100.0);
                    currentInv = wholeShares * 100 - remaining;
                }
            } else {
                // 卖出逻辑
                currentInv += qty;
                if (currentInv >= 100) {
                    // 卖出整股到市场
                    int wholeShares = currentInv / 100;
                    currentInv = currentInv - wholeShares * 100;
                }
            }

            // 确保库存非负且小于 1 股
            if (currentInv < 0) currentInv = 0;
            if (currentInv >= 100) {
                int wholeShares = currentInv / 100;
                currentInv -= wholeShares * 100;
            }

            invMap.put(symbol, currentInv);
        }

        // 按输入的 inventory 顺序输出
        String[][] result = new String[inventory.length][2];
        for (int i = 0; i < inventory.length; i++) {
            String sym = inventory[i][0];
            result[i][0] = sym;
            result[i][1] = String.valueOf(invMap.get(sym));
        }
        return result;
    }

    public static void main(String[] args) {
        // 示例 1
        String[][] orders1 = {{"AAPL","B","42","100"}};
        String[][] inventory1 = {{"AAPL","99"}};
        System.out.println(Arrays.deepToString(processOrders(orders1, inventory1)));
        // 输出: [["AAPL","57"]]

        // 示例 2
        String[][] orders2 = {{"AAPL","B","$42","100"}};
        String[][] inventory2 = {{"AAPL","50"}};
        System.out.println(Arrays.deepToString(processOrders(orders2, inventory2)));
        // 输出: [["AAPL","8"]]
    }
}
