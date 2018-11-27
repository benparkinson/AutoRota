package com.parkinsonhardy.autorota.rules;

import java.util.HashSet;
import java.util.Set;

public class IntegerMatcher {

    private Set<Integer> intsThatMatch;

    public IntegerMatcher(int... intsThatMatch) {
        this.intsThatMatch = new HashSet<>();
        for (int i : intsThatMatch) {
            this.intsThatMatch.add(i);
        }
    }

    public boolean matches(int candidate) {
        return intsThatMatch.contains(candidate);
    }

}
