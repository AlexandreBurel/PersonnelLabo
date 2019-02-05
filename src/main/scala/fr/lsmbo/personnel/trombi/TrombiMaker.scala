package fr.lsmbo.personnel.trombi

import java.io._

import fr.lsmbo.personnel._
import org.apache.poi.ss.usermodel.{BorderStyle, FillPatternType, Workbook}
import org.apache.poi.util.IOUtils
import org.apache.poi.xssf.usermodel.{XSSFColor, XSSFRow, XSSFWorkbook}

class TrombiMaker(workbook: XSSFWorkbook) {

  final val SEPARATOR_ROW_HEIGHT: Short = 100
  final val SMALL_ROW_HEIGHT: Short = 120

  private def getSortedPeople: Array[People] = {
    TableauDuPersonnel.getPersonnel()
      .sortBy(_.nom) // has to be in first position (otherwise the final sort will not be good)
      .sortBy(p => (p.corps.get.order, p.nom.getOrElse(""))) // it seems that you can't sort on 3Tuples :(
  }

  // get report file path
  val sheet = workbook.createSheet(MyConfig.trombiTitle)

  // column width (must fit in an A4 page)
  sheet.setColumnWidth(0, 22 * 260)
  sheet.setColumnWidth(1, 17 * 260)
  sheet.setColumnWidth(2, 16 * 260)
  sheet.setColumnWidth(3, 7 * 260)
  sheet.setColumnWidth(4, 12 * 260)
  sheet.setColumnWidth(5, 10 * 260)

  // very first line is a separator
  addSeparation(0)
  var line = 1
  var i = 0
  getSortedPeople.foreach(p => {
    i += 1
    try {
      // first line is empty and small
      addEmptyLine(line)
      // second line contains name
      var row = sheet.createRow(line+1)
      addCell(row, 0, p.nom.getOrElse("").toUpperCase, bold = true)
      addCell(row, 1, p.prenom.getOrElse(""), bold = true)
      addCell(row, 2, p.getInitiales, bold = true)
      // third line contains status
      row = sheet.createRow(line+2)
      addCell(row, 0, p.getCorps)
      // fourth line contains location
      row = sheet.createRow(line+3)
      addCell(row, 0, p.batiment.getOrElse(""))
      addCell(row, 1, if(p.bureau.isEmpty) "" else "Bureau "+p.bureau.get)
      // fifth line contains contact
      row = sheet.createRow(line+4)
      addCell(row, 0, "Tél : "+p.telephone.getOrElse(""))
      addCell(row, 1, p.mail.getOrElse(""))
      // sixth line contains birthday
      row = sheet.createRow(line+5)
      addCell(row, 0, "Anniversaire :")
      addCell(row, 1, p.formattedBirthday())
      // seventh line is empty and small
      addEmptyLine(line+6)
      // eighth line is a separator
      addSeparation(line+7)
      // add picture
      addPicture(p.getPictureAsStream, line)
      // increment first line number
      line += 8

      // add a line break every 8 people to make printing easier
      if(i % 8 == 0) {
        sheet.setRowBreak(line-1)
        // add a new separator
        addSeparation(line)
        line += 1
      }
    } catch {
      case e: FileNotFoundException =>
        println(s"Error on ${p.toString}: ${e.getMessage}")
      case e: Exception =>
        println(s"Error on ${p.toString}: ${e.getMessage}")
        e.printStackTrace()
    }
  })

  private def addPicture(inputStream: InputStream, line: Int) = {
    // get the contents of an InputStream as a byte[].
    val bytes = IOUtils.toByteArray(inputStream)
    // adds a picture to the workbook
    val pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG)
    // close the input stream
    inputStream.close()
    // returns an object that handles instantiating concrete classes
    val helper = workbook.getCreationHelper()
    // creates the top-level drawing patriarch.
    val drawing = sheet.createDrawingPatriarch()

    // create an anchor that is attached to the worksheet
    val anchor = helper.createClientAnchor()

    // create an anchor with upper left cell _and_ bottom right cell
    // TODO the picture should have an anchor on the top left corner, and a fixed height...
    anchor.setCol1(4) //Column D
    anchor.setCol2(5) //Column E
    anchor.setRow1(line) //Row 2
    anchor.setRow2(line + 7) //Row 8

    // creates a picture
    drawing.createPicture(anchor, pictureIdx)
  }

  private def addSeparation(line: Int): Unit = {
    val row = sheet.createRow(line)
    val cellStyle = workbook.createCellStyle
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
    cellStyle.setFillBackgroundColor(0.toShort)
    cellStyle.setBorderTop(BorderStyle.THICK)
    cellStyle.setTopBorderColor(new XSSFColor(java.awt.Color.WHITE))
    cellStyle.setBorderBottom(BorderStyle.THICK)
    cellStyle.setBottomBorderColor(new XSSFColor(java.awt.Color.WHITE))
    for (i <- 0 to 5) row.createCell(i).setCellStyle(cellStyle)
    row.setHeight(SEPARATOR_ROW_HEIGHT)
  }

  private def addEmptyLine(line: Int): Unit = {
    val row = sheet.createRow(line)
    row.setHeight(SMALL_ROW_HEIGHT)
  }

  private def addCell(row: XSSFRow, index: Int, value: String, bold: Boolean = false): Unit = {
    val cell = row.createCell(index)
    cell.setCellValue(value)
    if(bold) {
      val cellStyle = workbook.createCellStyle
      val font = workbook.createFont
      font.setBold(true)
      cellStyle.setFont(font)
      cell.setCellStyle(cellStyle)
    }
  }
}
