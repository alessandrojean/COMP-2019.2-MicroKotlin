import java.util.Scanner;

public class CalculoArea {
  private static final double PI = 3.14;

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    int raio = 0;
    boolean maisUm = true;
    System.out.println("+----------------------------+");
    System.out.println("| Cálculo de área do círculo |");
    System.out.println("+----------------------------+");
    System.out.println("");
    while (maisUm) {
      System.out.print("Digite o raio: ");
      raio = scanner.nextInt();
      System.out.println("A área é " + (PI * raio * raio));
      System.out.println("");
      System.out.print("Deseja continuar? (true/false) ");
      maisUm = scanner.nextBoolean();
    }
    scanner.close();
  }

}
