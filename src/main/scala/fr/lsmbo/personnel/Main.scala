package fr.lsmbo.personnel

import java.io.FileWriter

import fr.lsmbo.personnel.anniv.AnnivMaker
import fr.lsmbo.personnel.putz.PutzMaker
import fr.lsmbo.personnel.trombi.TrombiMaker

object Main extends App {

  lazy val artefactId = getClass.getPackage.getImplementationTitle
  lazy val version = getClass.getPackage.getImplementationVersion

  override def main(args: Array[String]): Unit = {

    args.foreach(_ match {
      case "putz" => new PutzMaker
      case "trombi" => new TrombiMaker
      case "anniv" => new AnnivMaker
      case _ =>
    })

    if (args.size == 0) {
      generateScript("Anniversaires.bat", "anniv")
      generateScript("PutzPlanning.bat", "putz")
      generateScript("Trombinoscope.bat", "trombi")
    }

  }

  private def generateScript(name: String, function: String): Unit = {
    try {
      val writer: FileWriter = new FileWriter(name)
      writer.write("@echo off\r\n")
      writer.write("\r\n")
      writer.write(s"java -jar ${artefactId}-${version}.jar ${function}\r\n") // write new line
      writer.write("\r\n")
      writer.write("pause\r\n")
      writer.close()
    } catch {
      case e: java.io.IOException => e.printStackTrace()
      case t: Throwable => t.printStackTrace()
    }
  }

}
