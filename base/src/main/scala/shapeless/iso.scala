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

import language.experimental.macros

import scala.reflect.macros.{ Context, Macro }

trait Iso[T, U] {
  def to(t: T): U
  def from(u: U): T
}

/*
 * Credit to Eugene Burmako for both the experimental implicit macro type inference
 * support and the first cut at the implementation below.
 */
object Iso {
  object impl extends Macro {
    def apply[C: c.WeakTypeTag, L: c.WeakTypeTag](c: Context): c.Expr[Iso[C, L]] = {
      import c.universe._
      import definitions._
      import Flag._

      val sym = c.weakTypeOf[C].typeSymbol
      if (!sym.isClass || !sym.asClass.isCaseClass)
        c.abort(c.enclosingPosition, s"$sym is not a case class")

      val fields = sym.typeSignature.declarations.toList.collect {
        case x: TermSymbol if x.isVal && x.isCaseAccessor => x
      }

      val HNilTypeTree   = Select(Ident(TermName("shapeless")), TypeName("HNil"))
      val HNilValueTree  = Select(Ident(TermName("shapeless")), TermName("HNil"))

      val HConsTypeTree  = Select(Ident(TermName("shapeless")), TypeName("$colon$colon"))
      val HConsValueTree = Select(Ident(TermName("shapeless")), TermName("$colon$colon"))

      def mkHListType: Tree = {
        fields.map { f => TypeTree(f.typeSignature) }.foldRight(HNilTypeTree : Tree) {
          case (t, acc) => AppliedTypeTree(HConsTypeTree, List(t, acc))
        }
      }

      def mkHListValue: Tree = {
        fields.map(_.name.toString.trim).foldRight(HNilValueTree : Tree) {
          case (v, acc) => Apply(HConsValueTree, List(Select(Ident(TermName("t")), TermName(v)), acc))
        }
      }

      def mkNth(n: Int): Tree =
        Select(
          (0 until n).foldRight(Ident(TermName("u")) : Tree) {
            case (_, acc) => Select(acc, TermName("tail"))
          },
          TermName("head")
        )

      def mkCaseClassValue: Tree =
        Apply(
          Select(Ident(sym.companionSymbol), TermName("apply")),
          (0 until fields.length).map(mkNth(_)).toList
        )

      val isoClass =
        ClassDef(Modifiers(FINAL), TypeName("$anon"), List(),
          Template(
            List(AppliedTypeTree(Ident(TypeName("Iso")), List(Ident(sym), mkHListType))),
            emptyValDef,
            List(
              DefDef(
                Modifiers(), nme.CONSTRUCTOR, List(),
                List(List()),
                TypeTree(),
                Block(List(pendingSuperCall), Literal(Constant(())))),

              DefDef(
                Modifiers(), TermName("to"), List(),
                List(List(ValDef(Modifiers(PARAM), TermName("t"), Ident(sym), EmptyTree))),
                TypeTree(),
                mkHListValue),

              DefDef(
                Modifiers(), TermName("from"), List(),
                List(List(ValDef(Modifiers(PARAM), TermName("u"), mkHListType, EmptyTree))),
                TypeTree(),
                mkCaseClassValue)
            )
          )
        )

      c.Expr[Iso[C, L]](
        Block(
          List(isoClass),
          Apply(Select(New(Ident(TypeName("$anon"))), nme.CONSTRUCTOR), List())
        )
      )
    }

    override def inferExprInstance(c: Context)(
      tree: c.Tree,
      tparams: List[c.Symbol],
      pt: c.Type = c.universe.WildcardType,
      treeTp0: c.Type = null,
      keepNothings: Boolean = true,
      useWeaklyCompatible: Boolean = false): List[c.Symbol] = {

      import c.universe._
      import definitions._

      val TypeRef(_, _, List(caseClassTpe, _)) = pt // Iso[Test.Foo,?]
      val fields = caseClassTpe.typeSymbol.typeSignature.declarations.toList.collect {
        case x: TermSymbol if x.isVal && x.isCaseAccessor => x
      }

      val hnilType =  weakTypeOf[shapeless.HNil]
      val hconsType = weakTypeOf[shapeless.::[_, _]]

      val tequiv = fields.map(_.typeSignature).foldRight(hnilType) {
        case (t, acc) => appliedType(hconsType, List(t, acc))
      }

      c.substExpr(tree, tparams, List(caseClassTpe, tequiv), pt)
      Nil
    }
  }

  implicit def materializeIso[C, L]: Iso[C, L] = macro impl[C, L]
}
