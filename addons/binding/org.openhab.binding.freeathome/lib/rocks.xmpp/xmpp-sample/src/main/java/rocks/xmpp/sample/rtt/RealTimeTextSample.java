/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
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

package rocks.xmpp.sample.rtt;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.extensions.rtt.InboundRealTimeMessage;
import rocks.xmpp.extensions.rtt.OutboundRealTimeMessage;
import rocks.xmpp.extensions.rtt.RealTimeTextManager;
import rocks.xmpp.extensions.rtt.model.RealTimeText;
import rocks.xmpp.im.chat.Chat;
import rocks.xmpp.im.chat.ChatManager;

/**
 * @author Christian Schudt
 */
public class RealTimeTextSample extends Application {

    private OutboundRealTimeMessage realTimeMessage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        SocketConnectionConfiguration socketConnectionConfiguration = SocketConnectionConfiguration.builder()
                .channelEncryption(ChannelEncryption.DISABLED)
                .hostname("localhost")
                .port(5222).build();

        XmppSessionConfiguration xmppSessionConfiguration = XmppSessionConfiguration.builder()
                .debugger(ConsoleDebugger.class)
                .build();


        final XmppClient xmppClient = XmppClient.create("localhost", xmppSessionConfiguration, socketConnectionConfiguration);

        // Enable XEP-0301 Real-time Text
        xmppClient.enableFeature(RealTimeText.NAMESPACE);

        xmppClient.connect();
        xmppClient.login("111", "111");

        final Jid contact = xmppClient.getDomain().withLocal("111");

        // Create a chat session with another user.
        final Chat chat = xmppClient.getManager(ChatManager.class).createChatSession(contact);
        RealTimeTextManager realTimeTextManager = xmppClient.getManager(RealTimeTextManager.class);
        // Create an new RTT message.
        realTimeMessage = realTimeTextManager.createRealTimeMessage(chat);

        TextArea textArea = new TextArea();
        textArea.textProperty().addListener((observable, oldValue, newValue) -> realTimeMessage.update(newValue));
        final Label label = new Label();

        // Upon receiving a RTT message, display it.
        realTimeTextManager.addRealTimeMessageListener(e -> {
            InboundRealTimeMessage rtt = e.getRealTimeMessage();
            rtt.addRealTimeTextChangeListener(e1 -> Platform.runLater(() -> label.setText(e1.getText())));
        });

        Button button = new Button("Send");
        button.setOnAction(event -> {
            realTimeMessage.commit();
            realTimeMessage = realTimeTextManager.createRealTimeMessage(chat);
            textArea.clear();
        });


        Scene scene = new Scene(new VBox(label, textArea, button));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
