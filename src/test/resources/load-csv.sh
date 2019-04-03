# -------------------------------------------
# loads data from a csv file to Neo4j database
# -------------------------------------------
export NEO4J_HOME=~/neo4j
#export NEO4_DEBUG=1
source $NEO4J_HOME/bin/neo4j-admin import \
  --database=familytree \
  --mode=csv \
  --nodes:Person="persons.csv" \
  --relationships:Person="relations.csv" \
  --id-type=STRING

#  