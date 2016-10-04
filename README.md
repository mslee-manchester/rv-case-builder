# rv-case-builder
Constructs XML Case Files for Reasoner Verification method

This project is designed to take results from the reasoner verification method, stored as csv's and .owl files, and 
construct case files in an xml format that follows a schema. This stores the verdicts delivered on
a case, as well as different pieces of information about the case (some of which can be considered
"evidence" to believe things one way or the other). The intention is that these files will allow us to
perform queries.
