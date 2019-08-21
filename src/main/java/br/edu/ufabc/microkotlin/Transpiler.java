package br.edu.ufabc.microkotlin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.edu.ufabc.microkotlin.expr.*;
import br.edu.ufabc.microkotlin.program.*;
import br.edu.ufabc.microkotlin.stmt.*;

/**
 * Efetua a tradução do código em MicroKotlin para Java.
 */
public class Transpiler implements
    Expr.Visitor<String>, Stmt.Visitor<String>, Program.Visitor<String> {

  @SuppressWarnings("serial")
  private static class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token token, String message) {
      super(message);
      this.token = token;
    }
  }

  /**
   * Mapeamento dos tipos do MicroKotlin para os do Java.
   */
  private static final Map<String, String> TYPES;

  static {
    TYPES = new HashMap<>();
    TYPES.put("Int", "int");
    TYPES.put("Double", "double");
    TYPES.put("Boolean", "boolean");
    TYPES.put("String", "String");
  }

  /**
   * Arquivo de saída do tradutor.
   */
  private String outputFile;

  /**
   * Classe de saída.
   */
  private String outputClass;

  public Transpiler(String outputFile) {
    this.outputFile = outputFile;
    this.outputClass = generateClassName(outputFile);
  }

  private String generateClassName(String outputFile) {
    String pathSeparator = System.getProperty("file.separator");
    String className = outputFile.substring(0, outputFile.length() - 5);

    if (className.contains(pathSeparator)) {
      className = className.substring(className.lastIndexOf(pathSeparator) + 1);
    }

    return className;
  }

  public void transpile(Program program) throws IOException {
    PrintWriter writer = new PrintWriter(outputFile, "UTF-8");

    try {
      writer.print(translate(program));
      writer.close();
    } catch (RuntimeError error) {
      MicroKotlin.error(error.token, error.getMessage());
    }
  }

  private String translate(Program program) {
    return program.accept(this);
  }

  private String evaluate(Expr expr) {
    return expr.accept(this);
  }

  private String execute(Stmt stmt) {
    return stmt.accept(this);
  }

  @Override
  public String visitProgram(Program program) {
    StringBuilder builder = new StringBuilder();
    builder.append("import java.util.Scanner;\n\n");
    builder.append("public class " + outputClass + " {\n");

    for (StmtVal valDecl : program.constants) {
      builder.append("  " + execute(valDecl) + "\n");
    }

    builder.append("\n  public static void main(String[] args) {\n");
    builder.append("    Scanner scanner = new Scanner(System.in);\n");

    for (Stmt statement : program.statements) {
      String[] statementLines = execute(statement).split("\n");

      for (String line : statementLines) {
        builder.append("    " + line + "\n");
      }
    }

    builder.append("    scanner.close();\n");
    builder.append("  }\n\n");
    builder.append("}\n");
    return builder.toString();
  }

  private String executeBlock(List<Stmt> statements) {
    StringBuilder builder = new StringBuilder();

    for (Stmt statement : statements) {
      builder.append("  " + execute(statement) + "\n");
    }

    return builder.toString();
  }

  @Override
  public String visitBlockStmt(StmtBlock stmt) {
    return "{\n" + executeBlock(stmt.statements) + "}\n";
  }

  @Override
  public String visitDoWhileStmt(StmtDoWhile stmt) {
    StringBuilder builder = new StringBuilder();
    builder.append("do ");
    builder.append(execute(stmt.body));
    builder.append("while (" + evaluate(stmt.condition) + ");\n");
    return builder.toString();
  }

  @Override
  public String visitExpressionStmt(StmtExpression stmt) {
    return evaluate(stmt.expression) + ";";
  }

  @Override
  public String visitIfStmt(StmtIf stmt) {
    StringBuilder builder = new StringBuilder();
    builder.append("if (" + evaluate(stmt.condition) + ") ");
    builder.append(execute(stmt.thenBranch));

    if (stmt.elseBranch != null) {
      builder.append("else ");
      builder.append(execute(stmt.elseBranch));
    }

    builder.append("\n");
    return builder.toString();
  }

  @Override
  public String visitPrintStmt(StmtPrint stmt) {
    return "System.out.print(" + evaluate(stmt.expression) + ");";
  }

  @Override
  public String visitPrintLnStmt(StmtPrintLn stmt) {
    return "System.out.println(" + evaluate(stmt.expression) + ");";
  }

  @Override
  public String visitValStmt(StmtVal stmt) {
    String value = evaluate(stmt.initializer);
    String type = transformType(stmt.type);

    // TODO: Definir na tabela de símbolos.
    return "private static final " + type + " " +
        stmt.name.lexeme + " = " + value + ";";
  }

  @Override
  public String visitVarStmt(StmtVar stmt) {
    String type = transformType(stmt.type);
    String value = null;
    if (stmt.initializer != null) {
      value = evaluate(stmt.initializer);
    }

    // TODO: Definir na tabela de símbolos.
    return type + " " + stmt.name.lexeme +
      (value != null ? " = " + value : "") + ";";
  }

  @Override
  public String visitWhileStmt(StmtWhile stmt) {
    StringBuilder builder = new StringBuilder();
    builder.append("while (" + evaluate(stmt.condition) + ") ");
    builder.append(execute(stmt.body));
    builder.append("\n");
    return builder.toString();
  }

  @Override
  public String visitAssignExpr(ExprAssign expr) {
    String value = evaluate(expr.value);

    // TODO: Redefinir o valor na tabela de símbolos.
    return expr.name.lexeme + " = " + value;
  }

  private String transformType(Token type) {
    if (TYPES.containsKey(type.lexeme)) {
      return TYPES.get(type.lexeme);
    }

    throw new RuntimeError(type, "The type specified doesn't exist.");
  }

  @Override
  public String visitLiteralExpr(ExprLiteral expr) {
    return stringify(expr.value);
  }

  @Override
  @SuppressWarnings("incomplete-switch")
  public String visitUnaryExpr(ExprUnary expr) {
    String right = evaluate(expr.right);

    switch(expr.operator.type) {
      case BANG:
        return "!" + right;
      case MINUS:
        checkNumberOperand(expr.operator, right);
        return "-" + right;
    }

    // Não alcançável.
    return null;
  }

  @Override
  public String visitVariableExpr(ExprVariable expr) {
    // TODO: Efetuar o get na tabela de símbolos para dar erro caso não existir.
    return expr.name.lexeme;
  }

  private void checkNumberOperand(Token operator, String operand) {
    if (isNumber(operand)) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, String left, String right) {
    if (isNumber(left) && isNumber(right)) return;
    throw new RuntimeError(operator, "Operands must be numbers.");
  }

  private boolean isNumber(String object) {
    try {
      Double.parseDouble(object);
      return true;
    } catch (NumberFormatException e) {
      return isVariable(object);
    }
  }

  private boolean isString(String object) {
    return isVariable(object) || (object.startsWith("\"") && object.endsWith("\""));
  }

  private boolean isVariable(String object) {
    // TODO: Verificar na tabela de símbolos se a variável existe.
    return true;
  }

  private String stringify(Object object) {
    if (object == null) return "null";

    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    if (object instanceof String) {
      return "\"" + object.toString() + "\"";
    }

    return object.toString();
  }

  @Override
  public String visitGroupingExpr(ExprGrouping expr) {
    return "(" + evaluate(expr.expression) + ")";
  }

  @Override
  public String visitLogicalExpr(ExprLogical expr) {
    String left = evaluate(expr.left);
    String right = evaluate(expr.right);

    if (expr.operator.type == TokenType.OR) {
      return left + " || " + right;
    }

    return left + " && " + right;
  }

  @Override
  @SuppressWarnings("incomplete-switch")
  public String visitBinaryExpr(ExprBinary expr) {
    String left = evaluate(expr.left);
    String right = evaluate(expr.right);

    switch (expr.operator.type) {
      case GREATER:
        checkNumberOperands(expr.operator, left, right);
        return left + " > " + right;
      case GREATER_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return left + " >= " + right;
      case LESS:
        checkNumberOperands(expr.operator, left, right);
        return left + " < " + right;
      case LESS_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return left + " <= " + right;
      case BANG_EQUAL:
        return left + " != " + right;
      case EQUAL:
        return left + " == " + right;
      case MINUS:
        checkNumberOperands(expr.operator, left, right);
        return left + " - " + right;
      case PLUS:
        if (isString(left) || isString(right)) {
          return left + " + " + right;
        }

        if (isNumber(left) && isNumber(right)) {
          return left + " + " + right;
        }

        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
      case SLASH:
        checkNumberOperands(expr.operator, left, right);
        return left + " / " + right;
      case TIMES:
        checkNumberOperands(expr.operator, left, right);
        return left + " * " + right;
      case REM:
        checkNumberOperands(expr.operator, left, right);
        return left + " % " + right;
    }

    // Não alcançável.
    return null;
  }

  @Override
  public String visitReadExpr(ExprRead expr) {
    if ("String".equals(expr.type)) {
      return "scanner.nextLine()";
    }

    return "scanner.next" + expr.type + "()";
  }

}
