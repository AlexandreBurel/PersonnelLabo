package fr.lsmbo.personnel

import java.nio.file.{Files, StandardCopyOption}
import java.time.{LocalDate, ZoneId}

import org.apache.poi.ss.usermodel.{CellType, DateUtil, Row, WorkbookFactory}

import scala.collection.mutable.ArrayBuffer

object TableauDuPersonnel {

  def getPersonnel(currentPeopleOnly: Boolean = true): Array[People] = {
    val list = if (currentPeopleOnly) personnel.filter(_.isValid) else personnel
    list.foreach(p => {
      p.isUnique = list.count(_.prenom.get.equals(p.prenom.get)) == 1
    })
    list
  }

  lazy val personnel: Array[People] = {
    val allPeople = new ArrayBuffer[People]
    // copy file in temp directory (and erase it at the end)
    val tempFile = Files.createTempFile("", "").toFile
    tempFile.deleteOnExit()
    Files.copy(MyConfig.personnelFile.toPath, tempFile.toPath, StandardCopyOption.REPLACE_EXISTING)
    // read excel file
    val workbook = WorkbookFactory.create(tempFile)
    val sheet = workbook.getSheetAt(0)
    val it = sheet.rowIterator()
    // first lines are headers and info
    while (it.hasNext && it.next().getRowNum < 4) {}

    while (it.hasNext) {
      val row = it.next()
      // do nothing if the line is empty
      if (!isBlank(row)) {
        val initiales = getString(row, 0)
        val nom = getString(row, 1)
        var prenom = getString(row, 2)
        if(prenom.getOrElse("").equals("Liz-Paola")) prenom = Some("Paola") // just to avoid correcting manually later...
        else if(prenom.getOrElse("").equals("Jeewan-Babu")) prenom = Some("Jeewan")
        else if(prenom.getOrElse("").equals("Noelia Milagros")) prenom = Some("Noelia")
        val depart = getDate(row, 11)
        val people = People(initiales.getOrElse(""), nom, prenom, Some(new Corps(getString(row, 3).getOrElse(""))),
          getString(row, 4), getString(row, 5), getString(row, 6), getString(row, 7), getString(row, 8),
          getString(row, 9), getDate(row, 10), depart, getNumeric(row, 12), getString(row, 13), getString(row, 14),
          getDate(row, 15), getString(row, 16), getString(row, 17), getDate(row, 18), getDate(row, 19),
          getString(row, 20), getString(row, 21), getString(row, 22), getNumeric(row, 23), getBoolean(row, 24),
          getBoolean(row, 25), getBoolean(row, 26), getString(row, 27), getDate(row, 28), getBoolean(row, 29))
        // one line per contract, so we need to merge people if same initials or same name
//        val indexOfSamePeople = {
//          if (initiales.isDefined) allPeople.indexWhere(_.initiales.equals(people.initiales))
//          else allPeople.indexWhere(p => (p.nom.getOrElse("nom") + p.prenom.getOrElse("prenom")).equals(nom.getOrElse("") + prenom.getOrElse("")))
//        }
        val indexOfSamePeople = allPeople.indexWhere(p => {
          (p.nom.getOrElse("nom") + p.prenom.getOrElse("prenom")).equals(nom.getOrElse("") + prenom.getOrElse("")) || initiales.getOrElse("") == p.getInitiales
        })
        if (indexOfSamePeople != -1) {
          // merge both (and if ambiguity, prefer newer contract)
          allPeople(indexOfSamePeople).merge(people)
        } else {
          // add people to the list
          allPeople += people
        }
      }
    }
    workbook.close()
    allPeople.toArray
  }

  private def isBlank(row: Row): Boolean = {
    val it = row.cellIterator()
    var isBlank = true
    while (it.hasNext) {
      val c = it.next()
      if (!c.getCellType.equals(CellType.BLANK)) isBlank = false
    }
    isBlank
  }

  private def getString(row: Row, index: Int): Option[String] = {
    try {
      row.getCell(index).getCellType match {
        case CellType.STRING => Some(row.getCell(index).getRichStringCellValue.getString)
        case CellType.NUMERIC => Some(row.getCell(index).getNumericCellValue.toInt.toString)
        case CellType.BOOLEAN => Some(row.getCell(index).getBooleanCellValue.toString)
        case _ => None
      }
    } catch {
      case _: Throwable => None
    }
  }

  private def getNumeric(row: Row, index: Int): Option[Int] = {
    try {
      if (row.getCell(index).getCellType.equals(CellType.NUMERIC)) {
        return Some(row.getCell(index).getNumericCellValue.toInt)
      }
    } catch {
      case _: Throwable =>
    }
    None
  }

  private def getBoolean(row: Row, index: Int): Option[Boolean] = {
    try {
      if (row.getCell(index).getCellType.equals(CellType.BOOLEAN)) {
        Some(row.getCell(index).getBooleanCellValue)
      } else if (row.getCell(index).getCellType.equals(CellType.BLANK)) {
        None
      } else {
        row.getCell(index).getRichStringCellValue.getString.toLowerCase match {
          case "true" => Some(true)
          case "false" => Some(false)
          case _ => None
        }
      }
    } catch {
      case _: Throwable => None
    }
  }

  private def getDate(row: Row, index: Int): Option[LocalDate] = {
    try {
      if (row.getCell(index).getCellType.equals(CellType.NUMERIC) && DateUtil.isCellDateFormatted(row.getCell(index))) {
        Some(row.getCell(index).getDateCellValue.toInstant.atZone(ZoneId.systemDefault()).toLocalDate)
      } else None
    } catch {
      case _: Throwable => None
    }
  }
}
