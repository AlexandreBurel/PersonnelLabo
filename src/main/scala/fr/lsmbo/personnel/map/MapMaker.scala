package fr.lsmbo.personnel.map

import fr.lsmbo.personnel.{MyConfig, People, TableauDuPersonnel}
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFWorkbook}

class MapMaker(workbook: XSSFWorkbook) {

  // check for the template sheet
  val templateSheetIndex = workbook.getSheetIndex("MAP")
  if (templateSheetIndex < 0) {
    throw new Exception("Template sheet does not exist !")
  }

  // rename the sheet
  workbook.setSheetName(templateSheetIndex, MyConfig.mapTitle)

  // get the sheet
  val sheet = workbook.getSheetAt(templateSheetIndex)

  TableauDuPersonnel.getPersonnel().foreach(p => {
    val cell = getCell(p)
    if(cell.isDefined) {
      if(cell.get.getStringCellValue.isEmpty) {
        cell.get.setCellValue(p.prenom.getOrElse(p.initiales))
      } else {
        cell.get.setCellValue(cell.get.getStringCellValue+", "+p.prenom.getOrElse(p.initiales))
      }
    }
  })

  private def getCell(p: People):  Option[XSSFCell] = {
    var cellOpt: Option[XSSFCell] = None
    if(p.batiment.isDefined && p.bureau.isDefined) {
      if(p.batiment.get.equals("R2-N0")) {
        p.bureau.get match {
          case "1" => cellOpt = Some(sheet.getRow(5).getCell(0)) // B6
          case "3" => cellOpt = Some(sheet.getRow(8).getCell(0)) // B9
          case "4" => cellOpt = Some(sheet.getRow(11).getCell(0)) // B12
          case "5" => cellOpt = Some(sheet.getRow(14).getCell(0)) // B15
          case "6" => cellOpt = Some(sheet.getRow(2).getCell(2)) // D3
          case "7" => cellOpt = Some(sheet.getRow(11).getCell(2)) // D12
          case "8" => cellOpt = Some(sheet.getRow(17).getCell(2)) // D18
        }
      } else if(p.batiment.get.equals("R5-N0")) {
        p.bureau.get match {
          case "1" => cellOpt = Some(sheet.getRow(5).getCell(4)) // F6
          case "2" => cellOpt = Some(sheet.getRow(11).getCell(6)) // H12
          case "3" => cellOpt = Some(sheet.getRow(8).getCell(4)) // F9
          case "4" => cellOpt = Some(sheet.getRow(17).getCell(4)) // F18
          case "6" => cellOpt = Some(sheet.getRow(11).getCell(4)) // F12
          case "7" => cellOpt = Some(sheet.getRow(2).getCell(6)) // H3
          case "8" => cellOpt = Some(sheet.getRow(14).getCell(4)) // F15
          case "9" => cellOpt = Some(sheet.getRow(2).getCell(4)) // F3
        }
      }
    }
    return cellOpt
  }
}
