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
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

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
    // Calculate new weight of old query terms.
    Iterator<String> t = terms.iterator();
    Iterator<Double> w = weights.iterator();
    for (; t.hasNext(); ){
      String term = t.next();
      Double weight = w.next();
      qm.put(term, alpha*weight);
    }
    // Calculate weight of each documents terms.
    for (Iterator<PostingsEntry> it = dr.iterator(); it.hasNext();){
      int docId = it.next().docID;
      // Get terms from doc.
      String path = indexer.index.docIDs.get(""+docId);
      File f = new File(path);
      HashSet<String> words = new HashSet<String>();
      try {
        Reader reader = new FileReader(f);
        SimpleTokenizer tok = new SimpleTokenizer(reader);
        while (tok.hasMoreTokens()){
          words.add(tok.nextToken());
        }
        reader.close();
      }
      catch (IOException e){
        e.printStackTrace();
      }
      // For each term find weight in list. (Calc instead?)
      for (Iterator<String> wi = words.iterator(); wi.hasNext();){
        String word = wi.next();
        LinkedList<PostingsEntry> l = indexer.index.getPostings(word).list;
        Double weight = 0.0;
        for (Iterator<PostingsEntry> li = l.iterator(); li.hasNext(); ){
          PostingsEntry e = li.next();
          if (e.docID == docId){
            weight = e.score;
            break;
          }
        }
        // Add terms and weights from doc.
        if (qm.containsKey(word)){
          qm.put(word, qm.get(word)+b*weight);
        } else {
          qm.put(word, b*weight);
        }
      }
    }
    terms = new LinkedList<String>(qm.keySet());
    weights.clear();
    for (Iterator<String> it = terms.iterator(); it.hasNext(); ){
      weights.add(qm.get(it.next()));
    }
  }
}


