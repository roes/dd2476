There are several interesting types of queries to look at:
1. Common expressions (eg. life or death)
2. Several random words (eg. windmill saucepan)
3. Grammatical conjunctions (eg. query containing or/else)
4. Quotation marks (eg. "life or death", "windmill saucepan")
5. Logical operators (eg. windmill || saucepan)

By trying these types with different words, some common and others more unusual, you can draw some conclusions. Quotation marks always causes it to try a phrase query, common expressions are likely handled by some index for k-words (returns results similar to a phrase query). It's clear when you enter random words that it does an intersection query, you get results containing all the words, but if you put quotation marks around the query it tells you there are no results.
Also interesting to note is that when their search can't find exact intersection matches it retrieves results with similar words/meaning (eg. results with pot instead of saucepan), which hints at an index of related words.
