package cn.tuyucheng.taketoday.kotlin

interface BookService {
   fun inStock(bookId: Int): Boolean
   fun lend(bookId: Int, memberId: Int)
}