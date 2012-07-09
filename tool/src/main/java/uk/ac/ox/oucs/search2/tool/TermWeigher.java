package uk.ac.ox.oucs.search2.tool;

import java.text.Collator;
import java.util.*;

/**
 * @author Colin Hebert
 */
public class TermWeigher {
    /**
     * Change the way the weight is determined.
     * If it's relative the most important Term will have the maximum weight whereas the least important term will have the minimum weight.
     * If it isn't relative, each term will have a weight depending on its frequency only.
     * If the most and least important terms have the same frequency, the relative mode still changes the weight of the term
     * whereas the non relative mode will set the same weight to every term.
     */
    private boolean relativeWeigher = true;

    private int maxTerms = 50;

    private double maxWeight = 3.0;

    private int numberOfTerms;

    private long highestFrequency;

    private Comparator<Term> postSort = ALPHABETICAL_SORT;

    public static final Comparator<Term> ALPHABETICAL_SORT = new Comparator<Term>() {
        @Override
        public int compare(Term o1, Term o2) {
            return Collator.getInstance().compare(o1.getTerm(), o2.getTerm());
        }
    };

    public static final Comparator<Term> WEIGHT_SORT = new Comparator<Term>() {
        @Override
        public int compare(Term o1, Term o2) {
            return Double.compare(o1.weight, o2.weight);
        }
    };
    public static final Comparator<Term> FREQUENCY_SORT = new Comparator<Term>() {
        @Override
        public int compare(Term o1, Term o2) {
            return (int) (o1.frequency - o2.frequency);
        }
    };

    public List<Term> getWeighedTerms(Map<String, Long> allTerms) {
        List<Term> terms = getTopTerms(allTerms);
        if (!relativeWeigher)
            highestFrequency = getHighestFrequency(terms);

        int i = 0;
        for (Term term : terms) {
            term.position = i++;
            term.weight = getWeight(term);
        }
        Collections.sort(terms, postSort);
        return terms;
    }

    private List<Term> getTopTerms(Map<String, Long> termMap) {
        List<Term> terms = new ArrayList<Term>(termMap.size());
        for (Map.Entry<String, Long> termEntry : termMap.entrySet()) {
            terms.add(new Term(termEntry.getKey(), termEntry.getValue()));
        }
        Collections.sort(terms, FREQUENCY_SORT);
        if (terms.size() > maxTerms)
            terms = terms.subList(0, maxTerms);
        numberOfTerms = terms.size();

        return terms;
    }

    private long getHighestFrequency(Iterable<Term> terms) {
        long maxFrequency = 0;
        for (Term term : terms) {
            if (term.frequency > maxFrequency)
                maxFrequency = term.frequency;
        }
        return maxFrequency;
    }

    private double getWeight(Term term) {
        if (relativeWeigher)
            return maxWeight * (numberOfTerms - term.position) / numberOfTerms;
        else
            return maxWeight * term.frequency / highestFrequency;
    }

    public static class Term {
        private final String term;
        private final long frequency;
        private int position;
        private Double weight;

        public Term(String term, long frequency) {
            this.term = term;
            this.frequency = frequency;
        }

        public String getTerm() {
            return term;
        }

        public long getFrequency() {
            return frequency;
        }

        public double getWeight() {
            if (weight == null)
                throw new IllegalStateException("Cannot get a term weight before it has been weighed");
            return weight;
        }
    }

    public void setMaxTerms(int maxTerms) {
        this.maxTerms = maxTerms;
    }

    public void setRelativeWeigher(boolean relativeWeigher) {
        this.relativeWeigher = relativeWeigher;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public void setPostSort(Comparator<Term> postSort) {
        this.postSort = postSort;
    }
}
