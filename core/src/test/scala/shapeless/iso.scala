/*
 * Copyright (c) 2013 Miles Sabin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shapeless

import org.junit.Test
import org.junit.Assert._

class IsoTest {
  def typed[T](t: => T) {}

  def toHList[C, L](c: C)(implicit iso: Iso[C, L]): L = iso.to(c)
  
  class FromListAux[C] {
    def apply[L](l: L)(implicit iso: Iso[C, L]): C = iso.from(l)
  }
  def fromHList[C] = new FromListAux[C]

  // Note that the Iso for Foo can be summoned by toHList and 
  // fromHList without the need for any manual publication of
  // implicit Iso instances

  case class Foo(i: Int, s: String, b: Boolean)

  @Test
  def testTo {
    val l0 = toHList(Foo(23, "foo", true))
    println(l0)

    typed[Int :: String :: Boolean :: HNil](l0)
    assertEquals(23 :: "foo" :: true :: HNil, l0)

    val l1 = toHList(("foo", 42, 4.2))
    println(l1)

    typed[String :: Int :: Double :: HNil](l1)
    assertEquals("foo" :: 42 :: 4.2 :: HNil, l1)
  }

  @Test
  def testFrom {
    val c0 = fromHList[Foo](13 :: "bar" :: false :: HNil)
    println(c0)

    typed[Foo](c0)
    assertEquals(Foo(13, "bar", false), c0)

    val c1 = fromHList[(String, Int, Double)]("foo" :: 42 :: 4.2 :: HNil)
    println(c1)

    typed[(String, Int, Double)](c1)
    assertEquals(("foo", 42, 4.2), c1)
  }
}
