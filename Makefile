HADOOP_HOME = /home/hadoop/hadoop/
MY_CLASSPATH = javac -cp /home/hadoop/hadoop/share/hadoop/common/hadoop-common-2.8.1.jar:/home/hadoop/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.8.1.jar:/home/hadoop/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.8.1.jar:/home/hadoop/hadoop/lib/commons-cli-1.4.jar:/home/hadoop/hadoop/share/hadoop/common/lib/log4j-1.2.17.jar:/home/hadoop/hadoop/share/hadoop/tools/lib/commons-lang-2.6.jar:neighborhood_classes -d neighborhood_classes/ src/*.java
HDFS_PATH = /user/hadoop/

all: build run csv

build: compile jar

compile:
	javac -cp $(MY_CLASSPATH) -d neighborhood_classes/ src/*.java

jar:
	cp -r src/META-INF/MANIFEST.MF neighborhood_classes
	cd neighborhood_classes; jar cvmf MANIFEST.MF NeighborhoodScoreHadoop.jar *
	mv neighborhood_classes/NeighborhoodScoreHadoop.jar .

run:
	$(HADOOP_HOME)/bin/hadoop jar NeighborhoodScoreHadoop.jar $(k) $(input) $(cout) $(nout) $(mout)

csv:
	hdfs dfs -get $(HDFS_PATH)$(mout)/* output_csv
	mv output_csv/part-* output_csv/result.csv
	mv output_csv/result.csv result.csv
	rm -rf output_csv/*


clean:
	$(HADOOP_HOME)/bin/hdfs dfs -rm -r -f output

gzip:
	gzip input/big-corpus/*
	gzip input/books/*

gunzip:
	gunzip input/big-corpus/*
	gunzip input/books/*

setup:
	bin/hdfs dfs -put $(HDFS_PATH)/input input
	
