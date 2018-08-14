package fr.lsmbo.personnel

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.File
import java.net.URL
import scala.collection.mutable.ArrayBuffer

object MyConfig {
  
  lazy val config: Config = ConfigFactory.load(this.getClass().getClassLoader(), "application.conf")

//  lazy val peopleFile: File = new File(config.getString("people.file.path"))
//  lazy val peopleFile: URL = this.getClass().getClassLoader().getResource(config.getString("people.file.path"))
  lazy val peopleFile: URL = new File("classes/"+config.getString("people.file.path")).toURI.toURL()
  
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
      //new File(this.getClass().getClassLoader().getResource(config.getString("trombi.picture.folder")).getPath)
      new File(config.getString("trombi.picture.folder"))
    } catch {
      case e: Exception =>
        println(s"Error on trombiPictureFolder: ${e.getMessage}")
        new File("classes/img")
    }
  }
  
  lazy val defaultPicture: File = new File("classes/Portrait_placeholder.png")
  
  lazy val allPeople: Array[Person] = {
    val personnel = new ArrayBuffer[Person]
    val bufferedSource = io.Source.fromURL(MyConfig.peopleFile)
    var firstLine = true
    for (line <- bufferedSource.getLines) {
      if(firstLine) {
        firstLine = false
      } else {
        val cols = line.split("\t").map(_.trim)
        try {
          val person = new Person(
              lastName = cols(0), firstName = cols(1), initials = cols(2).toUpperCase(),
              category = new Category(cols(3)), status = new Status(cols(4)), 
              location = new Location(cols(5)), room = cols(6), 
              mail = cols(7), phone = cols(8), 
              putzLabRooms = cols(9).toBoolean, putzCoffeeRooms = cols(10).toBoolean, putzTowels = cols(11).toBoolean, 
              picture = cols(12), birthday = cols(13))
          person.check
          personnel += person
        } catch {
          case e: Exception =>
            println(s"Error on $line")
            e.printStackTrace()
        }
      }
    }
    bufferedSource.close
    personnel.toArray
  }
  
  lazy val people: Array[Person] = allPeople.filterNot(_.category.value.equals(CategoryList.ANCIEN))

}
