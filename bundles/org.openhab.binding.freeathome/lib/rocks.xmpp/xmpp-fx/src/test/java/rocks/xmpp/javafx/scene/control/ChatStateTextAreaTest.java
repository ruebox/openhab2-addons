/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rocks.xmpp.javafx.scene.control;

import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Christian Schudt
 */
public class ChatStateTextAreaTest extends Application {
    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Chat State TextArea");

        final Label label = new Label("Your current chat state is:");
        final Label lblState = new Label();
        lblState.setStyle("-fx-font-weight: bold;-fx-font-size:larger");

        final ChatStateTextArea chatStateTextArea = new ChatStateTextArea();
        lblState.textProperty().bind(new StringBinding() {
            {
                super.bind(chatStateTextArea.chatStateProperty());
            }

            @Override
            protected String computeValue() {
                return chatStateTextArea.chatStateProperty().get().toString();
            }
        });
        chatStateTextArea.chatStateProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));

        Button button = new Button("Send");
        button.setOnAction(event -> {
            chatStateTextArea.clear();
            chatStateTextArea.requestFocus();
        });
        HBox hBox = new HBox(5, label, lblState);
        hBox.setAlignment(Pos.CENTER_LEFT);
        VBox vBox = new VBox(10, hBox, chatStateTextArea, button);
        vBox.setPadding(new Insets(20, 20, 20, 20));
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
