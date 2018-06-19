package twitter;

import java.util.Iterator;
import java.util.Set;

public class SetCompareIgnoreCase {
    /*
     * For testing outputs in ExtractTest and MyExtractTest
     * 
     * Performs case-insensitive set equality.
     * @param a, b: sets of Strings
     * @return boolean indicating equality
     */
    public static boolean equals(Set<String> a, Set<String>b)
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
}
