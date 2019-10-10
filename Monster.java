public class Monster {

  public int curhp;
  public boolean isAlive = true;
  public boolean isDead = false;
  public boolean isStable = false;
  String name;
  int maxhp;
  int initiative = 99;
  int deathSaveSuccesses = 0;
  int deathSaveFails = 0;

  Monster(String n, int h) {
    name = n;
    maxhp = h;
    curhp = maxhp;
  }

  public static Monster makeMonster(String n, int h, int i) {
    Monster m = new Monster(n, h);
    m.initiative = i;
    return m;
  }

  public static Monster makeMonster(String n, int h) {
    return new Monster(n, h);
  }

  public void deathSave(boolean result) {
    if (!isStable && !isAlive || !isDead) {
      if (result) {
        deathSaveSuccesses++;
      } else {
        deathSaveFails++;
      }
      if (deathSaveFails == 3) {
        isDead = true;
        deathSaveSuccesses = 0; deathSaveFails = 0;
      } else if (deathSaveSuccesses == 3) {
        isStable = true;
        deathSaveSuccesses = 0; deathSaveFails = 0;
      }
    }
  }

  public void damage(int damage) {
    if (!isDead && isAlive) {
      curhp = curhp - damage;
      if (curhp > maxhp) {curhp = maxhp;
      }else if (curhp <= 0 && isAlive) {curhp = 0; isAlive = false;
      }else if (curhp > 0 && !isAlive) {isAlive = true; isStable = false;
      }
    }else if(curhp == 0 && !isDead && damage < 0) {
      curhp = -damage;
      deathSaveFails = 0; deathSaveSuccesses = 0;
      isAlive = true; isStable = false;
    }
  }

  @Override
  public String toString() {
    return name + ", " + maxhp + ", " + curhp + ", " + initiative;
  }
}
