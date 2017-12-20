# A3 - Neighborhood Score: Reloaded

This project calculates the median neighborhood score for each word in the corpus

Pre-requisites:
Java 8 installed
Hadoop installed

Before running make sure that the input/books and input/big-corpus folder has .gz files.
If not run the below commands:
	gzip input/big-corpus/*
	gzip input/books/*

Extract the big-corpus and books using the below commands:
	gunzip input/big-corpus/*
	gunzip input/books/*

Copy the input(s) to be processed to hdfs.

Make sure that an neighborhood_classes and output_csv folders are there in the same path as src folder.

Set HADOOP_HOME, MY_CLASSPATH and HDFS_PATH variables as per your configuration.

For running the project, run make by passing 5 arguments.
Consider the example below:
make k=2 input=input cout=cout nout=nout mout=mout
where
k = no. of neighborhood
input = input path of the corpus on the hdfs
cout = output path for char count
nout = output path for neighborhood
mout = output path for mean score

This will create a .jar file.

If you have a 4 nodes/2 nodes cluster on EMR, you can upload the .jar file, 
upload the corpus, give the arguments as below to run the program (example):
2
s3://dhvani-input/
cout
nout
mout

If you are not familiar with this, please follow the steps in the word document attached for running on EMR.

