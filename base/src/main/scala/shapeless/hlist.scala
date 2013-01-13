/*
 * Copyright (c) 2011 Miles Sabin 
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

import language.implicitConversions

/**
 * `HList` ADT base trait.
 * 
 * @author Miles Sabin
 */
sealed trait HList

/**
 * Non-empty `HList` element type.
 * 
 * @author Miles Sabin
 */
final case class ::[+H, +T <: HList](head : H, tail : T) extends HList {
  override def toString = head+" :: "+tail.toString
}

/**
 * Empty `HList` element type.
 * 
 * @author Miles Sabin
 */
trait HNil extends HList {
  def ::[H](h : H) = shapeless.::(h, this)
  override def toString = "HNil"
}

/**
 * Empty `HList` value.
 * 
 * @author Miles Sabin
 */
case object HNil extends HNil

/**
 * Carrier for `HList` operations.
 * 
 * These methods are implemented here and pimped onto the minimal `HList` types to avoid issues that would otherwise be
 * caused by the covariance of `::[H, T]`.
 * 
 * @author Miles Sabin
 */
final class HListOps[L <: HList](l : L) {
  /**
   * Returns the head of this `HList`. Available only if there is evidence that this `HList` is composite.
   */
  def head(implicit c : IsHCons[L]) : c.H = c.head(l) 

  /**
   * Returns that tail of this `HList`. Available only if there is evidence that this `HList` is composite.
   */
  def tail(implicit c : IsHCons[L]) : c.T = c.tail(l)

  /**
   * Prepend the argument element to this `HList`.
   */
  def ::[H](h : H) : H :: L = shapeless.::(h, l)
}

object HList {
  implicit def hlistOps[L <: HList](l : L) : HListOps[L] = new HListOps(l)
}

/**
 * Type class witnessing that this `HList` is composite and providing access to head and tail. 
 * 
 * @author Miles Sabin
 */
trait IsHCons[L <: HList] {
  type H
  type T <: HList
    
  def head(l : L) : H
  def tail(l : L) : T
}

object IsHCons {
  implicit def hlistIsHCons[H0, T0 <: HList] = new IsHCons[H0 :: T0] {
    type H = H0
    type T = T0
  
    def head(l : H0 :: T0) : H = l.head
    def tail(l : H0 :: T0) : T = l.tail
  }
}

