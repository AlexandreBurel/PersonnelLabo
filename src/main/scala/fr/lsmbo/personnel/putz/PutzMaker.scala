package fr.lsmbo.personnel.putz

import java.io
import java.io.{FileInputStream, FileOutputStream}
import java.text.SimpleDateFormat
import java.util.Calendar

import fr.lsmbo.personnel.{LocationList, MyConfig, People, TableauDuPersonnel}
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}

import scala.collection.mutable.ArrayBuffer

class PutzMaker {

  final val MIN_PUTZ_PEOPLE = 18
  final val MIN_COFFEE_PEOPLE = 2
  final val MIN_TOWEL_PEOPLE = 1
  final val ANONYMOUS = new People(initiales = "<>")

  // the excel file must exist and have a template sheet !
  if (!MyConfig.putzTemplateFile.exists) {
    throw new Exception("Xlsx file '" + MyConfig.putzTemplateFile.getAbsolutePath + "' does not exist !")
  }

  // directly write output file
  val workbook: XSSFWorkbook = {
    val fileInputStream = new FileInputStream(MyConfig.putzTemplateFile)
    new XSSFWorkbook(fileInputStream)
  }
  // check for the template sheet
  val templateSheetIndex = workbook.getSheetIndex("Template")
  if (templateSheetIndex < 0) {
    throw new Exception("Template sheet does not exist !")
  }
  // make sure the sheet name is new
  var sheetName = monthes.head + "-" + monthes.last + " " + year
  var i = 2
  while (workbook.getSheetIndex(sheetName) >= 0) {
    sheetName = monthes.head + "-" + monthes.last + " " + year + " (" + i + ")"
    i += 1
  }
  // create a new sheet named like "janvier-mai 2017" based on Template sheet
  val sheet: XSSFSheet = workbook.cloneSheet(templateSheetIndex, sheetName)

  val personnel = TableauDuPersonnel.getPersonnel()
  // make sure that there is enough people for each task
  if (getLabRoomPutzablePeople(false).size < MIN_PUTZ_PEOPLE) {
    println(s"Warning, not enough people to putz the lab rooms ($MIN_PUTZ_PEOPLE required)")
//    throw new Exception("Not enough people to putz the lab rooms")
  }
  if (getCoffeeRoomPutzablePeople(None, false).size < MIN_COFFEE_PEOPLE) {
    println(s"Warning, not enough people to putz the coffee rooms ($MIN_COFFEE_PEOPLE required)")
//    throw new Exception("Not enough people to putz the coffee rooms")
  }
  if (getTowelsPutzablePeople(false).size < MIN_TOWEL_PEOPLE) {
    println(s"Warning, not enough people to putz the towels ($MIN_TOWEL_PEOPLE required)")
//    throw new Exception("Not enough people to putz the towels")
  }

  // for each month, define a random list of putzable people and put them in each room
  var column = 1
  monthes.foreach(month => {
    // set column title
    sheet.getRow(2).getCell(column).setCellValue(month + " " + year)
    // define lab people
    val labPeople = getLabRoomPutzablePeople()
    sheet.getRow(3).getCell(column).setCellValue(labPeople(0).selectMe + "\n" + labPeople(1).selectMe) // Labo LA1 R5
    sheet.getRow(4).getCell(column).setCellValue(labPeople(2).selectMe + "\n" + labPeople(3).selectMe) // Labo LA2 R5
    sheet.getRow(5).getCell(column).setCellValue(labPeople(4).selectMe + "\n" + labPeople(5).selectMe) // Labo LA3 R5
    sheet.getRow(6).getCell(column).setCellValue(labPeople(6).selectMe + "\n" + labPeople(7).selectMe) // Labo LA4 R5
    sheet.getRow(7).getCell(column).setCellValue(labPeople(8).selectMe + "\n" + labPeople(9).selectMe) // Salle Bio (LA5) + MassPrep R5
    sheet.getRow(8).getCell(column).setCellValue(labPeople(10).selectMe + "\n" + labPeople(11).selectMe) // Sous-sol R5 (centri+speedvac)
    sheet.getRow(9).getCell(column).setCellValue(labPeople(12).selectMeForSolvants + "\n" + labPeople(13).selectMeForSolvants) // Elimination des solvants usagers et des bouteilles en verre
    sheet.getRow(10).getCell(column).setCellValue(labPeople(14).selectMe + "\n" + labPeople(15).selectMe) // Labo machines R2
    sheet.getRow(11).getCell(column).setCellValue(labPeople(16).selectMe + "\n" + labPeople(17).selectMe) // Labo bio LA1 R2
    // define coffee people
    sheet.getRow(12).getCell(column).setCellValue(getCoffeeRoomPutzablePeople(Some(LocationList.R2)).head.selectMe) // Salle de réunion/cafet R2
    sheet.getRow(13).getCell(column).setCellValue(getCoffeeRoomPutzablePeople(Some(LocationList.R5)).head.selectMe) // Salles de réunion/cafet R5
    sheet.getRow(14).getCell(column).setCellValue(getTowelsPutzablePeople()(0).selectMe) // Torchons et serviettes
    column += 1
  })

  // print a summary for the creator, in case there should be manual edition
  println("Putz people for lab rooms:")
  personnel.filter(_.putzLabo.get).sortBy(_.putzCounter).foreach(p => println(s"${p.toString}: ${p.putzCounter}"))

  // write, close and quit
  val outputStream = new FileOutputStream(MyConfig.putzOutputFile)
  workbook.write(outputStream)
  workbook.close()


  private lazy val monthes: Array[String] = {
    // get month number (ie. May is 5)
    val dateFormatter = new SimpleDateFormat("M")
    var currentMonthNumber = dateFormatter.format(Calendar.getInstance.getTime).toInt
    if (!MyConfig.startCurrentMonth) currentMonthNumber += 1
    // return an array of month names
    val monthPerNumber = Map(1 -> "Janvier", 2 -> "Février", 3 -> "Mars", 4 -> "Avril", 5 -> "Mai", 6 -> "Juin", 7 -> "Juillet", 8 -> "Août", 9 -> "Septembre", 10 -> "Octobre", 11 -> "Novembre", 12 -> "Decembre")
    val monthes = new ArrayBuffer[String]
    for (i <- currentMonthNumber until currentMonthNumber + MyConfig.numberOfMonthes) {
      monthes += (if(i <= 12) monthPerNumber.get(i).get else monthPerNumber.get(i-12).get)
    }
    monthes.toArray
  }

  private lazy val year: String = {
    // FIXME there's a bug when generating the putz planning at the end of the year, this method will return the current year instead
    val dateFormatter = new SimpleDateFormat("yyyy")
    dateFormatter.format(Calendar.getInstance.getTime)
  }

  private def shuffleAndOrder(p: Array[People]): Array[People] = {
    // shuffle people
    val shuffledPeople = util.Random.shuffle(p.toList)
    // return this list sorted by counter
    // also sorting by solvantCounter to make sure that "Elimination day" is fairly randomized (not everytime the same person)
    shuffledPeople.sortBy(p => (p.putzCounter, p.solvantCounter)).toArray
  }

  private def getLabRoomPutzablePeople(autoFill: Boolean = true): Array[People] = {
//    shuffleAndOrder(personnel.filter(_.putzLabo.getOrElse(false)))
    val list = ArrayBuffer(shuffleAndOrder(personnel.filter(_.putzLabo.getOrElse(false))): _*)
    if(autoFill) while(list.size < MIN_PUTZ_PEOPLE) list.append(ANONYMOUS)
    list.toArray
  }

  private def getCoffeeRoomPutzablePeople(locationOpt: Option[LocationList.Value], autoFill: Boolean = true): Array[People] = {
//    if(locationOpt.isDefined)
//      shuffleAndOrder(personnel.filter(p => p.putzCafe.get && p.batiment.getOrElse("").equals(locationOpt.get.toString)))
//    else
//      shuffleAndOrder(personnel.filter(_.putzCafe.get))
    var list = ArrayBuffer(shuffleAndOrder(personnel.filter(_.putzCafe.get)): _*)
    if(locationOpt.isDefined) list = list.filter(p => p.batiment.getOrElse("").equals(locationOpt.get.toString))
    if(autoFill) while(list.size < MIN_COFFEE_PEOPLE) list.append(ANONYMOUS)
    list.toArray
  }

//  private def getTowelsPutzablePeople: Array[People] = shuffleAndOrder(personnel.filter(_.putzTorchon.get))
  private def getTowelsPutzablePeople(autoFill: Boolean = true): Array[People] = {
    val list = ArrayBuffer(shuffleAndOrder(personnel.filter(_.putzTorchon.get)): _*)
    if(autoFill) while(list.size < MIN_TOWEL_PEOPLE) list.append(ANONYMOUS)
    list.toArray
  }
}
