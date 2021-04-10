package gui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class View extends java.util.Observable {

	private Control control;
	
	// GUI Elemente
	private BorderPane root = new BorderPane();
	private GridPane grid = new GridPane();
	private TextField text = new TextField();
	private Button[] digit;
	private Button[] operation;
	
	// Text Manipulation
	private Button clear = new Button("C");
	private Button backspace = new Button("DEL");
	private Button separator = new Button(",");
	private Button sign  = new Button("+/-");
	
	// Operationen
	private Button equation = new Button("=");
	private Button addition = new Button("+");
	private Button subtraction = new Button("-");
	private Button multiplication = new Button("×");
	private Button division  = new Button("÷");
	private Button modulus  = new Button("%");
	
	// Button Maße
	private final double WIDTH = 75.0;
	private final double HEIGHT = 50.0;
	
		
	public View(Control control, Stage stage) {
		this.control = control;
		this.addObserver(control);
		
		Scene scene = new Scene(root, 310, 370);
		scene.getStylesheets().addAll(this.getClass().getResource("/css/style.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Simple Calculator");
		stage.getIcons().add(new Image("/img/free-icon.png"));
		stage.show();
		
		initComponents();
		initListener();
		initKeyEventHandler();
	}
	
	private void initComponents() {
		Font font = Font.font("Calibri", FontWeight.LIGHT, 30);
		text.setFont(font);
		text.setPrefHeight(106);
		text.setEditable(false);
		text.setAlignment(Pos.CENTER_RIGHT);
		text.setStyle("-fx-background-color: #e6e6e6;");
		text.setText("0");
		
		initButtons();
		addButtons();
		
		grid.setVgap(2);
		grid.setHgap(2);
		root.setBottom(grid);
		root.setTop(text);
		root.setPadding(new Insets(2, 2, 2, 2));
		root.setStyle("-fx-background-color: #e6e6e6;");
	}
	
	private void initButtons() {
		// Ziffern
		digit = new Button[10];
		for (int i = 0; i < digit.length; i++) {
			digit[i] = new Button();
			digit[i].setText(Integer.toString(i));
			digit[i].setPrefSize(WIDTH, HEIGHT);
			digit[i].setId("number");
		}
		
		// Text Manipulation
		Button[] tmp = new Button[]{clear, backspace, separator, sign};
		for (int i = 0; i < tmp.length; i++) {
			tmp[i].setPrefSize(WIDTH, HEIGHT);
			tmp[i].setId("other");
		}
		
		// Operationen
		operation = new Button[]{equation, addition, subtraction, multiplication, division, modulus};
		for (int i = 0; i < operation.length; i++) {
			operation[i].setPrefSize(WIDTH, HEIGHT);
			operation[i].setId("other");
		}
		equation.setId("result");
	}

	// Grid werden die Buttons hinzugefügt.
	private void addButtons() {
		/* Ziffern
		 * (digit[7], 0, 1) ### (digit[8], 1, 1) ### (digit[9], 2, 1)
		 * (digit[4], 0, 2) ### (digit[5], 1, 2) ### (digit[6], 2, 2)
		 * (digit[1], 0, 3) ### (digit[2], 1, 3) ### (digit[3], 2, 3)
		 */
		grid.add(digit[0], 1, 4);
		for (int i = 1; i < digit.length; i++) {
			grid.add(digit[i], (i-1) % 3, (int) (4 - Math.ceil(i/3.0)));
		}
		
		// Text Manipulation
		grid.add(clear, 1, 0);
		grid.add(backspace, 2, 0);
		grid.add(separator, 2, 4);
		grid.add(sign, 0, 4);
		
		// Operationen
		for (int i = 0; i < operation.length-1; i++) {
			grid.add(operation[i], 3, 4-i);
		}
		grid.add(modulus, 0, 0);
	}
	
	private void initListener() {
		// Ziffern
		for (Button btn : digit) {
			btn.setOnAction(e -> control.handleDigit(btn.getText()));
		}
		
		// Text Manipulation
		clear.setOnAction(e -> control.handleClear());
		backspace.setOnAction(e -> control.handleBackspace());
		separator.setOnAction(e -> control.handleSeparator());
		sign.setOnAction(e -> control.handleSign());
		
		for (Button btn : operation) {
			btn.setOnAction(e -> control.handleOperation(btn.getText()));
		}
	}

	public void initKeyEventHandler() {
		EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// Ziffern
				String key = event.getCharacter();
				if (key.matches("[0-9]")) {
					control.handleDigit(key);
					return;
				}
				
				int ascii = key.charAt(0);
				switch (ascii) {
				/* Text Manipulation
				 * [Entf]: 127 -> clear
				 * [BACK]: 8 -> backspace
				 * [,][.]: 44,46 -> separator
				 */
				case 127:
					control.handleClear(); break;
				case 8:
					control.handleBackspace(); break;
				case 44: case 46:
					control.handleSeparator(); break;
					
				/* Operationen
				 * [=][ENTER]: 61,13 -> equation
				 * [+]: 43 -> addition
				 * [-]: 45 -> subtraction
				 * [*]: 42 -> multiplication
				 * [/]: 47 -> division
				 * [%]: 37 -> modulus
				 */
				case 61: case 13:
					control.handleOperation("="); break;
				case 43:
					control.handleOperation("+"); break;
				case 45:
					control.handleOperation("-"); break;
				case 42:
					control.handleOperation("×"); break;
				case 47:
					control.handleOperation("÷"); break;
				case 37:
					control.handleOperation("%"); break;
				}
			}
		};
		
		root.addEventHandler(KeyEvent.KEY_TYPED, handler);
		text.addEventHandler(KeyEvent.KEY_TYPED, handler);
	}
	
	public String getText() {
		return text.getText();
	}
	
	// Leert den Text.
	public void clear() {
		text.setText("0");
	}
	
	// Bei Änderung des Textes wird der Operand angepasst.
	public void setText(String text) {
		this.text.setText(text);
		this.setChanged();
		this.notifyObservers();
	}
	
	// Zeigt das Ergebnis mit dem nächsten Operator an.
	public void setText(double result, String operator) {
		String text = Double.toString(result).replace('.', ',');
		if (text.endsWith(",0")) text = text.substring(0, text.length()-2);
		if (!operator.equals("=")) text = text + operator;
		this.text.setText(text);
	}
}
