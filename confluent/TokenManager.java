import java.util.*;

/**
 * Confluent OA: tokens with create_time + TTL.
 * (Very close to LC 1797 Design Authentication Manager.)
 *
 * generate(tokenId, currentTime): token expires at currentTime + ttl.
 * renew(tokenId, currentTime): extend if not yet expired.
 * getToken(tokenId, currentTime): true if token is still alive.
 * getCurrentTokens(currentTime): list of unexpired tokens.
 *
 * Expired at time t means expireTime <= t (LC 1797 semantics).
 *
 * Data structures:
 * - HashMap<id, expireTime> for O(1) lookup.
 * - TreeMap<expireTime, Set<id>> so cleanup / listing is O(expired * log n)
 *   instead of scanning everything (the naive version is fine to start with).
 */
public class TokenManager {

    private final int ttl;
    private final Map<String, Integer> expireAt = new HashMap<>();
    private final TreeMap<Integer, Set<String>> byExpire = new TreeMap<>();

    public TokenManager(int ttl) { this.ttl = ttl; }

    public void generate(String id, int now) {
        evict(now);
        remove(id);
        add(id, now + ttl);
    }

    public void renew(String id, int now) {
        evict(now);
        if (!expireAt.containsKey(id)) return;
        remove(id);
        add(id, now + ttl);
    }

    public boolean getToken(String id, int now) {
        evict(now);
        return expireAt.containsKey(id);
    }

    public List<String> getCurrentTokens(int now) {
        evict(now);
        List<String> res = new ArrayList<>();
        for (Set<String> ids : byExpire.values()) res.addAll(ids); // ordered by expiry
        return res;
    }

    public int countUnexpired(int now) {
        evict(now);
        return expireAt.size();
    }

    private void add(String id, int exp) {
        expireAt.put(id, exp);
        byExpire.computeIfAbsent(exp, k -> new LinkedHashSet<>()).add(id);
    }

    private void remove(String id) {
        Integer exp = expireAt.remove(id);
        if (exp == null) return;
        Set<String> s = byExpire.get(exp);
        s.remove(id);
        if (s.isEmpty()) byExpire.remove(exp);
    }

    private void evict(int now) {
        while (!byExpire.isEmpty() && byExpire.firstKey() <= now) {
            for (String id : byExpire.pollFirstEntry().getValue()) expireAt.remove(id);
        }
    }

    public static void main(String[] args) {
        TokenManager tm = new TokenManager(5);
        tm.renew("aaa", 1);                               // no-op, doesn't exist
        tm.generate("aaa", 2);                            // expires 7
        System.out.println(tm.countUnexpired(6));         // 1
        tm.generate("bbb", 7);                            // expires 12; aaa expired at 7
        tm.renew("aaa", 8);                               // no-op, expired
        tm.renew("bbb", 10);                              // expires 15
        System.out.println(tm.getCurrentTokens(10));      // [bbb]
        System.out.println(tm.getToken("aaa", 10));       // false
        System.out.println(tm.countUnexpired(15));        // 0
    }
}
