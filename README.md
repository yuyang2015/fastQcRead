fastQcRead
===
A quality control tool for high throughput sequence data by Spark.
---
####how to use it
* download the source code to your workspace (recommend IntelliJ IDEA)
* build artifacts(jar) e.g. In IDEA you can accomplish it by follow 4 steps on the premise of configured your project artifacts:<br> `Bulid->Build Artifacts->fastQcRead->Build`
* submit the fastQcRead jar on your Spark cluster like :<br>
`spark-submit --master spark://master:7077 --class com.celloud.QC.FastQC.fastQcGo /share/data/yuyang/jars/fastQcRead.jar /home/yuyang/data/file.fastq hdfs://master:9000 36` <br>the 3 params expressed as <br>
p1:the uri of hdfsMaster e.g. hdfs://master:9000<br>
p2:path for input file from hdfs e.g. /data/mydata/qcFile.fastq<br>
p3:num for file partition  cores*2~3<br>
