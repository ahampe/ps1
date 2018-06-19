/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     * 
     * Partition for guessFollowsGraph:
     * Empty list of tweets, tweets without any mentions, tweets with multiple mentions
     * All distinct user mentions, duplicate mentions
     * (Later: no common hashtags, all shared hashtags)
     * 
     * Partition for influencers:
     * Empty map, map with empty value(s) (guaranteed to never happen with guessFollowsGraph, 
     *      but still worth testing)
     * All equal influencers, Unequal influencers
     * 
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about #rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype #rivest", d2);
    private static final Tweet tweet3 = new Tweet(3, "conanthebarbarian", "crush @thulsadoom", d3);
    private static final Tweet tweet4 = new Tweet(4, "davit", "#holla @shawty", d1);
    private static final Tweet tweet5 = new Tweet(5, "eggbert", "@davit @alyssa @thulsadoom hey whats up #holla", d3);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // --- Tests for guessFollowsGraph ---
    
    // Covers empty graph
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    // Covers no mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2));
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    // Covers all distinct mentions
    @Test
    public void testGuessFollowsGraphDistinctMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3, tweet4));
        
        assertFalse("expected non-empty graph", followsGraph.isEmpty());
        assertEquals("expected size", 2, followsGraph.size());
        assertTrue("expected followers", followsGraph.get("conanthebarbarian").equals(new HashSet<String>(Arrays.asList("thulsadoom"))));
        assertTrue("expected followers", followsGraph.get("davit").equals(new HashSet<String>(Arrays.asList("shawty"))));
    }
    
    // Covers multiple mentions in one tweet, duplicate mentions
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3, tweet4, tweet5));
        
        assertFalse("expected non-empty graph", followsGraph.isEmpty());
        assertEquals("expected size", 3, followsGraph.size());
        assertTrue("expected followers", followsGraph.get("eggbert").equals(new HashSet<String>(Arrays.asList("davit", "alyssa", "thulsadoom"))));
    }
    
    // --- Tests for influencers ---
    
    // Covers empty list
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    // Covers map of empty values
    @Test
    public void testInfluencersEmptyMapVals() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("foo", new HashSet<String>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    // Covers all equal influencers
    @Test
    public void testInfluencersEqual() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("james", new HashSet<String>(Arrays.asList("hank", "gretchen")));
        followsGraph.put("bob", new HashSet<String>(Arrays.asList("mark", "james")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertFalse("expected non-empty list", influencers.isEmpty());
        assertEquals("expected size", 4, influencers.size());
        assertTrue("expected influencers", influencers.containsAll(Arrays.asList("hank", "gretchen", "mark", "james")));
    }
    
    // Covers all unequal influencers
    @Test
    public void testInfluencersUnequal() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("grady", new HashSet<String>(Arrays.asList("nobody", "bigshot", "localceleb")));
        followsGraph.put("james", new HashSet<String>(Arrays.asList("bigshot")));
        followsGraph.put("bob", new HashSet<String>(Arrays.asList("localceleb", "bigshot")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertFalse("expected non-empty list", influencers.isEmpty());
        assertEquals("expected size", 3, influencers.size());
        assertEquals("expected first influencer", "bigshot", influencers.get(0));
        assertEquals("expected first influencer", "localceleb", influencers.get(1));
        assertEquals("expected first influencer", "nobody", influencers.get(2));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
