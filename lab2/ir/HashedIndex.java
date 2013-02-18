/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 *   Additions: Hedvig Kjellström, 2012
 */  


package ir;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collections;


/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {

    /** The index as a hashtable. */
    private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();


    /**
     *  Inserts this token in the index.
     */
    public void insert( String token, int docID, int offset ) {
	//
	//  YOUR CODE HERE
	//
    	if (!index.containsKey(token)) {
    		index.put(token, new PostingsList());
    	}
    	index.get(token).add(docID, offset);
    }


    public void calculateWeights( int nbDocs ) {
      double[] norm = new double[nbDocs];
      for (Iterator<PostingsList> it = index.values().iterator(); it.hasNext();) {
        PostingsList p = it.next();
        for (ListIterator<PostingsEntry> it2 = p.getIterator(); it2.hasNext();) {
          PostingsEntry e = it2.next();
          norm[e.docID] += e.size()*e.size();
        }
      }
      for (int i = 0; i < nbDocs; i++) {
        if (norm[i] > 0) {
          norm[i] = Math.sqrt(norm[i]);
        }
      }
      for (Iterator<PostingsList> it = index.values().iterator(); it.hasNext();) {
        PostingsList p = it.next();
        for (ListIterator<PostingsEntry> it2 = p.getIterator(); it2.hasNext();) {
          PostingsEntry e = it2.next();
          e.score = (e.size()*Math.log10(nbDocs/p.size()))/norm[e.docID];
        }
      }
    }

    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
	// 
	//  REPLACE THE STATEMENT BELOW WITH YOUR CODE
	//
    	return index.get(token);
    }


    /**
     *  Searches the index for postings matching the query.
     */
    public PostingsList search( Query query, int queryType, int rankingType ) {
	// 
	//  REPLACE THE STATEMENT BELOW WITH YOUR CODE
	//
    	if ( queryType == Index.INTERSECTION_QUERY ) {
    		int nb_of_terms = query.terms.size();
    		if ( nb_of_terms == 0 ) {
    			return null;
    		}
    		PostingsList merge = getPostings(query.terms.get(0));
    		for ( int i = 1; i < nb_of_terms; i++ ) {
    			PostingsList tmp = new PostingsList();
    			ListIterator<PostingsEntry> a = merge.getIterator();
    			ListIterator<PostingsEntry> b = 
    					getPostings(query.terms.get(i)).getIterator();
    			while ( a.hasNext() && b.hasNext() ) {
    				PostingsEntry entry_a = a.next();
    				PostingsEntry entry_b = b.next();
    				if ( entry_a.docID == entry_b.docID ) {
    					tmp.add(entry_a);
    				} else if ( entry_a.docID > entry_b.docID ) {
    					a.previous();
    				} else {
    					b.previous();
    				}
    			}
    			merge = tmp;
    		}
    		return merge;
    	} else if ( queryType == Index.PHRASE_QUERY ) {
    		int nb_of_terms = query.terms.size();
    		if ( nb_of_terms == 0 ) {
    			return null;
    		}
    		PostingsList merge = getPostings(query.terms.get(0));
    		for ( int i = 1; i < nb_of_terms; i++ ) {
    			PostingsList tmp = new PostingsList();
    			ListIterator<PostingsEntry> a = merge.getIterator();
    			ListIterator<PostingsEntry> b = 
    					getPostings(query.terms.get(i)).getIterator();
    			while ( a.hasNext() && b.hasNext() ) {
    				PostingsEntry entry_a = a.next();
    				PostingsEntry entry_b = b.next();
    				if ( entry_a.docID == entry_b.docID ) {
    					PostingsEntry e = new PostingsEntry(entry_a.docID);
    					ListIterator<Integer> a_p = entry_a.getIterator();
    					ListIterator<Integer> b_p = entry_b.getIterator();
    					while ( a_p.hasNext() && b_p.hasNext() ) {
    						int a_pos = a_p.next();
    						int b_pos = b_p.next();
    						if ( a_pos == b_pos-1 ) {
    							e.addPos(b_pos);
    						} else if ( a_pos > b_pos ) {
    							a_p.previous();
    						} else {
    							b_p.previous();
    						}
    					}
    					if (e.size() > 0) {
    						tmp.add(e);
    					}
    				}
    				else if ( entry_a.docID > entry_b.docID ) {
    					a.previous();
    				}
    				else {
    					b.previous();
    				}
    			}
    			merge = tmp;
    		}
    		return merge;
    	} else if ( queryType == Index.RANKED_QUERY ) {
    		int nb_of_terms = query.terms.size();
    		if ( nb_of_terms == 0 ) {
    			return null;
    		}
        HashMap<Integer, PostingsEntry> entries = new HashMap<Integer, PostingsEntry>();
    		for ( int i = 0; i < nb_of_terms; i++ ) {
          for (ListIterator<PostingsEntry> it =
               getPostings(query.terms.get(i)).getIterator(); it.hasNext();) {
            PostingsEntry e = it.next();
            if (!entries.containsKey(e.docID)) {
              entries.put(e.docID, new PostingsEntry(e.docID));
            }
            entries.get(e.docID).score += e.score;
          }
        }
        PostingsList tmp = new PostingsList();
        tmp.list.addAll(entries.values());
        Collections.sort(tmp.list);
        return tmp;
      }
    	return null;
    }


    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
