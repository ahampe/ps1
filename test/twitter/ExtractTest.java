/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     * 
     * Test strategy for getTimespan(): 
     * Partition:
     * tweets.length() = 0, 1, 2, 3+
     * reorder Instant chronology: forward (d1 < d2 < d3), reversed (d3 > d2 > d1), zigzag (d2 < d3 > d1)
     * list of equal Instants
     * 
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "conanthebarbarian", "crush your enemies", d3);
    private static final Tweet tweet4 = new Tweet(4, "davit", "holla @shawty", d1);
    private static final Tweet tweet5 = new Tweet(5, "eggbert", "email me: egg@bert.co.uk", d3);
    private static final Tweet tweet6 = new Tweet(6, "francisco", "hola @shawty", d2);
    private static final Tweet tweet7 = new Tweet(7, "grady", "hello @SHAWTY my name is @JUDGE", d3);
    private static final Tweet tweet8 = new Tweet(8, "hubert", "@Crammed@Together", d2);
    private static final Tweet tweet9 = new Tweet(9, "ichabod", "I am @_ichabod, not @shaw-ty", d2);

    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Covers tweets.length() = 0
    @Test(expected=AssertionError.class) // Possible that another error is raised, make sure
    public void testGetTimespanNoTweet() {
        Timespan badSpan = Extract.getTimespan(new ArrayList<Tweet>());
    }
    
    // Covers tweets.length() = 1
    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }
    
    // Covers tweets.length() = 2
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    // Covers tweets.length() = 3+ and forward chronology
    @Test
    public void testGetTimespanThreeTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    // Covers reversed chronology
    @Test
    public void testGetTimespanReversedTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet2, tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    // Covers zigzag chronology
    @Test
    public void testGetTimespanZigzagTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet3, tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    // Covers equal Instants
    @Test
    public void testGetTimespanAllSameInstant() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet4));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }
    
    /*
     * Test strategy for getMentionedUsers():
     * 
     * Partition:
     * Number of mentions in one tweet: 0, 1, 2+
     * Duplicate user mentions:
     *      Same case
     *      Different cases
     * Mention placement:
     *      At start of string
     *      Intermediate
     *      At end of string
     * Multiple tweets: 
     *      {0 mentions, 1+ mentions}
     *      {1+ mentions, 1+ mentions}
     * "False" mentions:
     *      "@" preceded by a valid char
     *      "@" followed by an invalid char: "%", " ", ":" (should pass)
     * Unconventional usernames:
     *      "@" with underscores or hyphens
     */
    
    /*
     * Performs case-insensitive set equality.
     * @param a, b: sets of Strings
     * @return boolean indicating equality
     */
    boolean setEqualsIgnoreCase(Set<String> a, Set<String>b)
    {
        if (a.size() != b.size()) return false;
        Iterator<String> ai = a.iterator();
        Iterator<String> bi = b.iterator();
        while(ai.hasNext())
        {
             if (!ai.next().equalsIgnoreCase(bi.next())) return false;
        }
        return true;
    }
    
    // Covers 0 mentions
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    // Covers 1 mention, at end of string
    @Test
    public void testGetMentionedUsersOneMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet4));
        
        Set<String> theseUsers = new HashSet<String>();
        theseUsers.add("shawty");
        
        assertTrue("expected mentions", setEqualsIgnoreCase(mentionedUsers, theseUsers));
    }
    
    // Covers two distinct, one duplicate (case-insensitive)
    @Test
    public void testGetMentionedUsersTwoDistinct() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet4, tweet7));
        
        Set<String> theseUsers = new HashSet<String>();
        theseUsers.add("shawty");
        theseUsers.add("judge");
        
        assertTrue("expected mentions", setEqualsIgnoreCase(mentionedUsers, theseUsers));
    }
    
    // Covers one case-sensitive duplicate
    @Test
    public void testGetMentionedUsersTwoExact() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet4, tweet6));
        
        Set<String> theseUsers = new HashSet<String>();
        theseUsers.add("SHawTY");
        
        assertTrue("expected mentions", setEqualsIgnoreCase(mentionedUsers, theseUsers));
    }
    
    // Covers fake mentions, one mention at start of string
    @Test
    public void testGetMentionedUsersFakeMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5, tweet8));
        
        Set<String> theseUsers = new HashSet<String>();
        theseUsers.add("crammed");
        
        assertTrue("expected mentions", setEqualsIgnoreCase(mentionedUsers, theseUsers));
    }
    
    // Covers unconventional usernames, invalid char following mention
    @Test
    public void testGetMentionedUsersUnconventional() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet9));
        
        Set<String> theseUsers = new HashSet<String>();
        theseUsers.add("_ichabod");
        theseUsers.add("shaw-ty");
        
        assertTrue("expected mentions", setEqualsIgnoreCase(mentionedUsers, theseUsers));
    }
}
