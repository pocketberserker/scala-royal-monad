package royal

import scalaz._
import scalaz.std.list._
import scalaprops._

object RoyalMonadTest extends Scalaprops {

  implicit val nonRoyalInstance: NonRoyalReturn[List] = Royal.nonRoyalReturnInstance[List]

  val rightIdentity = Property.forAll { m: List[Int] =>
    val M = Royal.royalMonadInstance[List]
    val R = Royal.royalReturnInstance[List]
    M.royalBind(m)(R.royalReturn) == m
  }

  val leftIdentity = Property.forAll { (x: Int, f: Int => List[Int]) =>
    val M = Royal.royalMonadInstance[List]
    val R = Royal.royalReturnInstance[List]
    M.royalBind(R.royalReturn(x))(f) == f(x)
  }

  val associativity = Property.forAll { (m: List[Int], f: Int => List[Int], g: Int => List[Int]) =>
    val M = Royal.royalMonadInstance[List]
    M.royalBind(m)((x: Int) => M.royalBind(f(x))(g)) == M.royalBind(M.royalBind(m)(f))(g)
  }
}
