/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Hedvig Kjellström, 2012
 */  

package ir;

import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

public class Query {

  public LinkedList<String> terms = new LinkedList<String>();
  public LinkedList<Double> weights = new LinkedList<Double>();

  /**
   *  Creates a new empty Query 
   */
  public Query() {
  }

  /**
   *  Creates a new Query from a string of words
   */
  public Query( String queryString  ) {
    StringTokenizer tok = new StringTokenizer( queryString );
    HashMap<String, Integer> t = new HashMap<String, Integer>();
    while ( tok.hasMoreTokens() ) {
      String term = tok.nextToken();
      if (t.containsKey(term)){
        t.put(term, t.get(term)+1);
      } else {
        t.put(term, 1);
      }
    }
    double norm = 0;
    for (Iterator<String> it = t.keySet().iterator(); it.hasNext();){
      norm += Math.pow(t.get(it.next()), 2);
    }
    norm = Math.sqrt(norm);
    for (Iterator<String> it = t.keySet().iterator(); it.hasNext();){
      String term = it.next();
      terms.add( term );
      weights.add( new Double((double)t.get(term)/norm) );
    }
  }

  /**
   *  Returns the number of terms
   */
  public int size() {
    return terms.size();
  }

  /**
   *  Returns a shallow copy of the Query
   */
  public Query copy() {
    Query queryCopy = new Query();
    queryCopy.terms = (LinkedList<String>) terms.clone();
    queryCopy.weights = (LinkedList<Double>) weights.clone();
    return queryCopy;
  }

  /**
   *  Expands the Query using Relevance Feedback
   */
  public void relevanceFeedback( PostingsList results, boolean[] docIsRelevant, Indexer indexer ) {
    // results contain the ranked list from the current search
    // docIsRelevant contains the users feedback on which of the 10 first hits are relevant

    ArrayList<PostingsEntry> dr = new ArrayList<PostingsEntry>();
    for (int i = 0; i < 10; i++){
      if (docIsRelevant[i]) dr.add(results.get(i));
    }
    // If no feedback was given, just do a normal query.
    if (dr.size() == 0){
      return;
    }

    // Update the query with new terms and weights according to the feedback.
    // qm = alpha*q0 + beta*(1/dr.size())*sum(v(d)) for all d in dr.
    double alpha = 0.1;
    double beta = 0.9;
    double b = beta/dr.size();
    HashMap<String, Double> qm = new HashMap<String, Double>();
    Iterator<String> t = terms.iterator();
    Iterator<Double> w = weights.iterator();
    for (; t.hasNext(); ){
      String term = t.next();
      Double weight = w.next();
      qm.put(term, alpha*weight);
    }
    for (Iterator<PostingsEntry> it = dr.iterator(); it.hasNext();){
      int docId = it.next().docID;
      // get all words in doc with docID = docId.
      // for each word add weight = b*score.
    }
  }
}


