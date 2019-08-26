package br.edu.ufabc.microkotlin;

import java.util.HashMap;
import java.util.Map;
import br.edu.ufabc.microkotlin.Variable.VariableProperty;

import static br.edu.ufabc.microkotlin.Variable.VariableProperty.*;

/**
 * Tabela de símbolos do compilador.
 */
public class SymbolTable {

  /**
   * Escopo.
   */
  final SymbolTable enclosing;

  /**
   * Valores das variáveis e constantes.
   */
  private final Map<String, Variable> values = new HashMap<>();

  public SymbolTable() {
    this.enclosing = null;
  }

  public SymbolTable(SymbolTable enclosing) {
    this.enclosing = enclosing;
  }

  /**
   * Retorna a variável se ela existir.
   *
   * @param name nome da variável
   * @return variável se existe
   */
  public Variable get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    if (enclosing != null) return enclosing.get(name);

    MicroKotlin.error(name, "Undefined variable '" + name.lexeme + "'.");
    return null;
  }

  /**
   * Retorna se a variável existe.
   *
   * @param name nome da variável
   * @return verdadeiro se a variável existe
   */
  public boolean contains(String name) {
    if (values.containsKey(name)) return true;
    if (enclosing != null) return enclosing.contains(name);
    return false;
  }

  /**
   * Atribui um valor a uma variável.
   *
   * @param name nome da variável
   * @param value valor da atribuição
   */
  public void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      if (values.get(name.lexeme).property == VARIABLE) {
        values.get(name.lexeme).value = value;
        return;
      }

      MicroKotlin.error(name,
          "Cannot reassign a value to the constant '" + name.lexeme + "'.");
    }

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }

    MicroKotlin.error(name,
        "Undefined variable '" + name.lexeme + "'.");
  }

  public void define(Token name, VariableProperty property, Object value) {
    if (contains(name.lexeme)) {
      MicroKotlin.error(name,
          "A variable with the name '" + name.lexeme + "' already exists.");
      return;
    }

    values.put(name.lexeme, new Variable(property, value));
  }

}
