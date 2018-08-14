package fr.lsmbo.personnel

object CategoryList extends Enumeration {
  type CategoryList = Value
  val PERMANENT = Value("Permanent")
  val CONTRACTUEL = Value("Contractuel")
  val ETUDIANT = Value("Etudiant")
  val ANCIEN = Value("Ancien")
  val UNKNOWN = Value("Non défini")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}

object StatusList extends Enumeration {
  type StatusList = Value
//  val CHERCHEUR_CDI = Value("Chercheur-CDI") // remplacer par Chercheur
//  val CHERCHEUR_CDD = Value("Chercheur-CDD") // supprimer
  val CHERCHEUR = Value("Chercheur")
//  val ITA_CDI = Value("ITA-CDI") // remplacer par ITA
//  val ITA_CDD = Value("ITA-CDD") // supprimer
  val ITA = Value("ITA")
  val POST_DOCTORANT = Value("Post-doctorant")
  val DOCTORANT = Value("Doctorant")
  val STAGIAIRE = Value("Stagiaire")
  val UNKNOWN = Value("Non défini")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString.toLowerCase() == s.toLowerCase())
}

object LocationList extends Enumeration {
  type LocationList = Value
  val R5 = Value("R5-N0")
  val R2 = Value("R2-N0")
  val IPHC = Value("27")
  val UNKNOWN = Value("Non défini")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}
