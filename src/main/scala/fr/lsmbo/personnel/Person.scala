package fr.lsmbo.personnel

import java.io.File
import java.util.Date
import scala.collection.mutable.ArrayBuffer

case class Person(
    firstName: String,
    lastName: String,
    initials: String,
    category: Category,
    status: Status,
    location: Location,
//    category: String,
//    status: String,
//    location: String,
    room: String,
    mail: String,
    phone: String,
//    order: Int, // TODO remove this one
    putzLabRooms: Boolean, // rename to putzLabRooms
    putzCoffeeRooms: Boolean, // rename to putzCoffeeRooms
    putzTowels: Boolean,
//    hidden: Boolean,  // remove this one
    // TODO add putzTowels
    picture: String,
    birthday: String,
    var putzCounter: Int = 0
    ) {
  
  override def toString: String = s"$firstName $lastName ($initials)"
  
  def selectMe: String = {
    putzCounter += 1
    initials
  }
  
  def getPicture: File = {
    val file = new File(MyConfig.trombiPictureFolder.getAbsolutePath + "/" + picture)
    if(file.exists() && file.isFile()) {
      return file
    } else {
      return MyConfig.defaultPicture
    }
  }
  
//  def getFullStatus: String = {
//    var fullStatus = ""
//    status match {
//      case Status.CHERCHEUR_CDD | Status.CHERCHEUR_CDI => fullStatus += "Chercheur"
//      case Status.ITA_CDD | Status.ITA_CDI => fullStatus += "Ingénieur"
//      case Status.UNKNOWN => ""
//      case _ => fullStatus += status.toString()
//    }
//    fullStatus += s" ($category)"
//    fullStatus
//  }
  def getFullStatus: String = s"${status.get} (${category.get})"
  
  def formattedBirthday: String = {
    // excel format is list 14-sept
    if(birthday.equals("x") || !birthday.contains("-")) {
      ""
    } else {
      val dm = birthday.split("-")
      dm(1) match {
        case "janv" => s"${dm(0)} janvier"
        case "févr" => s"${dm(0)} février"
        case "mars" => s"${dm(0)} mars"
        case "avr" => s"${dm(0)} avril"
        case "mai" => s"${dm(0)} mai"
        case "juin" => s"${dm(0)} juin"
        case "juil" => s"${dm(0)} juillet"
        case "août" => s"${dm(0)} août"
        case "sept" => s"${dm(0)} septembre"
        case "oct" => s"${dm(0)} octobre"
        case "nov" => s"${dm(0)} novembre"
        case "déc" => s"${dm(0)} décembre"
        case _ => ""
      }
    }
  }
  
  def check {
    if(category.value == CategoryList.UNKNOWN) println(s"Warning: $firstName $lastName's category is not recognized: ${category.name}")
    if(status.value == StatusList.UNKNOWN) println(s"Warning: $firstName $lastName's status is not recognized: ${status.name}")
    if(location.value == LocationList.UNKNOWN) println(s"Warning: $firstName $lastName's location is not recognized: ${location.name}")
  }
  
}

case class Category(value: CategoryList.Value, name: String) {
  def this(name: String) = this(CategoryList.withNameOpt(name).getOrElse(if(name == "") CategoryList.ANCIEN else CategoryList.UNKNOWN), name)
  def get: String = if(value.equals(CategoryList.UNKNOWN)) name else value.toString()
  lazy val order: Int = {
    value match {
      case CategoryList.PERMANENT => 0
      case CategoryList.CONTRACTUEL => 1
      case CategoryList.ETUDIANT => 2
      case CategoryList.UNKNOWN => 3
      case _ => 4
    }
  }
}
case class Status(value: StatusList.Value, name: String) {
  def this(name: String) = this(StatusList.withNameOpt(name).getOrElse(StatusList.UNKNOWN), name)
  def get: String = if(value.equals(StatusList.UNKNOWN)) name else value.toString()
  lazy val order: Int = {
    value match {
      case StatusList.CHERCHEUR => 0
      case StatusList.POST_DOCTORANT => 1
      case StatusList.ITA => 2
      case StatusList.DOCTORANT => 3
      case StatusList.STAGIAIRE => 4
      case StatusList.UNKNOWN => 5
      case _ => 6
    }
  }
}
case class Location(value: LocationList.Value, name: String) {
  def this(name: String) = this(LocationList.withNameOpt(name).getOrElse(LocationList.UNKNOWN), name)
  def get: String = if(value.equals(LocationList.UNKNOWN)) name else value.toString()
}
