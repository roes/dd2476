How is the relevance feedback process affected by alpha and beta?
Changing the value of alpha affects the importance of the query terms, while changing the value of beta changes the importance of the document terms we're extending the query with.

Why is the search after feedback slower?
Several reasons. Weights need to be recalculated, more terms are added to the query, both of these factors cause the search to take more time.

Why is the number of returned documents larger?
We've expanded the query with more terms which results in more documents containing one or more of these terms.

Why are there more highly ranked short documents?
Since it's likely the documents will contain at least some of the expanded query terms the fact that it's shorter leads to the normalised weights being relatively high compaired to longer documents.
