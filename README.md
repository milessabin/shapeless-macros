shapeless-macros
================

This project is a proof of concept of the application of implicit macros (not
yet a part of Scala, but hopefully they will be in Scala 2.11) to [shapeless](https://github.com/milessabin/shapeless).

Currently this comprises an implicit macro which automagically creates
instances of shapeless `Iso`s for arbitrary case classes. This completely
eliminates the residual boilerplate currently required in [several](https://github.com/milessabin/shapeless/blob/master/examples/src/main/scala/shapeless/examples/lenses.scala#L35) of [shapeless's](https://github.com/milessabin/shapeless/blob/master/examples/src/main/scala/shapeless/examples/monoids.scala#L36) [key](https://github.com/milessabin/shapeless/blob/master/examples/src/main/scala/shapeless/examples/zipper.scala#L48)
[applications](https://github.com/jdegoes/blueeyes/blob/master/json/src/test/scala/blueeyes/json/serialization/IsoSerializationSpec.scala#L14).

Between now and the [2013 North East Scala Symposium](http://nescala.org/) I'll add implicit
macros supporting shapeless's type-level natural numbers and sized collections,
which I'll be [talking about in Philly](http://nescala.org/2013/talks#27) if I get
enough votes!

And that's about it: my current plan is to fold everything of value here into
shapeless proper as soon as implicit macros are available in [Macro Paradise](http://docs.scala-lang.org/overviews/macros/paradise.html) and
shapeless builds cleanly with Scala 2.11.0-SNAPSHOT.

Building
--------

Implicit macros are supported by the [`topic/implicit-macros`](https://github.com/scalamacros/kepler/tree/topic/implicit-macros) branch of [Eugene
Burmako's](https://twitter.com/xeno_by) `scalamacros/kepler` github repository. There isn't a binary
distribution of this branch, so you'll have to clone it and build it locally
(for best results, build with a 1.6 JDK ... `ant all.clean ; ant` should give
you a usable build in the `build/pack` subdirectory).

Once you've done that, clone this project and either set environment variable
`KEPLER_HOME` to point to the Scala build you've completed, or edit [project/Build.scala](https://github.com/milessabin/shapeless-macros/blob/master/project/Build.scala#L22) correspondingly.
Then run `sbt` as normal (I recommend Paul Phillips [`sbt` launcher script](https://github.com/paulp/sbt-extras)): `compile` and then `test` to run the example.

Discussion
----------

Please use the [shapless mailing list](https://groups.google.com/group/shapeless-dev)
for discussion of this project and applications of implicit macros in shapeless
(or elsewhere).

Acknowledgements
----------------

Thanks to [Eugene Burmako](https://twitter.com/xeno_by) for supporting implicit macros and being so generally
helpful. And thanks to [Alois Cochard](https://twitter.com/aloiscochard) for kicking the tires and sending pull
requests so quickly.
