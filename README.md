fastQcRead v0.3.1
===
A quality control tool for high throughput sequence data by Spark.
---
### Introduction
input the standard fastq file then it will figure out 6 quota(10%,25%,50%,75%,90%,average-quality-values) for every fastq file column  for Boxplot(Box-whisker Plot)
####how to use it
* download the source code to your workspace (recommend IntelliJ IDEA)
* build artifacts(jar) e.g. In IDEA you can accomplish it by follow 4 steps:<br> `①Bulid->②Build Artifacts->③fastQcRead->④Build`
* submit the fastQcRead jar on your Spark cluster like :<br>
`spark-submit --master spark://master:7077 --class com.celloud.QC.FastQC.fastQcGo /share/data/yuyang/jars/fastQcRead.jar /home/yuyang/data/file.fastq hdfs://master:9000 36` <br>the 3 params expressed as <br>
p1: path of input file from hdfs e.g. /data/mydata/qcFile.fastq<br>
p2: uri of hdfsMaster e.g. hdfs://master:9000<br>
p3: num of file partition  cores*2~3<br>

####how to display the result
the program will print the statistic results on the driver node console like<br>
　`32,32,32,32,32,31`<br>
　`32,32,32,32,32,31`<br>
　`37,37,37,37,32,35`<br>
　`37,37,37,37,37,36`<br>
　`37,37,37,37,37,36`<br>
　`42,42,42,42,37,41`<br>
　`42,42,42,42,37,41`<br>
　`42,42,42,42,42,41`<br>
　`42,42,42,42,42,41`<br>
　`...`<br>
you can also design your own outputformat by modify the source code

<br>
####defect
due to the imperfection of code,every hadoop block(128m) will drop one record.
