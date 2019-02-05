package fr.lsmbo.personnel

import java.io.FileOutputStream

import fr.lsmbo.personnel.anniv.AnnivMaker
import fr.lsmbo.personnel.map.MapMaker
import fr.lsmbo.personnel.putz.PutzMaker
import fr.lsmbo.personnel.trombi.TrombiMaker
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object Main extends App {

  lazy val artefactId = getClass.getPackage.getImplementationTitle
  lazy val version = getClass.getPackage.getImplementationVersion

  override def main(args: Array[String]): Unit = {

    // create a single file with one sheet per class
    val workbook: XSSFWorkbook = new XSSFWorkbook(MyConfig.getTemplateFileStream)
    try {
      new PutzMaker(workbook)
    } catch {
      case e => e.printStackTrace()
    }
    try {
      new TrombiMaker(workbook)
    } catch {
      case e => e.printStackTrace()
    }
    try {
      new MapMaker(workbook)
    } catch {
      case e => e.printStackTrace()
    }
    try {
      new AnnivMaker(workbook)
    } catch {
      case e => e.printStackTrace()
    }

    // set sheets order
    workbook.setSheetOrder(MyConfig.annivTitle, 0)
    workbook.setSheetOrder(MyConfig.mapTitle, 1)
    workbook.setSheetOrder(MyConfig.putzTitle, 2)
    workbook.setSheetOrder(MyConfig.trombiTitle, 3)
    workbook.setActiveSheet(1)

    // TODO the first two sheets are selected by default

    // write, close and quit
    val outputStream = new FileOutputStream(MyConfig.outputFile)
    workbook.write(outputStream)
    workbook.close()

  }

}
