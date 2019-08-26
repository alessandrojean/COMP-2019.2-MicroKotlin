/**
 * Gera uma pirâmide de estrelas.
 */

fun main() {
  print("Digite n: ");
  var n: Int = readInt();

  if (n < 3) {
    printLn("n deve ser maior ou igual a 3");
  } else {
    var i: Int = 0;
    while (i < n) {
      var j: Int = 0;
      while (j <= i) {
        print("*");
        j = j + 1;
      }
      printLn("");
      i = i + 1;
    }
  }

}
