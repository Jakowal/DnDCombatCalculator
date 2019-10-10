import java.util.Stack;
import java.util.ArrayList;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.File;

import javafx.event.*;

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

import javafx.stage.Stage;

public class Main extends Application {

  static int monsterCount = 1;
  static Text header = new Text("Monster Tracker" + "\n" + "Monster Count: " + monsterCount);
  static Pane pane = new Pane();
  static Scene scene = new Scene(pane);

  FileReader reader;
  File premadeMonsters = new File("./PremadeMonsters.txt");

  static Stack<Button> addbuttons = new Stack<>();
  static Stack<TextField[]> fields = new Stack<>();
  static ArrayList<TextField> damageFields = new ArrayList<>();
  static ArrayList<Monster> monsters = new ArrayList<>();
  static ArrayList<Text> hpText = new ArrayList<>();
  static ArrayList<Button[]> deathSaves = new ArrayList<>();
  static ArrayList<Circle[]> deathSaveSigns = new ArrayList<>();

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    pane.setPrefSize(1000,600);

    header.setFont(new Font(20));
    header.setX(800);  header.setY(20);
    pane.getChildren().add(header);

    pane.getChildren().add(makeStopButton());
    pane.getChildren().add(makeFileButton());
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

    pane.getChildren().add(nameInfo); pane.getChildren().add(hpInfo); pane.getChildren().add(initiativeInfo);
    pane.getChildren().add(name); pane.getChildren().add(hp); pane.getChildren().add(initiative);
    pane.getChildren().add(makeAddButton());
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

  private static class Filehandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e) {
      Platform.exit();
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
      Main.pane.getChildren().remove(addbuttons.pop());
      Main.monsterCount ++;
      TextField[] t = fields.pop();
      removeMonsterBlock(t);
      String n = t[0].getCharacters().toString(); int h = Integer.parseInt(t[1].getCharacters().toString());
      if (t[2].getCharacters().length() > 0) {
        int i = Integer.parseInt(t[2].getCharacters().toString());
        addMonster(n, h, i);
      }else {
        addMonster(n, h);
      }
      addMonsterBlock();
      header.setText("Monster Tracker" + "\n" + "Monster Count: " + monsterCount);
    }
  }

  private static class Stophandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e) {
      Platform.exit();
    }
  }
}
