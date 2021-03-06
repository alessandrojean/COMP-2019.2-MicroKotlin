package br.edu.ufabc.microkotlin.expr;

/**
 * Representa as expressões.
 *
 * expression → [identifier "="] expression | or
 */
public abstract class Expr {

  public static interface Visitor<R> {
    R visitAssignExpr(ExprAssign expr);
    R visitBinaryExpr(ExprBinary expr);
    R visitGroupingExpr(ExprGrouping expr);
    R visitLiteralExpr(ExprLiteral expr);
    R visitLogicalExpr(ExprLogical expr);
    R visitReadExpr(ExprRead expr);
    R visitUnaryExpr(ExprUnary expr);
    R visitVariableExpr(ExprVariable expr);
  }

  public abstract <R> R accept(Visitor<R> visitor);

}
