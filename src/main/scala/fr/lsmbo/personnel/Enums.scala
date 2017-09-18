package fr.lsmbo.personnel

object Category extends Enumeration {
  type Category = Value
  val PERMANENT = Value("Permanent")
  val CONTRACTUEL = Value("Contractuel")
  val ETUDIANT = Value("Etudiant")
  val ANCIEN = Value("Ancien")
  val UNKNOWN = Value("Non défini")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}

object Status extends Enumeration {
  type Status = Value
  val CHERCHEUR_CDI = Value("Chercheur-CDI")
  val CHERCHEUR_CDD = Value("Chercheur-CDD")
  val ITA_CDI = Value("ITA-CDI")
  val ITA_CDD = Value("ITA-CDD")
  val POST_DOCTORANT = Value("Post-doctorant")
  val DOCTORANT = Value("Doctorant")
  val UNKNOWN = Value("Non défini")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}

object Location extends Enumeration {
  type Location = Value
  val R5 = Value("R5-N0")
  val R2 = Value("R2-N0")
  val UNKNOWN = Value("Non défini")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}

//object Enums {
//}
