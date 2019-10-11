import java.util.Stack;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.File;

import javafx.event.*;

import javafx.collections.transformation.SortedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.Scene;
import javafx.scene.layout.*;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import javafx.stage.Stage;
import javafx.stage.FileChooser;

public class Main extends Application {

  static int monsterCount = 1;
  static Text header = new Text("Monster Count: " + monsterCount);
  static Text attackHeader = new Text("Attacks");
  static Pane pane = new Pane();
  static Scene scene = new Scene(pane);
  static Stage mainStage;

  static Scanner scanner;
  static File premadeMonsters = new File("./PremadeMonsters.mon");

  static Stack<Button> addbuttons = new Stack<>();
  static Stack<TextField[]> fields = new Stack<>();
  static Stack<ComboBox<Monster>> dropDowns = new Stack<>();

  static ArrayList<TextField> damageFields = new ArrayList<>();
  static ArrayList<Monster> monsters = new ArrayList<>();
  static ArrayList<Monster> premadeMonsterList = new ArrayList<>();
  static ArrayList<Text> hpText = new ArrayList<>();
  static ArrayList<Button[]> deathSaves = new ArrayList<>();
  static ArrayList<Circle[]> deathSaveSigns = new ArrayList<>();
  static ArrayList<ComboBox<Attack>> attackDropDowns = new ArrayList<>();
  static ArrayList<Button> attackDropDownButtons = new ArrayList<>();

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    mainStage = stage;
    pane.setPrefSize(1000,600);

    header.setFont(new Font(20));
    header.setX(720);  header.setY(30);
    pane.getChildren().add(header);

    attackHeader.setFont(new Font(20));
    attackHeader.setX(550);  attackHeader.setY(30);
    pane.getChildren().add(attackHeader);

    pane.getChildren().add(makeStopButton());
    pane.getChildren().add(makeFileButton());
    loadPremadeMonsters();
    addMonsterBlock();

    stage.setTitle("Jawa's Combat Tracker");
    stage.setScene(scene);
    stage.show();
  }

  private static void addMonster(String n, int h, int i) {
    monsters.add(Monster.makeMonster(n, h, i));
  }

  private static void addMonster(String n, int h) {
    monsters.add(Monster.makeMonster(n, h));
  }

  private static void addMonsterBlock() {
    Text nameInfo = new Text("Monster name:");
    nameInfo.setFont(new Font(15));
    nameInfo.setLayoutX(20); nameInfo.setLayoutY(65 + 70*(monsterCount-1));
    TextField name = new TextField("Monster" + monsterCount);
    name.setLayoutX(20); name.setLayoutY(70 + 70*(monsterCount-1));

    Text hpInfo = new Text("HP:");
    hpInfo.setFont(new Font(15));
    hpInfo.setLayoutX(180); hpInfo.setLayoutY(65 + 70*(monsterCount-1));
    TextField hp = new TextField();
    hp.setOnAction(new Addhandler());
    hp.setLayoutX(180); hp.setLayoutY(70 + 70*(monsterCount-1));

    Text initiativeInfo = new Text("Initiative (Optional):");
    initiativeInfo.setFont(new Font(15));
    initiativeInfo.setLayoutX(340); initiativeInfo.setLayoutY(65 + 70*(monsterCount-1));
    TextField initiative = new TextField();
    initiative.setOnAction(new Addhandler());
    initiative.setLayoutX(340); initiative.setLayoutY(70 + 70*(monsterCount-1));

    fields.push(new TextField[]{name, hp, initiative});
    dropDowns.push(makePremadeMonsterDropdown());

    pane.getChildren().add(nameInfo); pane.getChildren().add(hpInfo); pane.getChildren().add(initiativeInfo);
    pane.getChildren().add(name); pane.getChildren().add(hp); pane.getChildren().add(initiative);
    pane.getChildren().add(makeAddButton()); pane.getChildren().add(dropDowns.peek());
  }

  @SuppressWarnings("unchecked")
  private static ComboBox<Monster> makePremadeMonsterDropdown() {
    ComboBox<Monster> monsterDrop = new ComboBox();
    ObservableList list = FXCollections.observableList(premadeMonsterList);
    monsterDrop.setItems(list);
    monsterDrop.setLayoutX(720); monsterDrop.setLayoutY(70 + 70*(monsterCount-1));
    monsterDrop.setOnAction(new Premadehandler());
    return monsterDrop;
  }

  @SuppressWarnings("unchecked")
  private static ComboBox<Attack> makePremadeMonsterAttackDropdown(Monster m) {
    ComboBox<Attack> attackDrop = new ComboBox();
    ObservableList list = FXCollections.observableList(m.attacks);
    attackDrop.setItems(list);
    attackDrop.setLayoutX(550); attackDrop.setLayoutY(70 + 70*(monsterCount-2));
    attackDrop.setOnAction(new Attackhandler());
    return attackDrop;
  }

  private static void removeMonsterBlock(TextField[] t) {
    for (int i = 0; i < 3; i++) {
      pane.getChildren().remove(t[i]);
      Text text = new Text(t[i].getCharacters().toString());
      text.setFont(new Font(12));
      text.setLayoutX(t[i].getLayoutX()); text.setLayoutY(t[i].getLayoutY()+15);
      pane.getChildren().add(text);
      if (i == 1) hpText.add(text);
    }
    TextField field = new TextField();
    field.setLayoutX(t[1].getLayoutX()+40); field.setLayoutY(t[1].getLayoutY());
    field.setOnAction(new Damagehandler());
    field.setPrefWidth(60);
    damageFields.add(field);
    pane.getChildren().add(field);
    deathSaves.add(makeDeathSaveButtons());
    deathSaveSigns.add(makeDeathSaveSigns());
  }

  private static void removeMonsterBlock(TextField[] t, Monster m) {
    for (int i = 0; i < 3; i++) {
      pane.getChildren().remove(t[i]);
      Text text = new Text();
      if (i == 0) {text = new Text(m.name);}
      else if (i == 1) {text = new Text(""+m.curhp);}
      else if (m.initiative != 99) {text = new Text(""+m.initiative);}
      text.setFont(new Font(12));
      text.setLayoutX(t[i].getLayoutX()); text.setLayoutY(t[i].getLayoutY()+15);
      pane.getChildren().add(text);
      if (i == 1) hpText.add(text);
    }
    TextField field = new TextField();
    field.setLayoutX(t[1].getLayoutX()+40); field.setLayoutY(t[1].getLayoutY());
    field.setOnAction(new Damagehandler());
    field.setPrefWidth(60);
    damageFields.add(field);
    pane.getChildren().add(field);
    deathSaves.add(makeDeathSaveButtons());
    deathSaveSigns.add(makeDeathSaveSigns());
  }

  private static Circle[] makeDeathSaveSigns() {
    Circle[] points = new Circle[6];
    for (int i = 0; i < 6; i++) {
      Circle p = null;
      if (i > 2) {
        p = new Circle((204.0 + (i*8)), (65.0+70*(Main.monsterCount-2)), 4, Color.GREEN);
      }else {
        p = new Circle((204.0 + (i*8)), (65.0+70*(Main.monsterCount-2)), 4, Color.RED);
      }
      points[i] = p;
    }
    return points;
  }

  private static void loadPremadeMonsters() {
    FileChooser chooser = new FileChooser();
    try {scanner = new Scanner(premadeMonsters);
    } catch (FileNotFoundException ex){}
    String line = scanner.nextLine();
    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      String[] l = line.split(",");
      String name = l[0]; int hp = Integer.parseInt(l[1]);
      int attacks = Integer.parseInt(l[2]);
      int[] toHit = new int[attacks];
      String[] damage = new String[attacks];
      String[] atkname = new String[attacks];
      int j = 0;
      for (int i = 3; i < ((attacks*3)+3); i+=3) {
        atkname[j] = l[i]; toHit[j] = Integer.parseInt(l[i+1]); damage[j] = l[i+2];
        j++;
      }
      premadeMonsterList.add(Monster.makeMonster(name, hp));
      for (int i = 0; i < attacks; i++) {
        Monster.addAttack(premadeMonsterList.get(premadeMonsterList.size()-1),toHit[i], damage[i], atkname[i]);
      }
    }
  }

  private static Button[] makeDeathSaveButtons() {
    Button failButton = new Button("Bad ");
    failButton.setLayoutX(180); failButton.setLayoutY(70 + 70*(Main.monsterCount-2));
    failButton.setOnAction(new FailHandler());

    Button successButton = new Button("Good");
    successButton.setLayoutX(230); successButton.setLayoutY(70 + 70*(Main.monsterCount-2));
    successButton.setOnAction(new SuccessHandler());


    Button[] buttons = new Button[]{failButton, successButton};
    return buttons;
  }

  private static Button makeFileButton() {
    Button filebutton = new Button("File");
    filebutton.setLayoutX(910); filebutton.setLayoutY(10);
    filebutton.setOnAction(new Filehandler());
    return filebutton;
  }

  private static Button makeAddButton() {
    Button addbutton = new Button("Add");
    addbutton.setLayoutX(950); addbutton.setLayoutY(70 + 70*(monsterCount-1));
    addbutton.setOnAction(new Addhandler());
    addbuttons.push(addbutton);
    return addbutton;
  }

  private static Button makeStopButton() {
    Button stopbutton = new Button("Stop");
    stopbutton.setLayoutX(950); stopbutton.setLayoutY(10);
    stopbutton.setOnAction(new Stophandler());
    return stopbutton;
  }

  private static Button makePremadeMonsterButton() {
    Button attackButton = new Button("Attack");
    attackButton.setLayoutX(490); attackButton.setLayoutY(70+70*(monsterCount-2));
    attackButton.setOnAction(new Attackbuttonhandler());
    attackDropDownButtons.add(attackButton);
    return attackButton;
  }

  private static class Attackbuttonhandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent e) {
      int i = 0;
      for (i = 0; i < Main.attackDropDownButtons.size(); i++) {
        if (e.getSource() == attackDropDownButtons.get(i)) break;
      }
      attackDropDowns.get(i).fireEvent(new ActionEvent());
    }
  }

  @SuppressWarnings("unchecked")
  private static class Attackhandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e) {
      ComboBox<Attack> box = (ComboBox<Attack>)e.getSource();
      Attack a = box.getValue();
      int[] hit = a.rollAttack();
      Main.attackHeader.setText("To Hit: " + hit[0] + "\nDamage: " + hit[1]);
    }
  }

  private static class Premadehandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent e) {
      Main.monsterCount++;
      Main.pane.getChildren().remove(addbuttons.pop());
      ComboBox<Monster> box = Main.dropDowns.pop();
      Monster m = box.getValue();
      monsters.add(m);
      attackDropDowns.add(makePremadeMonsterAttackDropdown(m));
      Main.pane.getChildren().add(attackDropDowns.get(attackDropDowns.size()-1));
      Main.pane.getChildren().add(makePremadeMonsterButton());
      Main.pane.getChildren().remove(box);
      removeMonsterBlock(fields.pop(), m);
      addMonsterBlock();
      header.setText("Monster Count: " + monsterCount);
    }
  }

  private static class Filehandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e) {
      FileChooser chooser = new FileChooser();
      String line;
      try {
        scanner = new Scanner(chooser.showOpenDialog(mainStage));
      } catch (FileNotFoundException ex){System.out.println("Filehandler error: " + ex);}
      line = scanner.nextLine();
      while (scanner.hasNextLine()) {
        if (line.charAt(0) == '%') line = scanner.nextLine();
        String[] l = line.split(",");
        String name = l[0]; int hp = Integer.parseInt(l[1]);
        int attacks = Integer.parseInt(l[2]);
        int[] toHit = new int[attacks];
        String[] damage = new String[attacks];
        String[] atkname = new String[attacks];
        int j = 0;
        for (int i = 3; i < ((attacks*3)+3); i+=3) {
          atkname[j] = l[i]; toHit[j] = Integer.parseInt(l[i+1]); damage[j] = l[i+2];
          j++;
        }
        monsters.add(Monster.makeMonster(name, hp));
        for (int i = 0; i < attacks; i++) {
          Monster.addAttack(monsters.get(monsters.size()-1),toHit[i], damage[i], atkname[i]);
        }
        Main.monsterCount ++;
        Main.pane.getChildren().remove(addbuttons.pop());
        removeMonsterBlock(fields.pop(), monsters.get(monsters.size()-1));
        addMonsterBlock();
      }
    }
  }

  private static class FailHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e) {
      Monster m = null;
      int i;
      for (i = 0; i < Main.deathSaves.size(); i++) {
        if (Main.deathSaves.get(i)[0] == e.getSource()) {
          m = Main.monsters.get(i); break;
        }
      }
      m.deathSave(false);
      if (m.deathSaveFails > 0) {
        Main.pane.getChildren().add(Main.deathSaveSigns.get(i)[m.deathSaveFails-1]);
      }
      if (m.isDead) {
        Main.pane.getChildren().remove(deathSaves.get(i)[0]);
        Main.pane.getChildren().remove(deathSaves.get(i)[1]);
        for (int j = 0; j < 6; j++) {
          Main.pane.getChildren().remove(Main.deathSaveSigns.get(i)[j]);
        }
        Text dead = new Text("DEAD"); dead.setFont(new Font(15));
        dead.setLayoutX(deathSaves.get(i)[0].getLayoutX()); dead.setLayoutY(deathSaves.get(i)[0].getLayoutY() + 15);
        Main.pane.getChildren().add(dead);
      }
    }
  }

  private static class SuccessHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e) {
      Monster m = null;
      int i;
      for (i = 0; i < Main.deathSaves.size(); i++) {
        if (Main.deathSaves.get(i)[1] == e.getSource()) {
          m = Main.monsters.get(i); break;
        }
      }
      m.deathSave(true);
      if (m.deathSaveSuccesses > 0) {
        Main.pane.getChildren().add(Main.deathSaveSigns.get(i)[m.deathSaveSuccesses+2]);
      }
      if (m.isStable) {
        Main.pane.getChildren().remove(deathSaves.get(i)[0]);
        Main.pane.getChildren().remove(deathSaves.get(i)[1]);
        for (int j = 0; j < 6; j++) {
          Main.pane.getChildren().remove(Main.deathSaveSigns.get(i)[j]);
        }
        Main.pane.getChildren().add(hpText.get(i));
        Main.pane.getChildren().add(damageFields.get(i));
      }
    }
  }

  private static class Damagehandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e) {
      int i;
      Monster m = null;
      for (i = 0; i < damageFields.size(); i++) {
        if (damageFields.get(i)==e.getSource()) {
          m = monsters.get(i);
          break;
        }
      }
      int damage = Integer.parseInt(damageFields.get(i).getCharacters().toString());
      m.damage(damage);
      hpText.get(i).setText(""+m.curhp);
      if (!m.isAlive && !m.isStable && !m.isDead) {
        Main.pane.getChildren().remove(damageFields.get(i));
        Main.pane.getChildren().remove(hpText.get(i));
        Main.pane.getChildren().add(deathSaves.get(i)[0]);
        Main.pane.getChildren().add(deathSaves.get(i)[1]);
      }
    }
  }

  private static class Addhandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e) {
      TextField[] t = fields.peek();
      int h;
      try {
        h = Integer.parseInt(t[1].getCharacters().toString());
      } catch(NumberFormatException ex) {return;}
      fields.pop();
      String n = t[0].getCharacters().toString();
      Main.pane.getChildren().remove(addbuttons.pop());
      Main.monsterCount ++;
      removeMonsterBlock(t);
      if (t[2].getCharacters().length() > 0) {
        int i = Integer.parseInt(t[2].getCharacters().toString());
        addMonster(n, h, i);
      }else {
        addMonster(n, h);
      }
      addMonsterBlock();
      header.setText("Monster Count: " + monsterCount);
    }
  }

  private static class Stophandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e) {
      Platform.exit();
    }
  }
}
