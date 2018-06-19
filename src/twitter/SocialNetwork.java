/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> follows = new HashMap<String, Set<String>>();
        // map hashtags to people who have used them in their tweets:
        Map<String, Set<String>> tagUsers = new HashMap<String, Set<String>>();
        
        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor();
            Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(tweet));
            
            if (!mentioned.isEmpty()) {
                if (follows.containsKey(author)) {
                    Set<String> following = follows.get(author);
                    following.addAll(mentioned);
                }
                else {
                    follows.put(author, mentioned);
                }
            }
            
            Set<String> hashtags = Extract.getHashtags(Arrays.asList(tweet));
            if (!hashtags.isEmpty()) {
                for (String hashtag : hashtags) {
                    if (tagUsers.containsKey(hashtag)) {
                        Set<String> users = tagUsers.get(hashtag);
                        users.add(author);
                    }
                    else {
                        tagUsers.put(hashtag, new HashSet<String>(Arrays.asList(author)));
                    }
                }
            }
        }
        
        // Users with common hashtags will mutually follow each other:
        if (!tagUsers.isEmpty()) {
            for (Set<String> users : tagUsers.values()) {
                if (users.size() > 1) {
                    for (String user : users) {
                        Set<String> newFollows = new HashSet<String>();
                        for (String other : users) {
                            if (user.equals(other)) {
                                continue;
                            }
                            newFollows.add(other);
                        }
                        
                        if (follows.containsKey(user)) {
                            Set<String> following = follows.get(user);
                            following.addAll(newFollows);
                        }
                        else {
                            follows.put(user, newFollows);
                        }
                    }
                }
            }
        }
        
        return follows;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        List<String> influencers = new ArrayList<String>();
        Map<String, Integer> mentionCount = new HashMap<String, Integer>();
        
        for (Set<String> valSet : followsGraph.values()) {
            for (String mention : valSet) {
                if (mentionCount.containsKey(mention)) {
                    Integer numMtns = mentionCount.get(mention);
                    mentionCount.put(mention, numMtns + 1);
                }
                else {
                    mentionCount.put(mention, 1);
                    influencers.add(mention);
                }
            }
        }
        
        // Sorts in descending order based on mention count
        Collections.sort(influencers, new Comparator<String>() {
            public int compare(String first, String second) {
                return mentionCount.get(second).compareTo(mentionCount.get(first));
            }
        });
        
        return influencers;
    }

}
