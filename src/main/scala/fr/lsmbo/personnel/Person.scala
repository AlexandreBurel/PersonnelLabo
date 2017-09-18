package fr.lsmbo.personnel

import java.io.File

case class Person(
    firstName: String,
    lastName: String,
    initials: String,
    category: Category.Value,
    status: Status.Value,
    location: Location.Value,
    room: Int,
    mail: String,
    phone: String,
    order: Int,
    labRooms: Boolean,
    coffeeRooms: Boolean,
    hidden: Boolean,
    picture: String,
    var counter: Int = 0
    ) {
  
  override def toString: String = s"$firstName $lastName ($initials)"
  
  def selectMe: String = {
    counter += 1
    initials
  }
  
  def getPicture: File = {
    val file = new File(MyConfig.trombiPictureFolder.getAbsolutePath + "/" + picture)
    if(file.exists()) {
      return file
    } else {
      return MyConfig.defaultPicture
    }
  }
}