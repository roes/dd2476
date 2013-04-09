Compile:
javac -Xlint:none -cp .:pdfbox:megamap ir/*.java

Run:
java -Xmx1024m -cp .:pdfbox:megamap ir.SearchGUI -d svwiki/1000

2.2 Why do we use a union query here, but an intersection query in Assignment 1?
Using this approach of weights, we remove the importance of the order of the words and instead look at the documents as 'bags of words' where how many times they occur is the important factor. This makes a union query appropriate here, while the intersection approach improves results when not using word frequence as a weight.

The hits for 'december', or 'november eller december' seem to mostly be about calendar/dates, christmas themed or a person. This all seems reasonable, the articles about people probably have some of their important dates (birth, death etc) in those months, christmas is in december and I guess I don't need to explain the hits about calendars.

2.3 Does this pagerank ordering seem reasonable? Why? (Highest Latin, lowest Gunnebo IP)
Yes it does, Latin is a well known subject, referenced in a lot of different areas and should therefore get a high ranking. It's going to be very rare for pages to reference a small town sports field, hence a low ranking makes perfect sense.

What is the trend with decreasing pagerank?
Narrower topics or topics for smaller countries/areas.
