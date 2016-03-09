package com.celloud.QC.Util

import org.apache.hadoop.io.Text
import org.apache.spark.rdd.RDD

object fastQCUtil {

  /**
   * @author yuyang
   * @since 20160127
   * @note get the score standard
   */
  def getStandardScore(file: RDD[(Text, Text)]): Int = {
    val one = file.take(1).head._2.toString
    val scoreArray = one.toCharArray
    var flag = true
    var counter = 0
    var standCoreChar = 0

    while (flag && counter < scoreArray.length) {
      if (scoreArray(counter) < '5') {
        standCoreChar = '!'
        flag = false
      } else if (scoreArray(counter) > 'T') {
        standCoreChar = '@'
        flag = false
      } else {
        counter = counter + 1
      }
    }
    standCoreChar
  }
}
