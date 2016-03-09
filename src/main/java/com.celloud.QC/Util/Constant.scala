package com.celloud.QC.Util

/**
 *  @author yuyang
 *  @since 20151224
 *  @note constant for qc read
 */
object Constant {

  //fasqFileRead standard value
  val core_standard = 20
  val len_standard = 29

  //delimiter
  val tab = "\t"
  val comma = ","
  val verticalLine = "|"
  val waveLine = "~"
  val dollar = "$"
  val tabEscape = "\\t"
  val verticalLineEscape = "\\|"
  val waveLineEscape = "\\~"
  val dollarEscape = "\\$"

  //masterURI
  val maserURI_BIO = "hdfs://bio-server:9000"
  val maserURI_Master = "hdfs://master:9000"
  val emr_Master = "hdfs://emr-header-1:9000"
}
