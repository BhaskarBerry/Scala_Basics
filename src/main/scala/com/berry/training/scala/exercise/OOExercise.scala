package com.berry.training.scala.exercise

/*
1. Generic trait MyPredicate[-T] with a little method test (t) ==> Boolean
2. Generic trait MyTransformer[-A, B] with a method transform(A) => B
3. MyList:
  -map(transformer) => MyList
  -filter (predicate) => MyList
  - flatMap(transformer from A tto MyList[B]) => MyList[B]

class EvenPredicate extends MyPredicate[T]
class stringToIntTransformer extends MyTransformer[String, Int]

 [1,2,3].map(n*2) = [2,4,6]
 [1,2,3,4].filter(n*2) = [2,4]
 [1,2,3].flatMap(n => [n , n+1]) => [1,2,2,3,3,4]
 */
abstract class MyList[+A] {
  def head: A
  def tail: MyList[A]
  def isEmpty: Boolean
  def add[B >: A](element: B): MyList[B]
  def printElements: String
  override def toString: String = "[" + printElements + "]"

  def map[B](transformer: MyTransformer[A, B]): MyList[B]
  def flatMap[B](transformer: MyTransformer[A, MyList[B]]): MyList[B]
  def filter(predicate: MyPredicate[A]): MyList[A]
//conctenation
  def ++[B >: A ](list: MyList[B]): MyList[B]
}

object Empty extends MyList[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyList[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](element: B): MyList[B] = new Cons(element, Empty)
  def printElements: String = ""

  def map[B](transformer: MyTransformer[Nothing, B]): MyList[B] = Empty
  def flatMap[B](transformer: MyTransformer[Nothing, MyList[B]]): MyList[B]= Empty
  def filter(predicate: MyPredicate[Nothing]): MyList[Nothing] = Empty
  def ++[B >: Nothing](list : MyList[B]) : MyList[B]= list
}

class Cons[+A](h: A, t: MyList[A]) extends MyList[A] {
  def head: A = h
  def tail: MyList[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](element: B): MyList[B] = new Cons(element, this)

  def printElements: String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements

  def filter(predicate: MyPredicate[A]): MyList[A] =
    if (predicate.test(h)) new Cons(h, t.filter(predicate))
    else t.filter(predicate)


  /*
  [1,2,3].map(n * 2)
    = new Cons(2, [2,3].map(n*2))
    = new Cons(2 , new Cons(4, [3].map(n * 2)))
   */
  def map[B](transformer: MyTransformer[A, B]): MyList[B] =
    new Cons(transformer.transform(h), t.map(transformer))

  def ++[B >: A ](list: MyList[B]): MyList[B] = new Cons(h, t ++ list)

  def flatMap[B](transformer: MyTransformer[A, MyList[B]]): MyList[B]=
    transformer.transform(h) ++ t.flatMap(transformer)
}

trait MyPredicate[-T] {
  def test(elem: T): Boolean
}

trait MyTransformer[-A, B] {
  def transform(elem: A): B
}

object OOExercise extends App {

  val listOfInt: MyList[Int] =
    new Cons(1, new Cons(2, new Cons(3, Empty)))

  val anotherlistOfInt: MyList[Int] =
    new Cons(4, new Cons(5, new Cons(6, Empty)))

  val listOfString: MyList[String] =
    new Cons("Hello", new Cons("Ayub", new Cons("Ram", Empty)))
  println(listOfInt.toString)
  println(listOfString.toString)

  println(listOfInt.map(new MyTransformer[Int, Int] {
    override def transform(elem: Int): Int = elem * 2
  }))

  println(listOfInt.filter(new MyPredicate[Int] {
    override def test(elem: Int): Boolean = elem % 2 ==0
  }))

  println((listOfInt ++ anotherlistOfInt).toString)
  println(listOfInt.flatMap(new MyTransformer[Int, MyList[Int]] {
    override def transform(elem: Int): MyList[Int] = new Cons(elem, new Cons(elem + 1 ,  Empty))
  }).toString)

}
