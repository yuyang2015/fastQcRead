package com.celloud.QC.FastQC

import com.celloud.QC.Core.fastQCread

object fastQcGo {

  def main(args: Array[String]): Unit = {
    assert(args.length >= 3,"Requires three parameters: p1:path for input file from hdfs like '/data/mydata/qcFile.fastq'; p2: hdfs master Uri like 'hdfs://master:9000' p3: num of file partitions")
    val path = args(0)
    val hdfsMasterUri = args(1)
    val numPartition = args(2).toInt
    fastQCread.fastQCread(path,hdfsMasterUri,numPartition)
  }
}
