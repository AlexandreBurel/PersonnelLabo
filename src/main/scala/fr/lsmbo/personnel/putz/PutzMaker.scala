package fr.lsmbo.personnel.putz

import java.text.SimpleDateFormat
import java.util.Calendar

import fr.lsmbo.personnel.{LocationList, MyConfig, People, TableauDuPersonnel}
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class PutzMaker(workbook: XSSFWorkbook) {

  final val MIN_PUTZ_PEOPLE = 12 // changed from 18 to 8 + 4 (8 labs and 4 solvants)
  final val MIN_COFFEE_PEOPLE = 2
  final val MIN_TOWEL_PEOPLE = 1
  final val ANONYMOUS = People(initiales = "<>")

  // check for the template sheet
  val templateSheetIndex: Int = workbook.getSheetIndex("PUTZ")
  if (templateSheetIndex < 0) {
    throw new Exception("Template sheet does not exist !")
  }
  // create a new name
  MyConfig.putzTitle = "Putz - " + monthes.head + "-" + monthes.last + " " + year
  // create a new sheet named like "Putz - janvier-mai 2017" based on Template sheet
  // TODO why do we clone the sheet instead of just renaming it ?
  val sheet: XSSFSheet = workbook.cloneSheet(templateSheetIndex, MyConfig.putzTitle)

  val personnel: Array[People] = TableauDuPersonnel.getPersonnel()
  // make sure that there is enough people for each task
  if (getLabRoomPutzablePeople(false).length < MIN_PUTZ_PEOPLE) {
    println(s"Warning, not enough people to putz the lab rooms ($MIN_PUTZ_PEOPLE required)")
  }
  if (getCoffeeRoomPutzablePeople(None, autoFill = false).length < MIN_COFFEE_PEOPLE) {
    println(s"Warning, not enough people to putz the coffee rooms ($MIN_COFFEE_PEOPLE required)")
  }
  if (getTowelsPutzablePeople(false).length < MIN_TOWEL_PEOPLE) {
    println(s"Warning, not enough people to putz the towels ($MIN_TOWEL_PEOPLE required)")
  }

  // for each month, define a random list of putzable people and put them in each room
  var i = 0
  val solvantPeople = new mutable.HashMap[Int, Array[People]]
  val labPeople: Array[People] = getSolvantsPutzablePeople()
  monthes.foreach(month => {
    val thisMonthPeople = labPeople.slice(4*i, 4*i+4)
//    println("Solvants People for "+monthes(i)+": ")
//    thisMonthPeople.foreach(p => println("- " + p.getDisambiguiedFirstName))
    solvantPeople.put(i+1, thisMonthPeople)
//    val labPeople = getSolvantsPutzablePeople(column-1)
    // Elimination des solvants usagers et des bouteilles en verre
    sheet.getRow(9).getCell(i+1).setCellValue(
      thisMonthPeople(0).selectMeForSolvants + "\n" +
        thisMonthPeople(1).selectMeForSolvants + "\n" +
        thisMonthPeople(2).selectMeForSolvants + "\n" +
        thisMonthPeople(3).selectMeForSolvants)
    i += 1
  })
  var column = 1
  monthes.foreach(month => {
    // set column title
    sheet.getRow(2).getCell(column).setCellValue(month + " " + year)
    // define lab people
    val labPeople = getLabRoomPutzablePeople2(solvantPeople(column))
//    println("List of people for "+month + " " + year+":")
//    labPeople.foreach(p => println("- " + p.getDisambiguiedFirstName + " (" + p.putzCounter + "/"+p.solvantCounter+")"))
    // changed the following lines to set one person per row, except for row 9 with 4 people
    // the height of the rows in the template excel file have been adapted
//    sheet.getRow(3).getCell(column).setCellValue(labPeople(0).selectMe + "\n" + labPeople(1).selectMe) // Labo LA1 R5
//    sheet.getRow(4).getCell(column).setCellValue(labPeople(2).selectMe + "\n" + labPeople(3).selectMe) // Labo LA2 R5
//    sheet.getRow(5).getCell(column).setCellValue(labPeople(4).selectMe + "\n" + labPeople(5).selectMe) // Labo LA3 R5
//    sheet.getRow(6).getCell(column).setCellValue(labPeople(6).selectMe + "\n" + labPeople(7).selectMe) // Labo LA4 R5
//    sheet.getRow(7).getCell(column).setCellValue(labPeople(8).selectMe + "\n" + labPeople(9).selectMe) // Salle Bio (LA5) + MassPrep R5
//    sheet.getRow(8).getCell(column).setCellValue(labPeople(10).selectMe + "\n" + labPeople(11).selectMe) // Sous-sol R5 (centri+speedvac)
//    sheet.getRow(9).getCell(column).setCellValue(labPeople(12).selectMeForSolvants + "\n" + labPeople(13).selectMeForSolvants) // Elimination des solvants usagers et des bouteilles en verre
//    sheet.getRow(10).getCell(column).setCellValue(labPeople(14).selectMe + "\n" + labPeople(15).selectMe) // Labo machines R2
//    sheet.getRow(11).getCell(column).setCellValue(labPeople(16).selectMe + "\n" + labPeople(17).selectMe) // Labo bio LA1 R2
//    sheet.getRow(9).getCell(column).setCellValue(labPeople(0).selectMeForSolvants + "\n" + labPeople(1).selectMeForSolvants + "\n" + labPeople(2).selectMeForSolvants + "\n" + labPeople(3).selectMeForSolvants) // Elimination des solvants usagers et des bouteilles en verre
    sheet.getRow(3).getCell(column).setCellValue(labPeople(0).selectMe) // Labo LA1 R5
    sheet.getRow(4).getCell(column).setCellValue(labPeople(1).selectMe) // Labo LA2 R5
    sheet.getRow(5).getCell(column).setCellValue(labPeople(2).selectMe) // Labo LA3 R5
    sheet.getRow(6).getCell(column).setCellValue(labPeople(3).selectMe) // Labo LA4 R5
    sheet.getRow(7).getCell(column).setCellValue(labPeople(4).selectMe) // Salle Bio (LA5) + MassPrep R5
    sheet.getRow(8).getCell(column).setCellValue(labPeople(5).selectMe) // Sous-sol R5 (centri+speedvac)
    sheet.getRow(10).getCell(column).setCellValue(labPeople(6).selectMe) // Labo machines R2
    sheet.getRow(11).getCell(column).setCellValue(labPeople(7).selectMe) // Labo bio LA1 R2
    // define coffee people
    sheet.getRow(12).getCell(column).setCellValue(getCoffeeRoomPutzablePeople(Some(LocationList.R2)).head.selectMe) // Salle de réunion/cafet R2
    sheet.getRow(13).getCell(column).setCellValue(getCoffeeRoomPutzablePeople(Some(LocationList.R5)).head.selectMe) // Salles de réunion/cafet R5
    sheet.getRow(14).getCell(column).setCellValue(getTowelsPutzablePeople()(0).selectMe) // Torchons et serviettes
    column += 1
  })

  private val tooManyPeople = personnel.filter(_.putzLabo.getOrElse(false)).filter(_.putzCounter > monthes.length)
  if(tooManyPeople.length > 0) {
    println("People with more than one task in the putz planning each month:")
    tooManyPeople.foreach(p => println("- " + p.getDisambiguiedFirstName + " (" + p.putzCounter + ")"))
  } else {
    println("List of tasks per person (putz/solvants):")
    personnel.filter(_.putzLabo.getOrElse(false)).foreach(p => println("- " + p.getDisambiguiedFirstName + " (" + p.putzCounter + "/"+p.solvantCounter+")"))
  }

  // remove template sheet
  workbook.removeSheetAt(templateSheetIndex)

  // print a summary for the creator, in case there should be manual edition
//  println("Putz people for lab rooms:")
//  personnel.filter(_.putzLabo.getOrElse(false)).sortBy(_.putzCounter).foreach(p => println(s"${p.toString}: ${p.putzCounter}  (${p.solvantCounter})"))

  private lazy val monthes: Array[String] = {
    // get month number (ie. May is 5)
    val dateFormatter = new SimpleDateFormat("M")
    var currentMonthNumber = dateFormatter.format(Calendar.getInstance.getTime).toInt
    if (!MyConfig.startCurrentMonth) currentMonthNumber += 1
    // return an array of month names
    val monthPerNumber = Map(1 -> "Janvier", 2 -> "Février", 3 -> "Mars", 4 -> "Avril", 5 -> "Mai", 6 -> "Juin", 7 -> "Juillet", 8 -> "Août", 9 -> "Septembre", 10 -> "Octobre", 11 -> "Novembre", 12 -> "Decembre")
    val monthes = new ArrayBuffer[String]
    for (i <- currentMonthNumber until currentMonthNumber + MyConfig.numberOfMonthes) {
      monthes += (if(i <= 12) monthPerNumber(i) else monthPerNumber(i - 12))
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
//    shuffledPeople.sortBy(p => (p.putzCounter, -p.solvantCounter)).toArray
    shuffledPeople.sortBy(p => p.putzCounter).toArray
  }

  private def getSolvantsPutzablePeople(autoFill: Boolean = true): Array[People] = {
//    val list = personnel.filter(_.putzLabo.getOrElse(false)).filter(_.solvantCounter == 0)
//    val shuffledPeople = util.Random.shuffle(list)
//    shuffledPeople.sortBy(p => (p.putzCounter, p.solvantCounter)).toArray
//    shuffleAndOrder(personnel.filter(_.putzLabo.getOrElse(false)).filter(_.solvantCounter == 0))
    val list = shuffleAndOrder(personnel.filter(_.putzLabo.getOrElse(false)))
    if(autoFill) autoFillList(list, monthes.length*4) else list
  }
  private def getLabRoomPutzablePeople(autoFill: Boolean = true): Array[People] = {
//    val list = ArrayBuffer(shuffleAndOrder(personnel.filter(_.putzLabo.getOrElse(false))): _*)
//    if(autoFill) while(list.size < MIN_PUTZ_PEOPLE) list.append(ANONYMOUS)
//    if(autoFill) {
//      var i = 0
//      while(list.size < MIN_PUTZ_PEOPLE) {
//        list.append(list(i))
//        i += 1
//      }
//    }
//    list.toArray
    val list = shuffleAndOrder(personnel.filter(_.putzLabo.getOrElse(false)))
    if(autoFill) autoFillList(list, MIN_PUTZ_PEOPLE, preferSolvantPeople = true) else list
  }
  private def getLabRoomPutzablePeople2(peopleToAvoid: Array[People]): Array[People] = {
    shuffleAndOrder(personnel.filter(_.putzLabo.getOrElse(false)).filter(p => !peopleToAvoid.contains(p)))
  }

  private def getCoffeeRoomPutzablePeople(locationOpt: Option[LocationList.Value], autoFill: Boolean = true): Array[People] = {
//    var list = ArrayBuffer(shuffleAndOrder(personnel.filter(_.putzCafe.get)): _*)
    ////    if(locationOpt.isDefined) list = list.filter(p => p.batiment.getOrElse("").equals(locationOpt.get.toString))
    //////    if(autoFill) while(list.size < MIN_COFFEE_PEOPLE) list.append(ANONYMOUS)
    ////    if(autoFill) {
    ////      var i = 0
    ////      while(list.size < MIN_COFFEE_PEOPLE) {
    ////        list.append(list(i))
    ////        i += 1
    ////      }
    ////    }
    ////    list.toArray
    val list = shuffleAndOrder(personnel.filter(p => {
      p.putzCafe.getOrElse(false) && (if(locationOpt.isDefined) p.batiment.getOrElse("").equals(locationOpt.get.toString) else true)
    }))
    if(autoFill) autoFillList(list, MIN_COFFEE_PEOPLE) else list
  }

  private def getTowelsPutzablePeople(autoFill: Boolean = true): Array[People] = {
//    val list = ArrayBuffer(shuffleAndOrder(personnel.filter(_.putzTorchon.get)): _*)
//    if(autoFill) while(list.size < MIN_TOWEL_PEOPLE) list.append(ANONYMOUS)
//    list.toArray
    val list = shuffleAndOrder(personnel.filter(_.putzTorchon.getOrElse(false)))
    if(autoFill) autoFillList(list, MIN_TOWEL_PEOPLE) else list
  }

  private def autoFillList(list: Array[People], limit: Int, preferSolvantPeople: Boolean = false): Array[People] = {
    if(list.length >= limit) list
    else {
      var i = 0
      if(preferSolvantPeople) {
        if(i == 0) i = 12
        if(i == 13) i = 0
      }
      val completeList = ArrayBuffer(list: _*)
      while(completeList.size < limit) {
        completeList.append(list(i))
        i += 1
      }
      completeList.toArray
    }
  }

}
