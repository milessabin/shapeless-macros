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
    val l = toHList(Foo(23, "foo", true))
    println(l)

    typed[Int :: String :: Boolean :: HNil](l)
    assertEquals(23 :: "foo" :: true :: HNil, l)
  }

  @Test
  def testFrom {
    val f = fromHList[Foo](13 :: "bar" :: false :: HNil)
    println(f)

    typed[Foo](f)
    assertEquals(Foo(13, "bar", false), f)
  }
}
