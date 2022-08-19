package fr.lsmbo.personnel.map

import fr.lsmbo.personnel.{MyConfig, People, TableauDuPersonnel}
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFSheet, XSSFWorkbook}

class MapMaker(workbook: XSSFWorkbook) {

  // check for the template sheet
  val templateSheetIndex: Int = workbook.getSheetIndex("MAP")
  if (templateSheetIndex < 0) {
    throw new Exception("Template sheet does not exist !")
  }

  // rename the sheet
  workbook.setSheetName(templateSheetIndex, MyConfig.mapTitle)

  // get the sheet
  val sheet: XSSFSheet = workbook.getSheetAt(templateSheetIndex)

  TableauDuPersonnel.getPersonnel().foreach(p => {
    val cell = getCell(p)
    try {
      if (cell.isDefined) {
        if(cell.get.getStringCellValue.isEmpty) {
          cell.get.setCellValue(p.getDisambiguiedFirstName)
        } else {
          cell.get.setCellValue(cell.get.getStringCellValue + ", " + p.getDisambiguiedFirstName)
        }
      }
    } catch {
      case e: Exception => println(s"Error on cell ${if(cell.get != null) cell.get.getAddress else "<?>"} for ${p.toString} in ${p.batiment.get} bureau ${p.bureau.get}: ${e.getMessage}")
    }
  })

  private def getCell(p: People):  Option[XSSFCell] = {
    var cellOpt: Option[XSSFCell] = None
    if(p.batiment.isDefined && p.bureau.isDefined) {
      if(p.batiment.get.equals("R2-N0")) {
        p.bureau.get match {
          case "1" => cellOpt = Some(sheet.getRow(4).getCell(0)) // A5
          case "3" => cellOpt = Some(sheet.getRow(8).getCell(0)) // A9
          case "4" => cellOpt = Some(sheet.getRow(10).getCell(0)) // A11
          case "5" => cellOpt = Some(sheet.getRow(12).getCell(0)) // A13
          case "6" => cellOpt = Some(sheet.getRow(2).getCell(3)) // D3
          case "7" => cellOpt = Some(sheet.getRow(10).getCell(3)) // D11
          case "8" => cellOpt = Some(sheet.getRow(18).getCell(3)) // D19
          case _ => System.err.println(s"${p.prenom} ${p.nom} est dans le bureau '${p.bureau}' du ${p.batiment} qui n'est pas reconnu")
        }
      } else if(p.batiment.get.equals("R5-N0")) {
        p.bureau.get match {
          case "1" => cellOpt = Some(sheet.getRow(4).getCell(6)) // G5
          case "2" => cellOpt = Some(sheet.getRow(10).getCell(9)) // J11
          case "3" => cellOpt = Some(sheet.getRow(6).getCell(6)) // G7
          case "4" => cellOpt = Some(sheet.getRow(13).getCell(6)) // G14
          case "6" => cellOpt = Some(sheet.getRow(8).getCell(6)) // G9
          case "7" => cellOpt = Some(sheet.getRow(2).getCell(9)) // J3
          case "8" => cellOpt = Some(sheet.getRow(11).getCell(6)) // G12
          case "9" => cellOpt = Some(sheet.getRow(2).getCell(6)) // G3
          case _ => System.err.println(s"${p.prenom} ${p.nom} est dans le bureau '${p.bureau}' du ${p.batiment} qui n'est pas reconnu")
        }
      }
    }
    cellOpt
  }
}
