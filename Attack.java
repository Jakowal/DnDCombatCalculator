import java.util.Random;

public class Attack {

  public int toHit;
  public String damage;
  public String name;

  Attack(int h, String d, String n) {
    toHit = h;
    damage = d;
    name = n;
  }

  public int[] rollAttack() {
    int[] atk = new int[2];

    atk[0] = random(1,20)+toHit;
    atk[1] = 0;
    String[] dmg = damage.split("/");
    int j = 0;

    for (String s : dmg) {
      String temp = "";
      int diceAmount = 0;
      int diceType = 0;
      int bonus = 0;
      boolean diceAmountDone = false;
      boolean diceTypeDone = false;

      for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == 'd') {
          diceAmount = Integer.parseInt(temp);
          diceAmountDone = true;
          i++; temp = "";
        }else if (s.charAt(i) == '+' || s.charAt(i) == '-') {
          diceType = Integer.parseInt(temp);
          diceTypeDone = true;
          temp = "";
          if (s.charAt(i) == '+') i++;
        }
        if (i == s.length()-1 && temp.length() > 0) bonus = Integer.parseInt(temp);
        temp += s.charAt(i);
      }
      int diceRoll = 0;

      for (int i = 0; i < diceAmount; i++) {
        diceRoll += random(1, diceType);
      }
      atk[1] += diceRoll + bonus;
    }
    return atk;
  }

  private static int random(int min,int max) {
      return min + (int)(Math.random() * ((max - min)+1));
  }

  @Override
  public String toString() {
    return name;
  }
}
