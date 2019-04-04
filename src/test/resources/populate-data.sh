# -------------------------------------------
# populates data from csv files to Neo4j database
# -------------------------------------------
export NEO4J_HOME=~/neo4j
DATABASE=familytree

rm -rf $NEO4J_HOME/data/databases/$DATABASE

#export NEO4_DEBUG=1
source $NEO4J_HOME/bin/neo4j-admin import \
  --database=$DATABASE                   \
  --mode=csv                              \
  --nodes:Family="family.csv"             \
  --nodes:Person="persons.csv"            \
  --relationships="relations.csv"  \
  --id-type=STRING

#  