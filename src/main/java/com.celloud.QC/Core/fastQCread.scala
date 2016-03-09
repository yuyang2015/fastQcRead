package com.celloud.QC.Core

import com.celloud.QC.Util.{fastQCUtil, Constant}
import com.celloud.QC.QcInputFormat.qcInputFormat
import org.apache.hadoop.io.Text
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/* ------------------------------------------------------------------------------------- *
 | QC需求:输入文件file,文件的每四行为一个处理单元,按列处理。将每列的质量值从高到低排序,      |
 | 然后返回每列处于10%,25%,50%,75%,90%位置的质量值及该列所有质量值的平均值。                |
 * ------------------------------------------------------------------------------------- */
object fastQCread {

  /**
   * @author yuyang
   * @since 20160309
   * @note fastQCread
   * @param hdfsUri -> the uri of hdfsMaster e.g. hdfs://master:9000
   * @param path -> path for input file from hdfs e.g. /data/mydata/qcFile.fastq
   * @param numPartition -> num for file partition  cores*2~3
   * @return void -> print the 6 qcRead quota(10%,25%,50%,75%,90%,average-quality-values) of every column for Boxplot(Box-whisker Plot) e.g. 42,42,42,42,32,39
   */
  def fastQCread(path : String, hdfsUri : String , numPartition : Int): Unit = {
    val name = "fastQcReadV1.7.5"
    val sc = new SparkContext(new SparkConf().setAppName(name))
    val file = sc.newAPIHadoopFile[Text, Text, qcInputFormat](hdfsUri + path)
    val standCoreChar = fastQCUtil.getStandardScore(file)
    val createCombiner = (v: Int) => ArrayBuffer(v)
    val mergeValue = (buf: ArrayBuffer[Int], v: Int) => buf += v
    val mergeCombiners = (c1: ArrayBuffer[Int], c2: ArrayBuffer[Int]) => c1 ++= c2
     file.repartition(numPartition).map(x => x._2.toString).map(line => {
      var counter = 1
      val lineArray = line.toCharArray
      var str = ""
        while (counter <= lineArray.length) {
          str += counter + Constant.waveLine + (Integer.valueOf(lineArray(counter - 1)) - standCoreChar) + Constant.verticalLine
          counter = counter + 1
        }
      str.substring(0, str.length - 1)
    }).flatMap(line => line.split(Constant.verticalLineEscape))
      .map(quality => (quality.split(Constant.waveLineEscape)(0).toInt, quality.split(Constant.waveLineEscape)(1).toInt))
      .combineByKey(createCombiner, mergeValue, mergeCombiners)
      .map(x => (x._1, getLineResult(x._2))).sortByKey().map(x => x._2).collect().foreach(println)
      sc.stop()
  }

  /**
   * @author yuyang
   * @since 20160309
   * @note  fetch the statistics result of qcFile from startColumn to endColumn
   * @param fileAfterMatrixChange -> the fastQC file after matrix-Change
   * @param startColumn
   * @param endColumn
   * @return String -> statistics result e.g. 42,42,42,42,32,39
   */
  def getResultByArea(fileAfterMatrixChange: RDD[(Int, ArrayBuffer[Int])], startColumn: Int, endColumn: Int , numPartitions: Int): String = {
    fileAfterMatrixChange.filter(_._1 <= endColumn).filter(_._1 >= startColumn).map(x => (1, x._2)).foldByKey(new ArrayBuffer[Int], numPartitions)(_ ++= _)
      .map(line => getLineResult(line._2)).collect().head
  }

  /**
   * @author yuyang
   * @since 20160127
   * @note fetch the statistics result of qcFile In a given column index
   * @param column
   * @return String -> statistics result e.g.  42,42,42,42,32,39
   */
  def getLineResult(column: Iterable[Int]): String = {
    val resultMap = mutable.HashMap[Int, Int]()
    var valueSum, currentSumPercent, currentSum: BigDecimal = 0
    var tag10, tag25, tag50, tag75, tag90: Int = 0
    for (e <- column) {
      valueSum = valueSum + e
      if (resultMap.contains(e))
        resultMap.put(e, resultMap(e) + 1)
      else resultMap.+=(e -> 1)
    }
    val listSize = column.size
    val tagAvg = (valueSum / listSize).toInt
    resultMap.toList.sorted.reverse.foreach {
      case (key: Int, value: Int) =>
        currentSum = currentSum + value
        currentSumPercent = currentSum / listSize
        if (currentSumPercent >= 0.5) {
          //50~100
          if (currentSumPercent >= 0.75) {
            //75~100
            if (currentSumPercent >= 0.9) {
              //90~100
              if (tag10 == 0) {
                tag10 = key
              }
              if (tag25 == 0) {
                tag25 = key
              }
              if (tag50 == 0) {
                tag50 = key
              }
              if (tag75 == 0) {
                tag75 = key
              }
              if (tag90 == 0) {
                tag90 = key
              }
            } else {
              //75~90
              if (tag10 == 0) {
                tag10 = key
              }
              if (tag25 == 0) {
                tag25 = key
              }
              if (tag50 == 0) {
                tag50 = key
              }
              if (tag75 == 0) {
                tag75 = key
              }
            }
          } else {
            //50~75
            if (tag10 == 0) {
              tag10 = key
            }
            if (tag25 == 0) {
              tag25 = key
            }
            if (tag50 == 0) {
              tag50 = key
            }
          }
        } else {
          //位点位置落在50以下
          if (currentSumPercent >= 0.25) {
            //25~50
            if (tag10 == 0) {
              tag10 = key
            }
            if (tag25 == 0) {
              tag25 = key
            }
          } else {
            //0~25
            if (currentSumPercent >= 0.1) {
              //10~25
              if (tag10 == 0) {
                tag10 = key
              }
            }
          }
        }
    }
    tag10 + Constant.comma + tag25 + Constant.comma + tag50 + Constant.comma + tag75 + Constant.comma + tag90 + Constant.comma + tagAvg
  }
}
