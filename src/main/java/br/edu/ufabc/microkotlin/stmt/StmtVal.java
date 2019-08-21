package br.edu.ufabc.microkotlin.stmt;

import br.edu.ufabc.microkotlin.Token;
import br.edu.ufabc.microkotlin.expr.Expr;

public class StmtVal extends Stmt {

  public final Token name;
  public final Token type;
  public final Expr initializer;

  public StmtVal(Token name, Token type, Expr initializer) {
    this.name = name;
    this.type = type;
    this.initializer = initializer;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitValStmt(this);
  }

}
