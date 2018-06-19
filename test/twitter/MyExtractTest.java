package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

public class MyExtractTest {
    
    /*
     * Additional testing for getHashtags
     * Partition strategy:
     * Number of hashtags in one tweet: 0, 1, 2+
     * Duplicate hashtags:
     *      Same case
     *      Different cases
     * Hashtag placement:
     *      At start of string
     *      Intermediate
     *      At end of string
     * Multiple tweets: 
     *      {0 hashtags, 1+ hashtags}
     *      {1+ hashtags, 1+ hashtags}
     * False hashtags:
     *      "#" with end of string following
     *      "#" preceded by a non-whitespace char
     *      "#" followed by punctuation chars
     *      
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "we're #1 in west texas", d1);
    private static final Tweet tweet3 = new Tweet(3, "conanthebarbarian", "#crush your enemies", d3);
    private static final Tweet tweet4 = new Tweet(4, "davit", "#.", d1);
    private static final Tweet tweet5 = new Tweet(5, "eggbert", "i know how to #email!", d3);
    private static final Tweet tweet6 = new Tweet(6, "francisco", "i sent an #EMAIL to my #crush", d2);
    private static final Tweet tweet7 = new Tweet(7, "grady", "i#emailed #rush#limbaugh", d3);
    private static final Tweet tweet8 = new Tweet(8, "hubert", "#", d2);
    
    // Covers 0 hashtags
    @Test
    public void testGetHashtagsNoMention() {
        Set<String> hashtags = Extract.getHashtags(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", hashtags.isEmpty());
    }
    
    // Covers 1 hashtag, at beginning of string
    @Test
    public void testGetHashtagsOneMention() {
        Set<String> hashtags = Extract.getHashtags(Arrays.asList(tweet3));
        System.out.println("hashtags size: " + hashtags.size() + " contents: " + hashtags.toString());
        
        Set<String> theseHashtags = new HashSet<String>();
        theseHashtags.add("crush");
        
        assertTrue("expected hashtags: crush", SetCompareIgnoreCase.equals(hashtags, theseHashtags));
    }
    
    // Covers two distinct, one duplicate (case-insensitive)
    @Test
    public void testGetHashtagsTwoDistinct() {
        Set<String> hashtags = Extract.getHashtags(Arrays.asList(tweet3, tweet6));
        
        Set<String> theseHashtags = new HashSet<String>();
        theseHashtags.add("email");
        theseHashtags.add("crush");
        
        assertTrue("expected hashtags: email, crush", SetCompareIgnoreCase.equals(hashtags, theseHashtags));
    }
    
    // Covers case-sensitive duplicates
    @Test
    public void testGetHashtagsTwoExact() {
        Set<String> hashtags = Extract.getHashtags(Arrays.asList(tweet5, tweet6));
        
        Set<String> theseHashtags = new HashSet<String>();
        theseHashtags.add("eMaIl");
        theseHashtags.add("cRuSh");
        
        assertTrue("expected hashtags: email, crush", SetCompareIgnoreCase.equals(hashtags, theseHashtags));
    }
    
    // Covers fake hashtags
    @Test
    public void testGetHashtagsFake() {
        Set<String> hashtags = Extract.getHashtags(Arrays.asList(tweet4, tweet7, tweet8));
        
        Set<String> theseHashtags = new HashSet<String>();
        theseHashtags.add("rush");
        
        assertTrue("expected hashtags: rush", SetCompareIgnoreCase.equals(hashtags, theseHashtags));
    }
    
}
