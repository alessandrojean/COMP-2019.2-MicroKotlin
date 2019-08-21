package br.edu.ufabc.microkotlin.stmt;

public abstract class Stmt {

  public static interface Visitor<R> {
    R visitBlockStmt(StmtBlock stmt);
    R visitDoWhileStmt(StmtDoWhile stmt);
    R visitExpressionStmt(StmtExpression stmt);
    R visitIfStmt(StmtIf stmt);
    R visitPrintStmt(StmtPrint stmt);
    R visitPrintLnStmt(StmtPrintLn stmt);
    R visitValStmt(StmtVal stmt);
    R visitVarStmt(StmtVar stmt);
    R visitWhileStmt(StmtWhile stmt);
  }

  public abstract <R> R accept(Visitor<R> visitor);

}
