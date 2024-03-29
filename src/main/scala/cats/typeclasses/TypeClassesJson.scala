package cats.typeclasses

final case class Person(name: String, email: String)

//Define a very simple JSON AST
sealed trait Json
final case class JsObject(get: Map[String, Json]) extends Json
final case class JsString(get: String) extends Json
final case class JsNumber(get: Number) extends Json
case object JsNull extends Json

//The "Serialize the JSON" behavior is encoded in this trait [type-class]
trait JsonWriter[A] {
  def write(value: A): Json
}

//[type-class instance]
object JsonWriterInstance {

  implicit val stringWriter: JsonWriter[String] = new JsonWriter[String] {
    override def write(value: String): Json = JsString(value)
  }

  //Single Abstract Method
  implicit val numberWriter: JsonWriter[Int] = (value: Int) => JsNumber(value)

  implicit val personWriter: JsonWriter[Person] = new JsonWriter[Person] {
    override def write(value: Person): Json = {
      JsObject(
        Map(
          "name" -> JsString(value.name),
          "email" -> JsString(value.email)
        )
      )
    }
  }

  implicit def optionWriter[A](implicit writer: JsonWriter[A]):JsonWriter[Option[A]] =   new JsonWriter[Option[A]] {
    override def write(value: Option[A]): Json = value match {
      case Some(aValue) => writer.write(aValue)
      case None         => JsNull
    }
  }
}

//interface object
object Json {
  def toJson[A](value: A)(implicit writer: JsonWriter[A]): Json = {
    writer.write(value)
  }
}

object test1 extends App {

  import JsonWriterInstance.{stringWriter, personWriter}

  val result: Json = Json.toJson("Hello")
  val person: Json = Json.toJson(Person("aamir", "dropmeataaamira@gmail.com"))
  println(result)
  println(person)
}

//interface syntax
object JsonSyntax {
  implicit class JsonWriterOps[A](value: A) {
    def toJson(implicit writer: JsonWriter[A]): Json = writer.write(value)
  }
}

object test2 extends App {

  import JsonSyntax._
  import JsonWriterInstance.stringWriter
  import JsonWriterInstance.personWriter

  val res1: Json = "Hello".toJson
  val res2: Json = Person("aamir", "email").toJson
  println(res1)
  println(res2)
}

//implicitly
object test3 extends App {

  import JsonWriterInstance.stringWriter

  val res: JsonWriter[String] = implicitly[JsonWriter[String]]
  //implicitly[Double] // No implicit found for parameter Double
  println(res.write("hello"))
}

//Now, placing implicit values in companion objects
trait JsonWriter1[A] {
  def write(value: A): Json
}

object JsonWriter1 {
  implicit val stringWriter: JsonWriter1[String] = new JsonWriter1[String] {
    override def write(value: String): Json = JsString(value)
  }
}

object Json1 {
  def toJson1[A](value: A)(implicit writer: JsonWriter1[A]): Json = {
    writer.write(value)
  }
}

object test4 extends App {
  /*
  * Here we placed type class instances in companion objects of the type class
  * */
  val res: Json = Json1.toJson1("hello")
  println(res)
}

//ambiguous implicit value error
object test5 extends App {

  object TCI {
    implicit val writer1: JsonWriter[String] = (value: String) => JsString(value)
    implicit val writer2: JsonWriter[String] = (value: String) => JsString(value)
  }

  import TCI._
  //Json.toJson("A string") // not able to find implicits, ambiguity
}

/*
consider defining a JsonWriter for Option. We would need a
JsonWriter[Option[A]] instance for every A we care about in our application. We
could try to brute force the problem by creating a library of implicit
however, this approach clearly doesn’t scale. We end up requiring two
implicit vals for every type A in our application: one for A and one for
Option[A]
 */

object OptionInstances {

  implicit val optionInstance:JsonWriter[Option[Int]] = new JsonWriter[Option[Int]] {
    override def write(value: Option[Int]): Json = JsNumber(value.get)
  }
  //Now, I have to define Option[String] for String option and for other types too.
  //Fortunately, we can abstract the code for handling Option[A] into a common constructor based on the instance for A:

}

object GenericOptionTypeClassHandler extends App {

  import JsonWriterInstance._
  val res: Json = Json.toJson(Option("abc"))
  println(res)
  /*

   In this way, implicit resolution becomes a search through the space of possible
   combinations of implicit definitions, to find a combination that creates a type
   class instance of the correct overall type.

 */
}

