running tests

mvn clean compile exec:java -Dexec.mainClass="TestRunner" -P -ddi-cluster-Mongo-DB-dev,ddi-cluster-Mongo-DB
