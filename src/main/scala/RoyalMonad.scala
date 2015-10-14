package royal

import scalaz._
import Id._

trait RoyalReturn[M[_], R[_]] {
  def royalReturn[A](ra: R[A]): M[A]
}

trait RoyalMonad[M[_], N[_], P[_], R[_]] {
  implicit def M: RoyalReturn[M, R]
  implicit def N: RoyalReturn[N, R]
  implicit def P: RoyalReturn[P, R]
  def royalBind[A, B](ma: M[A])(f: R[A] => N[B]): P[B]
}

trait RelMonad[M[_], R[_]] extends RoyalReturn[M, R] {
  def relativeBind[A, B](ma: M[A])(f: R[A] => M[B]): M[B]
}

trait NonRoyalReturn[M[_]] {
  def rreturn[A](a: A): M[A]
}

trait PolyMonad[M[_], N[_], P[_]] {
  implicit def M: NonRoyalReturn[M]
  implicit def N: NonRoyalReturn[N]
  implicit def P: NonRoyalReturn[P]
  def polyBind[A, B](ma: M[A])(f: A => N[B]): P[B]
}

object Royal extends RoyalInstances {

  implicit def royalMonadInstance[M[_]](implicit F: Monad[M]): RoyalMonad[M, M, M, Id] = new RoyalMonad[M, M, M, Id] {
    implicit override val M = royalReturnInstance(nonRoyalReturnInstance(F))
    implicit override val N = M
    implicit override val P = M
    override def royalBind[A, B](m: M[A])(f: A => M[B]) = F.bind(m)(f)
  }
}

trait RoyalInstances {

  implicit def royalReturnInstance[M[_]](implicit N: NonRoyalReturn[M]): RoyalReturn[M, Id] = new RoyalReturn[M, Id] {
    override def royalReturn[A](x: A) = N.rreturn(x)
  }

  implicit def polyRoyalMonadInstance[M[_], N[_], P[_]](implicit PM: PolyMonad[M, N, P]): RoyalMonad[M, N, P, Id] = new RoyalMonad[M, N, P, Id] {
    implicit override val M = royalReturnInstance(PM.M)
    implicit override val N = royalReturnInstance(PM.N)
    implicit override val P = royalReturnInstance(PM.P)
    override def royalBind[A, B](m: M[A])(f: A => N[B]) = PM.polyBind(m)(f)
  }

  implicit def nonRoyalReturnInstance[M[_]](implicit M: Monad[M]): NonRoyalReturn[M] = new NonRoyalReturn[M] {
    override def rreturn[A](a: A) = M.point(a)
  }
}
