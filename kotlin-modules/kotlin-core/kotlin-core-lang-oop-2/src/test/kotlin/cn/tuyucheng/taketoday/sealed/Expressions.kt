package cn.tuyucheng.taketoday.sealed

sealed class Expr(val keyword: String)
class ForExpr : Expr("for")
class IfExpr : Expr("if")
class WhenExpr : Expr("when")
class DeclarationExpr : Expr("val")