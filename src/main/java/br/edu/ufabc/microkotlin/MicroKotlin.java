package br.edu.ufabc.microkotlin;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import br.edu.ufabc.microkotlin.program.Program;

/**
 * Classe inicial do transpilador da linguagem MicroKotlin.
 * O arquivo de entrada é informado como um dos argumentos
 * de execução, e o de saída será gerado na mesma localização.
 *
 * A implementação em parte é baseada no livro Crafting Interpreters.
 */
public class MicroKotlin {

  /**
   * Determina o estado, informando se houve algum erro em
   * alguma das análises realizadas.
   */
  public static boolean hadError = false;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("You need to specify the input file.");
      return;
    }

    transpileFile(args[0]);
  }

  /**
   * Efetua a leitura do arquivo informado, obtendo todo seu
   * conteúdo em uma String que será avaliada posteriormente.
   *
   * @param file caminho do arquivo de entrada
   * @throws IOException caso haja algum erro de leitura
   */
  private static void transpileFile(String file) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(file));
    transpile(new String(bytes, Charset.defaultCharset()), file);

    if (hadError) {
      System.exit(1);
    }
  }

  /**
   * Efetua a tradução do código passado como parâmetro.
   *
   * @param sourceCode código-fonte na linguagem MicroKotlin
   */
  private static void transpile(String sourceCode, String inputFile) throws IOException {
    Scanner scanner = new Scanner(sourceCode);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    Program program = parser.parse();

    if (hadError) return;

    String outputFile = inputFile.replace(".kt", ".java");
    Transpiler transpiler = new Transpiler(outputFile);
    transpiler.transpile(program);
  }

  /**
   * Informa um erro na análise do código fonte, especificando
   * a linha e mostrando a mensagem informada.
   *
   * @param line linha onde o erro ocorreu
   * @param message mensagem do erro
   */
  public static void error(int line, String message) {
    report(line, "", message);
  }

  /**
   * Reporta um erro geral do transpilador.
   *
   * @param line linha onde o erro ocorreu
   * @param where onde o erro ocorreu
   * @param message mensagem de erro
   */
  public static void report(int line, String where, String message) {
    System.err.printf("(%d) Error %s: %s%n", line, where, message);
    hadError = true;
  }

  /**
   * Reporta um erro do Parser.
   *
   * @param token token com erro
   * @param message mensagem de erro
   */
  public static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, "at end", message);
    } else {
      report(token.line, "at '" + token.lexeme + "'", message);
    }
  }

}
