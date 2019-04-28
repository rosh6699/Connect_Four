package com.rosh.connectFour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {


	private Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane = loader.load();

		controller = loader.getController();
		controller.createPlayground();

		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());


		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene = new Scene(rootGridPane);


		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect Four");
		primaryStage.setResizable(false);
		primaryStage.show();
	}


	private MenuBar createMenu() {

		Menu fileMenu = new Menu("File");
		MenuItem newGame = new MenuItem("New Game");

		newGame.setOnAction(event -> {

			controller.pl1.clear();
			controller.pl2.clear();

			controller.PLAYER_ONE="PLAYER ONE";
			controller.PLAYER_Two="PLAYER TWO";
			resetGame();


		});

		MenuItem resetGame = new MenuItem("Reset Game");

		resetGame.setOnAction(event -> resetGame());
		MenuItem exitGame = new MenuItem("Exit Game");

		exitGame.setOnAction(event -> exitGame());
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

		fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

		Menu helpMenu = new Menu("Help");
		MenuItem aboutGame = new MenuItem("About Game");
		aboutGame.setOnAction(event -> aboutGame());
		MenuItem aboutMe = new MenuItem("About Me");
		aboutMe.setOnAction(event -> aboutMe());

		SeparatorMenuItem separator = new SeparatorMenuItem();

		helpMenu.getItems().addAll(aboutGame, separator, aboutMe);


		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);


		return menuBar;
	}

	private void aboutMe() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About The Developer");
		alert.setHeaderText("Roshan R");
		alert.getDialogPane().setContent(new Label("I'm a tech enthusiast.\n" +
				"Created the game \n" +
				"as part of learning javaFX.\n"));
		alert.show();

	}

	private void aboutGame() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About Connect Four");
		alert.setHeaderText("How To Play ?");


		alert.getDialogPane().setContent(new Label("Connect Four is a two-player connection game in which the\n" +
				" players first choose a color and then take turns dropping colored discs\n" +
				" from the top into a seven-column, six-row vertically suspended grid.\n" +
				" The pieces fall straight down, occupying the next available space\n" +
				" within the column. The objective of the game is to be the first to\n" +
				" form a horizontal, vertical, or diagonal line of four of one's own \n" +
				"discs. Connect Four is a solved game. The first player can always win\n" +
				" by playing the right moves.\n"));

		alert.show();

	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);
	}

	private void resetGame() {
		controller.resetGame();
	}


	public static void main(String[] args) {
		launch(args);
	}
}
