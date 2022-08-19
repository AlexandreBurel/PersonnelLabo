package fr.lsmbo.personnel.anniv

import java.awt.Color
import java.time.LocalDate

import fr.lsmbo.personnel.{MyConfig, People, TableauDuPersonnel}
import org.apache.poi.ss.usermodel.{BorderStyle, FillPatternType, HorizontalAlignment}
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.{XSSFCellStyle, XSSFColor, XSSFWorkbook}

class AnnivMaker(workbook: XSSFWorkbook) {

  final val DEFAULT_LINE_HEIGHT: Short = 380
  final val TITLE_LINE_HEIGHT: Short = 471

// use the same year for everybody
private val list = TableauDuPersonnel.getPersonnel().filter(_.anniversaire.isDefined).sortBy(_.anniversaire.get.withYear(2018).toEpochDay)

  // create workbook and sheet
  private val sheet = workbook.createSheet(MyConfig.annivTitle)
  writeFile()

  private val missingBirthday = TableauDuPersonnel.getPersonnel().filter(_.anniversaire.isEmpty)
  if(!missingBirthday.isEmpty) {
    println("\nPersonnes pour lesquelles on n'a pas la date d'anniversaire:")
    missingBirthday.foreach(p => println(p.toString))
  }

  private def writeFile(): Unit = {

    // add title on first line, make it big (Calibri, 18, bold, centered) and on three columns (B to D)
    var row = sheet.createRow(0)
    row.setHeight(TITLE_LINE_HEIGHT)
    var cell = row.createCell(1)
    cell.setCellValue(sheet.getSheetName.toUpperCase)
    val bgColor = new XSSFColor(new Color(183, 222, 232), null)
    cell.setCellStyle(getStyle(color = bgColor, bold = true, size = 18, centered = true, top = true, bottom = true, left = true))
    cell = row.createCell(2)
    cell.setCellStyle(getStyle(color = bgColor, top = true, bottom = true))
    cell = row.createCell(3)
    cell.setCellStyle(getStyle(color = bgColor, top = true, bottom = true, right = true))
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 3))

    // one blank line
    row = sheet.createRow(1)
    row.setHeight(DEFAULT_LINE_HEIGHT)

    // one line per people
    for (i <- list.indices) {
      addPeople(list(i), i + 2, top = i == 0, bottom = i == list.length - 1)
    }

    // adjust column sizes
    sheet.setColumnWidth(0, 10 * 260)
    sheet.setColumnWidth(1, 28 * 260)
    sheet.setColumnWidth(2, 23 * 260)
    sheet.setColumnWidth(3, 18 * 260)
    sheet.setColumnWidth(4, 10 * 260)

  }

  private def addPeople(people: People, line: Int, top: Boolean = false, bottom: Boolean = false): Unit = {
    val bgColor = getColor(people.anniversaire)
    val row = sheet.createRow(line)
    row.setHeight(DEFAULT_LINE_HEIGHT)

    var cell = row.createCell(1)
    cell.setCellValue(people.nom.getOrElse("").toUpperCase)
    cell.setCellStyle(getStyle(color = bgColor, top = top, bottom = bottom, left = true))

    cell = row.createCell(2)
    cell.setCellValue(people.prenom.getOrElse(""))
    cell.setCellStyle(getStyle(color = bgColor, top = top, bottom = bottom))

    cell = row.createCell(3)
    cell.setCellValue(people.formattedBirthday("d MMMM"))
    cell.setCellStyle(getStyle(color = bgColor, centered = true, top = top, bottom = bottom, right = true))
  }

  private def getStyle(fontName: String = "Calibri", size: Short = 14, bold: Boolean = false, centered: Boolean = false, color: XSSFColor = null, top: Boolean = false, bottom: Boolean = false, left: Boolean = false, right: Boolean = false): XSSFCellStyle = {
    val cellStyle = workbook.createCellStyle
    val font = workbook.createFont
    font.setFontHeightInPoints(size)
    font.setBold(bold)
    font.setFontName(fontName)
    cellStyle.setFont(font)
    if(color != null) {
      cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
      cellStyle.setFillForegroundColor(color)
    }
    if(centered) cellStyle.setAlignment(HorizontalAlignment.CENTER)
    if (top) cellStyle.setBorderTop(BorderStyle.THIN)
    if (bottom) cellStyle.setBorderBottom(BorderStyle.THIN)
    if (left) cellStyle.setBorderLeft(BorderStyle.THIN)
    if (right) cellStyle.setBorderRight(BorderStyle.THIN)
    cellStyle
  }

  private def getColor(dateOpt: Option[LocalDate]): XSSFColor = {
    // FIXME
    if(dateOpt.isDefined) {
      new XSSFColor(dateOpt.get.getMonthValue match {
        case 1 => new Color(253, 233, 217) // janvier
        case 2 => new Color(184, 204, 228) // fevrier
        case 3 => new Color(235, 241, 222) // mars
        case 4 => new Color(252, 213, 180) // avril
        case 5 => new Color(183, 222, 232) // mai
        case 6 => new Color(141, 180, 226) // juin
        case 7 => new Color(253, 233, 217) // juillet
        case 8 => new Color(221, 217, 196) // aout
        case 9 => new Color(204, 192, 218) // septembre
        case 10 => new Color(184, 204, 228) // octobre
        case 11 => new Color(196, 215, 155) // novembre
        case 12 => new Color(252, 213, 180) // decembre
        case _ => Color.WHITE // how is it possible to get here ???
      }, null)
    } else new XSSFColor(Color.WHITE, null)
  }

}
