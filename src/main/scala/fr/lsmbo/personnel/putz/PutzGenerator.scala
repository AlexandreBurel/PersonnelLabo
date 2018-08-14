package fr.lsmbo.personnel.putz

import fr.lsmbo.personnel.MyConfig
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import org.apache.poi.xssf.usermodel.XSSFSheet
import scala.collection.mutable.ArrayBuffer
import fr.lsmbo.personnel.Person
import java.io.FileOutputStream
import fr.lsmbo.personnel.LocationList

class PutzGenerator {

  // TODO: make sure that "Elimination day" is fairly randomized (not everytime the same person)
  
  // the excel file must exist and have a template sheet !
  if (!MyConfig.putzOutputFile.exists) {
    throw new Exception("Xlsx file '" + MyConfig.putzOutputFile + "' does not exist !")
  }

  // directly write output file
  val workbook: XSSFWorkbook = {
    val fileInputStream = new FileInputStream(MyConfig.putzOutputFile)
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

  // make sure that there is enough people for each task
  if (getLabRoomPutzablePeople.size < 17) {
    throw new Exception("Not enough people to putz the lab rooms")
  }
  if (getCoffeeRoomPutzablePeople(None).size < 2) {
    throw new Exception("Not enough people to putz the coffee rooms")
  }
  if (getTowelsPutzablePeople.size < 2) {
    throw new Exception("Not enough people to putz the towels")
  }
  
  
  
  
  // for each month, define a random list of putzable people and put them in each room
  var column = 1
  monthes.foreach( month => {
    // set column title
    sheet.getRow(2).getCell(column).setCellValue(month + " " + year)
    // define lab people
    val labPeople = getLabRoomPutzablePeople
    sheet.getRow(3).getCell(column).setCellValue(labPeople(0).selectMe + "\n" + labPeople(1).selectMe)
    sheet.getRow(4).getCell(column).setCellValue(labPeople(2).selectMe + "\n" + labPeople(3).selectMe)
    sheet.getRow(5).getCell(column).setCellValue(labPeople(4).selectMe + "\n" + labPeople(5).selectMe)
    sheet.getRow(6).getCell(column).setCellValue(labPeople(6).selectMe + "\n" + labPeople(7).selectMe)
    sheet.getRow(7).getCell(column).setCellValue(labPeople(8).selectMe + "\n" + labPeople(9).selectMe)
    sheet.getRow(8).getCell(column).setCellValue(labPeople(10).selectMe + "\n" + labPeople(11).selectMe)
    sheet.getRow(9).getCell(column).setCellValue(labPeople(12).selectMe + "\n" + labPeople(13).selectMe)
    sheet.getRow(10).getCell(column).setCellValue(labPeople(14).selectMe + "\n" + labPeople(15).selectMe)
    sheet.getRow(11).getCell(column).setCellValue(labPeople(16).selectMe + "\n" + labPeople(17).selectMe)
    // define coffee people
    sheet.getRow(12).getCell(column).setCellValue(getCoffeeRoomPutzablePeople(Some(LocationList.R2)).head.selectMe)
    sheet.getRow(13).getCell(column).setCellValue(getCoffeeRoomPutzablePeople(Some(LocationList.R5)).head.selectMe)
    sheet.getRow(14).getCell(column).setCellValue(getTowelsPutzablePeople(0).selectMe)
    column += 1
  })
  
  // print a summary for the creator, in case there should be manual edition
  println("Putz people for lab rooms:")
  MyConfig.people.filter(_.putzLabRooms).sortBy(_.putzCounter).foreach(p => println(s"${p.toString}: ${p.putzCounter}"))
    
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
//      println(s"monthPerNumber.isDefinedAt($i)=${monthPerNumber.isDefinedAt(i)}")
//      monthes += monthPerNumber.get(i).get
      monthes += (if(i <= 12) monthPerNumber.get(i).get else monthPerNumber.get(i-12).get)
//      val m = (if(i <= 12) i else i - 12)
//      monthes += monthPerNumber.get(m).get
//      println(s"monthPerNumber.isDefinedAt($m)=${monthPerNumber.isDefinedAt(m)}")
    }
    monthes.toArray
  }

  private lazy val year: String = {
    // FIXME there's a bug when generating the putz planning at the end of the year, this method will return the current year instead
    val dateFormatter = new SimpleDateFormat("yyyy")
    dateFormatter.format(Calendar.getInstance.getTime)
  }

  private def shuffleAndOrder(p: Array[Person]): Array[Person] = {
    // shuffle people
    val shuffledPeople = util.Random.shuffle(p.toList)
    // return this list sorted by counter
    shuffledPeople.sortBy(_.putzCounter).toArray
  }

  private def getLabRoomPutzablePeople: Array[Person] = shuffleAndOrder(MyConfig.people.filter(_.putzLabRooms))

  private def getCoffeeRoomPutzablePeople(locationOpt: Option[LocationList.Value]): Array[Person] = {
    if(locationOpt.isDefined)
      shuffleAndOrder(MyConfig.people.filter(p => p.putzCoffeeRooms && p.location.value.equals(locationOpt.get)))
    else
      shuffleAndOrder(MyConfig.people.filter(_.putzCoffeeRooms))
  }
  
  private def getTowelsPutzablePeople: Array[Person] = shuffleAndOrder(MyConfig.people.filter(_.putzTowels))
  
}
