package org.scalajs.core.compiler.test

import org.scalajs.core.compiler.test.util._

import org.junit.Test
import org.junit.Ignore

// scalastyle:off line.size.limit

class JSOptionalTest extends DirectTest with TestHelpers {

  /* We add the compiler's output path itself to the classpath, for tests
   * involving separate compilation.
   */
  override def classpath: List[String] =
    super.classpath ++ List(testOutputPath)

  override def preamble: String = {
    """
    import scala.scalajs.js
    import scala.scalajs.js.annotation._
    """
  }

  @Test
  def optionalRequiresUndefinedRHS: Unit = {
    s"""
    trait A extends js.Object {
      val a1: js.UndefOr[Int] = 5
      val a2: Int = 5

      def b1: js.UndefOr[Int] = 5
      def b2: Int = 5

      var c1: js.UndefOr[Int] = 5
      var c2: Int = 5
    }
    """ hasErrors
    s"""
      |newSource1.scala:6: error: Members of Scala.js-defined JS traits must either be abstract, or their right-hand-side must be `js.undefined`.
      |      val a1: js.UndefOr[Int] = 5
      |                                ^
      |newSource1.scala:7: error: Members of Scala.js-defined JS traits must either be abstract, or their right-hand-side must be `js.undefined`.
      |      val a2: Int = 5
      |                    ^
      |newSource1.scala:9: error: Members of Scala.js-defined JS traits must either be abstract, or their right-hand-side must be `js.undefined`.
      |      def b1: js.UndefOr[Int] = 5
      |                                ^
      |newSource1.scala:10: error: Members of Scala.js-defined JS traits must either be abstract, or their right-hand-side must be `js.undefined`.
      |      def b2: Int = 5
      |                    ^
      |newSource1.scala:12: error: Members of Scala.js-defined JS traits must either be abstract, or their right-hand-side must be `js.undefined`.
      |      var c1: js.UndefOr[Int] = 5
      |                                ^
      |newSource1.scala:13: error: Members of Scala.js-defined JS traits must either be abstract, or their right-hand-side must be `js.undefined`.
      |      var c2: Int = 5
      |                    ^
    """
  }

  @Test
  def noOverrideConcreteNonOptionalWithOptional: Unit = {
    """
    abstract class A extends js.Object {
      val a1: js.UndefOr[Int] = 5
      val a2: js.UndefOr[Int]

      def b1: js.UndefOr[Int] = 5
      def b2: js.UndefOr[Int]
    }

    trait B extends A {
      override val a1: js.UndefOr[Int] = js.undefined
      override val a2: js.UndefOr[Int] = js.undefined

      override def b1: js.UndefOr[Int] = js.undefined
      override def b2: js.UndefOr[Int] = js.undefined
    }
    """ hasErrors
    """
      |newSource1.scala:14: error: Cannot override concrete val a1: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override val a1: js.UndefOr[Int] = js.undefined
      |                   ^
      |newSource1.scala:17: error: Cannot override concrete def b1: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override def b1: js.UndefOr[Int] = js.undefined
      |                   ^
    """

    """
    @js.native
    @JSGlobal
    class A extends js.Object {
      val a: js.UndefOr[Int] = js.native
      def b: js.UndefOr[Int] = js.native
    }

    trait B extends A {
      override val a: js.UndefOr[Int] = js.undefined
      override def b: js.UndefOr[Int] = js.undefined
    }
    """ hasErrors
    """
      |newSource1.scala:13: error: Cannot override concrete val a: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override val a: js.UndefOr[Int] = js.undefined
      |                   ^
      |newSource1.scala:14: error: Cannot override concrete def b: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override def b: js.UndefOr[Int] = js.undefined
      |                   ^
    """

    """
    @js.native
    trait A extends js.Object {
      val a: js.UndefOr[Int] = js.native
      def b: js.UndefOr[Int] = js.native
    }

    @js.native
    @JSGlobal
    class B extends A

    trait C extends B {
      override val a: js.UndefOr[Int] = js.undefined
      override def b: js.UndefOr[Int] = js.undefined
    }
    """ hasErrors
    """
      |newSource1.scala:16: error: Cannot override concrete val a: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override val a: js.UndefOr[Int] = js.undefined
      |                   ^
      |newSource1.scala:17: error: Cannot override concrete def b: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override def b: js.UndefOr[Int] = js.undefined
      |                   ^
    """
  }

  @Test
  def noOverrideConcreteNonOptionalWithOptionalSeparateCompilation1: Unit = {
    """
    abstract class A extends js.Object {
      val a1: js.UndefOr[Int] = 5
      val a2: js.UndefOr[Int]

      def b1: js.UndefOr[Int] = 5
      def b2: js.UndefOr[Int]
    }
    """.succeeds()

    """
    trait B extends A {
      override val a1: js.UndefOr[Int] = js.undefined
      override val a2: js.UndefOr[Int] = js.undefined

      override def b1: js.UndefOr[Int] = js.undefined
      override def b2: js.UndefOr[Int] = js.undefined
    }
    """ hasErrors
    """
      |newSource1.scala:6: error: Cannot override concrete val a1: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override val a1: js.UndefOr[Int] = js.undefined
      |                   ^
      |newSource1.scala:9: error: Cannot override concrete def b1: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override def b1: js.UndefOr[Int] = js.undefined
      |                   ^
    """
  }

  @Test
  def noOverrideConcreteNonOptionalWithOptionalSeparateCompilation2: Unit = {
    """
    @js.native
    @JSGlobal
    class A extends js.Object {
      val a: js.UndefOr[Int] = js.native
      def b: js.UndefOr[Int] = js.native
    }
    """.succeeds()

    """
    trait B extends A {
      override val a: js.UndefOr[Int] = js.undefined
      override def b: js.UndefOr[Int] = js.undefined
    }
    """ hasErrors
    """
      |newSource1.scala:6: error: Cannot override concrete val a: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override val a: js.UndefOr[Int] = js.undefined
      |                   ^
      |newSource1.scala:7: error: Cannot override concrete def b: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override def b: js.UndefOr[Int] = js.undefined
      |                   ^
    """
  }

  @Test
  def noOverrideConcreteNonOptionalWithOptionalSeparateCompilation3: Unit = {
    """
    @js.native
    trait A extends js.Object {
      val a: js.UndefOr[Int] = js.native
      def b: js.UndefOr[Int] = js.native
    }

    @js.native
    @JSGlobal
    class B extends A
    """.succeeds()

    """
    trait C extends B {
      override val a: js.UndefOr[Int] = js.undefined
      override def b: js.UndefOr[Int] = js.undefined
    }
    """ hasErrors
    """
      |newSource1.scala:6: error: Cannot override concrete val a: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override val a: js.UndefOr[Int] = js.undefined
      |                   ^
      |newSource1.scala:7: error: Cannot override concrete def b: scala.scalajs.js.UndefOr[Int] from A in a Scala.js-defined JS trait.
      |      override def b: js.UndefOr[Int] = js.undefined
      |                   ^
    """
  }

  @Test
  def noOptionalDefWithParens: Unit = {
    s"""
    trait A extends js.Object {
      def a(): js.UndefOr[Int] = js.undefined
      def b(x: Int): js.UndefOr[Int] = js.undefined
      def c_=(v: Int): js.UndefOr[Int] = js.undefined
    }
    """ hasErrors
    s"""
      |newSource1.scala:6: error: In Scala.js-defined JS traits, defs with parentheses must be abstract.
      |      def a(): js.UndefOr[Int] = js.undefined
      |                                    ^
      |newSource1.scala:7: error: In Scala.js-defined JS traits, defs with parentheses must be abstract.
      |      def b(x: Int): js.UndefOr[Int] = js.undefined
      |                                          ^
      |newSource1.scala:8: error: Raw JS setters must return Unit
      |      def c_=(v: Int): js.UndefOr[Int] = js.undefined
      |          ^
      |newSource1.scala:8: error: In Scala.js-defined JS traits, defs with parentheses must be abstract.
      |      def c_=(v: Int): js.UndefOr[Int] = js.undefined
      |                                            ^
    """
  }

}
