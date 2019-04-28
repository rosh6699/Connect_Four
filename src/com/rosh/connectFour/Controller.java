package com.rosh.connectFour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {


	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAA88";

	public static String PLAYER_ONE = "Player One";
	public static String PLAYER_Two = "Player Two";

	private boolean isPlayerOne = true;

	private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];


	private boolean isAllowed =true;

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertDiscsPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField pl1,pl2;

	@FXML
	public Button setBtn;




	public void createPlayground() {


		pl1.setFocusTraversable(false);
		pl2.setFocusTraversable(false);


		setBtn.setOnAction(event -> {

			PLAYER_ONE=pl1.getText();
			PLAYER_Two= pl2.getText();

					if(PLAYER_ONE.isEmpty() || PLAYER_Two.isEmpty()) {
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Enter all Details");
						alert.setContentText("Enter details for both Player One and Player Two");
						alert.show();
						PLAYER_ONE="PLAYER ONE";
						PLAYER_Two="PLAYER TWO";
						resetGame();
						pl1.clear();
						pl2.clear();



					}
				resetGame();


		});








		Shape rectangleWithHoles = createGameStructuralGrid();


		rootGridPane.add(rectangleWithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();

		for (Rectangle rectangle : rectangleList
		) {
			rootGridPane.add(rectangle, 0, 1);

		}

	}


	private Shape createGameStructuralGrid() {

		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);


		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {

				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);
				circle.setSmooth(true);

				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);

			}


		}


		rectangleWithHoles.setFill(Color.WHITE);

		return rectangleWithHoles;


	}


	private List<Rectangle> createClickableColumns() {

		List<Rectangle> rectangleList = new ArrayList<>();


		for (int col = 0; col < COLUMNS; col++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);


			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			final int column = col;
			rectangle.setOnMouseClicked(event -> {

				if(isAllowed) {
					isAllowed=false;
					insertDisc(new Disc(isPlayerOne), column);
				}
			});

			rectangleList.add(rectangle);

		}

		return rectangleList;

	}

	private void insertDisc(Disc disc, int column) {

		int row = ROWS - 1;

		while (row >= 0) {

			if (getDiscIfPresent(row, column) == null)
				break;
			row--;
		}


		if (row < 0) {
			return;
		}

		insertedDiscArray[row][column] = disc;


		insertDiscsPane.getChildren().add(disc);

		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + 20);

		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + 20);

		int currentRow = row;


		translateTransition.setOnFinished(event -> {
			isAllowed=true;

			if (gameEnded(currentRow, column)) {

				gameOver();


			}
			isPlayerOne = !isPlayerOne;

			playerNameLabel.setText(isPlayerOne ? PLAYER_ONE : PLAYER_Two);
		});

		translateTransition.play();

	}


	private boolean gameEnded(int row, int column) {

		List<Point2D> vertticalPoints = IntStream.rangeClosed(row - 3, row + 3).mapToObj(r -> new Point2D(r, column))
				.collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3).mapToObj(c -> new Point2D(row, c))
				.collect(Collectors.toList());


		Point2D startPoint1= new Point2D(row-3,column +3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6).mapToObj(i-> startPoint1.add(i,-i))
				.collect(Collectors.toList());

		Point2D startPoint2= new Point2D(row-3,column -3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6).mapToObj(i-> startPoint2.add(i,i))
				.collect(Collectors.toList());


		boolean isEnded = checkCombinations(vertticalPoints) || checkCombinations(horizontalPoints)
				||checkCombinations(diagonal1Points)|| checkCombinations(diagonal2Points);
		return isEnded;
	}


	private boolean checkCombinations(List<Point2D> points) {

		int chain = 0;
		for (Point2D point : points
		) {


			int r_ind = (int) point.getX();
			int c_ind = (int) point.getY();

			Disc disc = getDiscIfPresent(r_ind, c_ind);

			if (disc != null && disc.isPlayerOneMove == isPlayerOne) {
				chain++;

				if (chain == 4) {
					return true;
				}

			} else {
				chain = 0;


			}


		}
		return false;

	}

	private Disc getDiscIfPresent(int row, int column) {

		if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
			return null;
		else
			return insertedDiscArray[row][column];
	}

	private void gameOver() {

		String winner = isPlayerOne ? PLAYER_ONE : PLAYER_Two;
		System.out.println("Winner is " + winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setContentText("Want to play again ?");

		alert.setHeaderText("Winner is " + winner);

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No,Exit");



		alert.getButtonTypes().setAll(yesBtn,noBtn);

		Platform.runLater(() ->{


			Optional<ButtonType> btnCLicked= alert.showAndWait();

			if(btnCLicked.isPresent() && btnCLicked.get()==yesBtn){
				resetGame();
			}
			else{

				Platform.exit();
				System.exit(0);


			}


		});


	}
	public void resetGame() {


		insertDiscsPane.getChildren().clear();

		for(int row=0 ; row <insertedDiscArray.length;row++) {
			for(int col=0 ; col< insertedDiscArray[row].length;col++){

				insertedDiscArray[row][col]=null;
			}

		}

		isPlayerOne=true;

		playerNameLabel.setText(PLAYER_ONE);


		createPlayground();


	}


	private static class Disc extends Circle {

		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove) {

			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER / 2);
			setCenterX(CIRCLE_DIAMETER / 2);
			setCenterY(CIRCLE_DIAMETER / 2);

			setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
