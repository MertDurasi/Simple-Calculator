package gui;

import java.util.Observable;
import java.util.Observer;
import java.util.function.DoubleBinaryOperator;

import javafx.stage.Stage;
import util.Calc;

@SuppressWarnings("deprecation")
public class Control implements Observer {
	
	private View view;
	
	private double result = 0;	// erster Operand
	private Double operand = null;	// Repräsentation von Text und zweiter Operand
	private DoubleBinaryOperator operation = Calc::equation;	// nächste Operation

	public Control(Stage stage) {
		view = new View(this, stage);
	}
	
	// Die Operation wird berechnet.
	private void operate() {
		result = operation.applyAsDouble(result, operand);
		operand = null;
	}
	
	// Die nächste Operation wird gesetzt.
	private void setOperation(String operator) {
		switch (operator) {
		case "+":
			operation = Calc::addition; break;
		case "-":
			operation = Calc::subtraction; break;
		case "×":
			operation = Calc::multiplication; break;
		case "÷":
			operation = Calc::division; break;
		case "%":
			operation = Calc::modulus; break;

		default:
			operation = Calc::equation; break;
		}
	}
	
	public void handleOperation(String operator) {
		if (operand != null) operate();
		setOperation(operator);
		view.setText(result, operator);
	}
	 
	public void handleDigit(String digit) {
		if (operand == null || operand == 0) 
			view.setText(digit);
		else
			view.setText(view.getText() + digit);
	}
	 
	public void handleClear() {
		result = 0;
		operand = null;
		operation = Calc::equation;
		view.clear();
	}
	
	public void handleBackspace() {
		if (operand != null) {
			String text = view.getText();
			if(text.length() == 1)
				view.setText("0");
			else
				view.setText(text.substring(0, text.length()-1));
		}
	}
	
	public void handleSeparator() {
		if (operand == null || operand == 0)
			view.setText("0,");
		else
			view.setText(view.getText() + ",");
	}
	
	public void handleSign() {
		if (operand != null && operand != 0 || result != 0) {
			String text = view.getText();
			if (text.startsWith("-")) view.setText(text.substring(1));
			else view.setText("-" + text);
		}
	}
	
	// Der Operand wird aktualisiert.
	@Override
	public void update(Observable o, Object arg) {
		operand = Double.parseDouble(view.getText().replace(',', '.'));
	}
	
}
