package fr.lsmbo.personnel

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.File
import java.net.URL
import scala.collection.mutable.ArrayBuffer

object MyConfig {
  
  lazy val config: Config = ConfigFactory.load(this.getClass().getClassLoader(), "application.conf")

//  lazy val peopleFile: File = new File(config.getString("people.file.path"))
  lazy val peopleFile: URL = this.getClass().getClassLoader().getResource(config.getString("people.file.path"))
  
  lazy val putzOutputFile: File = {
    try {
      val file: URL = this.getClass().getClassLoader().getResource(config.getString("putz.output.file.path"))
      new File(file.getPath)
//      new File(config.getString("output.file.path"))
    } catch {
      case e: Exception =>
        println("ABU "+e.getMessage)
        new File("PutzPlanning.xlsx")
    }
//    try {
//      new File(this.getClass().getClassLoader().getResource(config.getString("output.file.path")).getPath)
//    } catch {
//      case e: Exception =>
//        println(s"Error on trombiPictureFolder: ${e.getMessage}")
//        new File("classes/img")
//    }
  }
  
  lazy val startCurrentMonth: Boolean = {
    try {
      config.getBoolean("start.current.month")
    } catch {
      case e: Exception => false
    }
  }
  
  lazy val numberOfMonthes: Int = {
    try {
      config.getInt("number.of.monthes")
    } catch {
      case e: Exception => 4
    }
  }
  
  lazy val trombiOutputFile: File = {
    try {
      new File(config.getString("trombi.output.file.path"))
    } catch {
      case e: Exception => new File("Trombinoscope.xlsx")
    }
  }
  
  lazy val trombiPictureFolder: File = {
    try {
      new File(this.getClass().getClassLoader().getResource(config.getString("trombi.picture.folder")).getPath)
    } catch {
      case e: Exception =>
        println(s"Error on trombiPictureFolder: ${e.getMessage}")
        new File("classes/img")
    }
  }
  
  lazy val defaultPicture: File = {
    try { 
      new File(trombiPictureFolder, config.getString("trombi.picture.default"))
    } catch {
      case e: Exception => new File("")
    }
  }
  
  
 
  lazy val people: Array[Person] = {
    val personnel = new ArrayBuffer[Person]
    val bufferedSource = io.Source.fromURL(MyConfig.peopleFile)
    for (line <- bufferedSource.getLines) {
      if(!line.startsWith("lastName")) {
        val cols = line.split("\t").map(_.trim)
        try {
          personnel += new Person(
              firstName = cols(1).toUpperCase(), lastName = cols(0), initials = cols(2).toUpperCase(),
              category = Category.withNameOpt(cols(3)).getOrElse(Category.UNKNOWN),
              status = Status.withNameOpt(cols(4)).getOrElse(Status.UNKNOWN),
              location = Location.withNameOpt(cols(5)).getOrElse(Location.UNKNOWN),
              room = cols(6).toInt, mail = cols(7), phone = cols(8),
              labRooms = cols(10).toBoolean, coffeeRooms = cols(11).toBoolean, hidden = cols(12).toBoolean,
              order = cols(9).toInt, picture = cols(13))
        } catch {
          case e: Exception => e.printStackTrace()
        }
      }
    }
    bufferedSource.close
    personnel.toArray
  }

}
