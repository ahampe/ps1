/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        assert tweets.size() > 0;
        Instant start = Instant.MAX;
        Instant end = Instant.MIN;
        
        for (Tweet tweet : tweets) {
            Instant tstamp = tweet.getTimestamp();
            if (tstamp.isBefore(start)) {
                start = tstamp;
            }
            if (tstamp.isAfter(end)) {
                end = tstamp;
            }
        }
        
        return new Timespan(start, end);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username. (???)
     *         (The above statement is unclear. A valid mention MUST be followed by
     *          a valid username char, otherwise it is not a valid mention. 
     *          Perhaps this was intended to say that an INVALID char following a
     *          mention is invalid, but I assume that such a mention is valid.)
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         (But only because a valid character precedes the "@", presumably?)
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> mentioned = new HashSet<String>();
        
        // Normally a Twitter handle is at most 14 chars, but
        // this is not part of the spec for this pset.
        // Realistically, the regex should be "(?:^|[^\\w-])@([\\w-]{1,14})"
        Pattern mentionPattern = Pattern.compile("(?:^|[^\\w-])@([\\w-]+)");
        
        for (Tweet tweet : tweets) {
            String txt = tweet.getText();
            Matcher m = mentionPattern.matcher(txt);
            while (m.find()) {
              mentioned.add(m.group(1).toLowerCase());
            }
        }
        
        return mentioned;
    }
    
    /**
     * Get hashtags mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of hashtags that are found in the text of the tweets.
     *         a hashtag is defined by a "#" followed by at least one letter character,
     *         followed by zero or more alphanumeric characters. It is assumed that all 
     *         of the characters in the hashtag are in the English alphabet or in the digits 0-9.
     *         Hashtags are not case-sensitive, and the returned set will be may include
     *         a hashtag at most once.
     */
    public static Set<String> getHashtags(List<Tweet> tweets) {
        Set<String> hashtags = new HashSet<String>();
        
        Pattern hashtagPattern = Pattern.compile("(?:^|\\s)\\#([A-Za-z]\\w*)");
        
        for (Tweet tweet : tweets) {
            String txt = tweet.getText();
            Matcher m = hashtagPattern.matcher(txt);
            while (m.find()) {
                hashtags.add(m.group(1).toLowerCase());
            }
        }
        
        return hashtags;
    }

}
