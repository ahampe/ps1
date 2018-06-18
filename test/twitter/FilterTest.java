/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     * 
     * Partition for writtenBy:
     * 0 matches for given author: empty and non-empty lists
     * 1, 2+ matches for given author: matching case and non-matching case
     * Tweets not in chronological order
     * 
     * Partition for inTimespan:
     * Empty tweet list
     * All tweets in timespan, no tweets in timespan, some tweets in timespan
     * One tweet at start of span, one tweet at end of span
     * Tweets not in chronological order
     * 
     * Partition for containing:
     * Exact word match, case mismatch, partial word match
     * Tweets with 2+ matching words
     * No matches, all matches
     * 
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T11:01:00Z");
    private static final Instant d4 = Instant.parse("2016-02-17T11:02:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "another tweet", d3);
    private static final Tweet tweet4 = new Tweet(4, "ch_ad", "this is another tweet...", d4);

    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // --- Tests for writtenBy ---
    
    // Covers 0 matches for given author, empty list
    @Test
    public void testWrittenByOneTweetNoResultEmpty() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(), "alyssa");
        
        assertEquals("expected empty list", 0, writtenBy.size());
    }
    
    // Covers 0 matches for given author, non-empty list
    @Test
    public void testWrittenByOneTweetNoResultNonempty() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet2), "ch_ad");
        
        assertEquals("expected empty list", 0, writtenBy.size());
    }
    
    // Covers 1 match for given author
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    // Covers 2+ matches for given author, different cases
    @Test
    public void testWrittenByMultipleMatchesDiffCases() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "AlySsA");
        
        assertEquals("expected list length 2", 2, writtenBy.size());
        assertTrue("expected list to contain tweet1", writtenBy.contains(tweet1));
        assertTrue("expected list to contain tweet3", writtenBy.contains(tweet3));
    }
    
    // Covers 2+ matches for given author, tweets not in chronological order
    @Test
    public void testWrittenByMultipleMatchesOutOfOrder() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet3, tweet2, tweet1), "alyssa");
        
        assertEquals("expected list length 2", 2, writtenBy.size());
        assertTrue("expected list to contain tweet1", writtenBy.contains(tweet1));
        assertTrue("expected list to contain tweet3", writtenBy.contains(tweet3));
        assertEquals("expected same order", 0, writtenBy.indexOf(tweet3));
    }
    
    // --- Tests for inTimespan ---
    
    // Covers all tweets in timespan
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    // Covers no tweets in timespan
    @Test
    public void testInTimespanMultipleTweetsNoResults() {
        Instant testStart = Instant.parse("2016-02-17T10:30:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:31:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    // Covers some tweets in timespan, one at start, one at end
    @Test
    public void testInTimespanMultipleTweetsSomeResults() {
        Instant testStart = d2;
        Instant testEnd = d4;
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet4), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet2, tweet4)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet2));
    }
    
    // Covers some tweets in timespan, not in chronological order
    @Test
    public void testInTimespanMultipleTweetsOutOfOrder() {
        Instant testStart = d2;
        Instant testEnd = d4;
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet4, tweet2, tweet1), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet2, tweet4)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet4));
    }
    
    // --- Tests for containing ---
    
    // Covers no matches
    @Test
    public void testContainingNoMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("biophysics"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    // Covers all matches, exact case
    @Test
    public void testContainingAllExactMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    
    // Covers one match and partial match
    @Test
    public void testContainingPartialMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet3, tweet4), Arrays.asList("tweet"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet3)));
    }
    
    // Covers some matches, one tweet with 2+ matching words, non-matching case
    @Test
    public void testContainingSomeMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet4), Arrays.asList("IS", "Another"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet4)));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
