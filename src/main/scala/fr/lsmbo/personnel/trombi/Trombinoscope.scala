package fr.lsmbo.personnel.trombi

import fr.lsmbo.personnel.Person
import fr.lsmbo.personnel.MyConfig
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import org.apache.poi.ss.usermodel.BorderStyle
import java.io.File
import java.io.InputStream
import java.io.FileInputStream
import org.apache.poi.util.IOUtils
import org.apache.poi.ss.usermodel.Workbook

class Trombinoscope {

  private def getStyle(bold: Boolean = false, top: Boolean = false, bottom: Boolean = false) = {
    val cellStyle = workbook.createCellStyle
    if (bold) {
      val font = workbook.createFont
      font.setBold(true)
      cellStyle.setFont(font)
    }
    if (top) cellStyle.setBorderTop(BorderStyle.THICK)
    if (bottom) cellStyle.setBorderBottom(BorderStyle.THICK)
    cellStyle
  }

  private def addTopLine(line: Int) = {
    var row = sheet.createRow(line)
    for (i <- 0 to 5) {
      row.createCell(i).setCellStyle(getStyle(top = true))
    }
  }

  private def addPicture(picture: File, line: Int) = {
    //FileInputStream obtains input bytes from the image file
    val inputStream = new FileInputStream(picture)
    //Get the contents of an InputStream as a byte[].
    val bytes = IOUtils.toByteArray(inputStream)
    //Adds a picture to the workbook
    val pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG)
    //close the input stream
    inputStream.close()
    //Returns an object that handles instantiating concrete classes
    val helper = workbook.getCreationHelper()
    //Creates the top-level drawing patriarch.
    val drawing = sheet.createDrawingPatriarch()

    //Create an anchor that is attached to the worksheet
    val anchor = helper.createClientAnchor()

    //create an anchor with upper left cell _and_ bottom right cell
    anchor.setCol1(4) //Column D
    anchor.setCol2(5) //Column E // TODO do not resize images !!
    anchor.setRow1(line + 1) //Row 3
    anchor.setRow2(line + 6) //Row 4

    //Creates a picture
    val pict = drawing.createPicture(anchor, pictureIdx)
  }

  // get report file path
  var workbook: XSSFWorkbook = new XSSFWorkbook
  val sheet = workbook.createSheet("Trombinoscope LSMBO")
  var line = 0

  // column width (must fit in an A4 page)
  sheet.setColumnWidth(0, 24 * 260)
  sheet.setColumnWidth(1, 15 * 260)
  sheet.setColumnWidth(2, 16 * 260)
  sheet.setColumnWidth(3, 10 * 260)
  sheet.setColumnWidth(4, 10 * 260)
  sheet.setColumnWidth(5, 10 * 260)

  MyConfig.people.filter(_.hidden == false).sortBy(_.order).foreach(p => {
    try {
    // line 1: empty, top border: bold & xlThick
    var row = sheet.createRow(line)
    for (i <- 0 to 5) {
      row.createCell(i).setCellStyle(getStyle(top = true))
    }
    // line 2: lastName, firstName, initials
    row = sheet.createRow(line + 1)
    var cell = row.createCell(0)
    cell.setCellValue(p.lastName)
    cell.setCellStyle(getStyle(bold = true))
    cell = row.createCell(1)
    cell.setCellValue(p.firstName)
    cell.setCellStyle(getStyle(bold = true))
    row.createCell(2).setCellValue(p.initials)
    // line 3: status
    row = sheet.createRow(line + 2)
    row.createCell(0).setCellValue(p.status.toString())
    // line 4: location, "Bureau " & room
    row = sheet.createRow(line + 3)
    row.createCell(0).setCellValue(p.location.toString())
    row.createCell(1).setCellValue("Bureau " + p.room)
    // line 5: mail
    row = sheet.createRow(line + 4)
    row.createCell(0).setCellValue(p.mail)
    // line 6: "Tél: " & phone
    row = sheet.createRow(line + 5)
    row.createCell(0).setCellValue("Tél: " + p.phone)
    // line 7: empty, bottom border: bold & xlThick
    row = sheet.createRow(line + 6)
    for (i <- 0 to 5) {
      row.createCell(i).setCellStyle(getStyle(bottom = true))
    }
    
    addPicture(p.getPicture, line)

    // picture:
    // - left = column D
    // - width = around 2.36cm (should be fixed)
    // - height = depends on width
    // - top = depends on height, must be centered vertically and not be more than line7.bottom - line1.top
    //
    // photo.Copy
    // Sheets(sheetName).Cells(line, 4).Select
    // Sheets(sheetName).Paste
    // Selection.Name = photo.title
    // Sheets(sheetName).Shapes(photo.title).Top = Worksheets(sheetName).Cells(line, 1).Top + (105.75 - photo.Height) / 2
    // 

    line += 7
    } catch {
      case e: FileNotFoundException => 
        println(s"Error on ${p.toString}: ${e.getMessage}")
        println(p.picture)
        println(p.getPicture.getAbsolutePath)
      case e: Exception => 
        println(s"Error on ${p.toString}: ${e.getMessage}")
        e.printStackTrace()
    }
  })

  // write data to file
  try {
    val outputStream = new FileOutputStream(MyConfig.trombiOutputFile)
    workbook.write(outputStream)
    workbook.close()
  } catch {
    case e: FileNotFoundException => e.printStackTrace
    case e: IOException => e.printStackTrace
    case e: Exception => e.printStackTrace
  }

}
