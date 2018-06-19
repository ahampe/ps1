package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class MySocialNetworkTest {
    
    /*
     * Additional tests on guessFollowsGraph:
     * No common hashtags, common hashtags
     * 
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about #rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype #rivest", d2);
    private static final Tweet tweet4 = new Tweet(4, "davit", "#holla @shawty", d1);
    private static final Tweet tweet5 = new Tweet(5, "eggbert", "@alyssa @thulsadoom hey whats up #holla", d3);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // --- Tests for guessFollowsGraph ---
    
    // Covers no common hashtags
    @Test
    public void testGuessFollowsGraphNoCommonHashtag() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2, tweet4));
        
        assertFalse("expected non-empty graph", followsGraph.isEmpty());
        assertEquals("expected size", 1, followsGraph.size());
        assertTrue("expected followers", followsGraph.get("davit").equals(new HashSet<String>(Arrays.asList("shawty"))));
    }
    
    // Covers common hashtags
    @Test
    public void testGuessFollowsGraphCommonHashtags() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2, tweet4, tweet5));
        
        assertFalse("expected non-empty graph", followsGraph.isEmpty());
        assertEquals("expected size", 4, followsGraph.size());
        assertTrue("expected followers", followsGraph.get("alyssa").equals(new HashSet<String>(Arrays.asList("bbitdiddle"))));
        assertTrue("expected followers", followsGraph.get("bbitdiddle").equals(new HashSet<String>(Arrays.asList("alyssa"))));
        assertTrue("expected followers", followsGraph.get("davit").equals(new HashSet<String>(Arrays.asList("shawty", "eggbert"))));
        assertTrue("expected followers", followsGraph.get("eggbert").equals(new HashSet<String>(Arrays.asList("alyssa", "thulsadoom", "davit"))));

    }
}
