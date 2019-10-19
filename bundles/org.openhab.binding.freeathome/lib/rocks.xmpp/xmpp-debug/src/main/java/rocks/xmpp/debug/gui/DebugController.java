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

package rocks.xmpp.debug.gui;

import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.Stanza;
import rocks.xmpp.extensions.sm.model.StreamManagement;

import javax.xml.bind.DataBindingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Christian Schudt
 */
public final class DebugController implements Initializable {

    private static final String CSS_INBOUND_STANZA = "inbound-stanza";

    private static final String CSS_OUTBOUND_STANZA = "outbound-stanza";

    private static final String CSS_ERROR_STANZA = "error-stanza";

    private static final String CSS_HIGHLIGHT_ROW = "highlight-row";

    private static final String CSS_PRESENCE = "presence";

    private static final String CSS_AVAILABLE = "available";

    private static final String CSS_UNAVAILABLE = "unavailable";

    private static final String CSS_AWAY = "away";

    private static final String CSS_DND = "dnd";

    final DebugViewModel viewModel;

    private final Transformer transformer;

    private final SAXParser parser;

    private FilteredList<StanzaEntry> filteredList;

    @FXML
    private Text lblStatus;

    @FXML
    private Text lblServer;

    @FXML
    private Text lblPort;

    @FXML
    private Circle circlePresence;

    @FXML
    private CheckBox cbIgnoreCase;

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox cbInbound;

    @FXML
    private CheckBox cbOutbound;

    @FXML
    private TextArea stanzaView;

    @FXML
    private TextArea txtOutbound;

    @FXML
    private TextArea txtInbound;

    @FXML
    private TableView<StanzaEntry> stanzaTableView;

    @FXML
    private TableColumn<StanzaEntry, Boolean> columnInbound;

    @FXML
    private TableColumn<StanzaEntry, LocalDateTime> columnDate;

    @FXML
    private TableColumn<StanzaEntry, String> columnStanza;

    @FXML
    private TableColumn<StanzaEntry, Jid> columnFrom;

    @FXML
    private TableColumn<StanzaEntry, Jid> columnTo;


    public DebugController() {

        viewModel = new DebugViewModel();

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);

        try {
            // http://stackoverflow.com/questions/21208325/how-do-i-change-the-default-logging-in-java-transformer
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parser = parserFactory.newSAXParser();
            parser.getXMLReader().setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });

            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setErrorListener(new ErrorListener() {
                @Override
                public void warning(TransformerException exception) throws TransformerException {
                    throw exception;
                }

                @Override
                public void error(TransformerException exception) throws TransformerException {
                    throw exception;
                }

                @Override
                public void fatalError(TransformerException exception) throws TransformerException {
                    throw exception;
                }
            });
        } catch (TransformerConfigurationException | ParserConfigurationException | SAXException e) {
            throw new DataBindingException(e);
        }
    }

    private static boolean containsIgnoreCase(String src, String input) {
        final int length = input.length();
        if (length == 0) {
            return true; // Empty string is contained
        }

        final char firstLo = Character.toLowerCase(input.charAt(0));
        final char firstUp = Character.toUpperCase(input.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, input, 0, length))
                return true;
        }

        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lblServer.textProperty().bind(viewModel.server);

        lblPort.textProperty().bind(new StringBinding() {
            {
                super.bind(viewModel.port);
            }

            @Override
            protected String computeValue() {
                return viewModel.port.getValue() != null ? Integer.toString(viewModel.port.get()) : "";
            }
        });

        lblStatus.textProperty().bind(new StringBinding() {
            {
                super.bind(viewModel.status);
            }

            @Override
            protected String computeValue() {
                return viewModel.status.getValue() != null ? viewModel.status.getValue().name().toLowerCase() : "";
            }
        });

        circlePresence.setRadius(10);
        circlePresence.getStyleClass().addAll(CSS_PRESENCE, CSS_UNAVAILABLE);
        viewModel.presence.addListener((observable, oldValue, newValue) -> {
            circlePresence.getStyleClass().removeAll(CSS_UNAVAILABLE, CSS_AVAILABLE, CSS_AWAY, CSS_DND);
            Presence presence = viewModel.presence.get();
            if (presence != null) {
                if (presence.isAvailable()) {
                    if (presence.getShow() != null) {
                        switch (presence.getShow()) {
                            case AWAY:
                            case XA:
                                circlePresence.getStyleClass().add(CSS_AWAY);
                                break;
                            case DND:
                                circlePresence.getStyleClass().add(CSS_DND);
                                break;
                            default:
                                circlePresence.getStyleClass().add(CSS_AVAILABLE);
                                break;
                        }
                    } else {
                        circlePresence.getStyleClass().add(CSS_AVAILABLE);
                    }
                } else {
                    circlePresence.getStyleClass().add(CSS_UNAVAILABLE);
                }
            } else {
                circlePresence.getStyleClass().add(CSS_UNAVAILABLE);
            }
        });

        filteredList = new FilteredList<>(viewModel.stanzas, this::isVisible);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filter());

        SortedList<StanzaEntry> sortedList = new SortedList<>(filteredList);
        stanzaTableView.setItems(sortedList);

        sortedList.comparatorProperty().bind(stanzaTableView.comparatorProperty());

        stanzaTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.highlightedItems.clear();
            stanzaView.getStyleClass().removeAll(CSS_INBOUND_STANZA, CSS_OUTBOUND_STANZA, CSS_ERROR_STANZA);
            if (newValue != null) {
                if (newValue.isInbound()) {
                    stanzaView.getStyleClass().add(CSS_INBOUND_STANZA);
                } else {
                    stanzaView.getStyleClass().add(CSS_OUTBOUND_STANZA);
                }

                try {
                    if (newValue.getStanza() != null) {
                        StreamResult result = new StreamResult(new StringWriter());
                        Source source = new SAXSource(parser.getXMLReader(), new InputSource(new StringReader(newValue.getXml())));
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        transformer.transform(source, result);
                        stanzaView.setText(result.getWriter().toString());
                    } else {
                        stanzaView.setText(newValue.getXml());
                    }
                } catch (TransformerException | SAXException e) {

                    try {
                        StreamResult result = new StreamResult(new StringWriter());
                        String streamEndTag = "</stream:stream>";
                        Source source = new SAXSource(parser.getXMLReader(), new InputSource(new StringReader(newValue.getXml() + streamEndTag)));
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                        transformer.transform(source, result);
                        String stream = result.getWriter().toString().trim();
                        if (stream.endsWith("/>")) {
                            stanzaView.setText(stream);
                        } else {
                            stanzaView.setText(stream.substring(0, stream.length() - 1 - streamEndTag.length()));
                        }
                    } catch (SAXException | TransformerException e1) {
                        stanzaView.setText(newValue.getXml());
                    }
                }
                List<StanzaEntry> outBoundRequests = new ArrayList<>();
                List<StanzaEntry> outBoundAnswers = new ArrayList<>();
                List<StanzaEntry> inBoundRequests = new ArrayList<>();
                List<StanzaEntry> inBoundAnswers = new ArrayList<>();
                int answerIndex = -1;
                int requestIndex = -1;
                // Add the highlighted items.
                for (StanzaEntry entry : stanzaTableView.getItems()) {
                    if (newValue.isInbound() != entry.isInbound()) {
                        if (newValue.getStanza() instanceof Stanza && entry.getStanza() instanceof Stanza) {
                            Stanza selectedStanza = (Stanza) newValue.getStanza();
                            Stanza otherStanza = (Stanza) entry.getStanza();
                            if (otherStanza.getId() != null && otherStanza.getId().equals(selectedStanza.getId())) {
                                if (selectedStanza instanceof IQ && otherStanza instanceof IQ) {
                                    IQ selectedIQ = (IQ) selectedStanza;
                                    IQ otherIQ = (IQ) otherStanza;
                                    if (((selectedIQ.isRequest() && otherIQ.isResponse())
                                            || (selectedIQ.isResponse() && otherIQ.isRequest()))) {
                                        // Add the highlighted items.
                                        viewModel.highlightedItems.add(entry);
                                    }
                                } else {
                                    viewModel.highlightedItems.add(entry);
                                }
                            }
                        }
                    }
                    if (entry.getStanza() instanceof StreamManagement.Answer) {
                        List<StanzaEntry> answers = entry.isInbound() ? inBoundAnswers : outBoundAnswers;
                        answers.add(entry);
                        if (newValue == entry) {
                            answerIndex = (newValue.isInbound() ? inBoundAnswers.size() : outBoundAnswers.size()) - 1;
                        }
                    }
                    if (entry.getStanza() == StreamManagement.REQUEST) {
                        List<StanzaEntry> requests = entry.isInbound() ? inBoundRequests : outBoundRequests;
                        requests.add(entry);
                        if (newValue == entry) {
                            requestIndex = (newValue.isInbound() ? inBoundRequests.size() : outBoundRequests.size()) - 1;
                        }
                    }
                }

                List<StanzaEntry> requests = newValue.isInbound() ? outBoundRequests : inBoundRequests;
                if (answerIndex > -1 && answerIndex < requests.size()) {
                    viewModel.highlightedItems.add(requests.get(answerIndex));
                }
                List<StanzaEntry> answers = newValue.isInbound() ? outBoundAnswers : inBoundAnswers;
                if (requestIndex > -1 && requestIndex < answers.size()) {
                    viewModel.highlightedItems.add(answers.get(requestIndex));
                }

                // Workaround to refresh table:
                // http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
                stanzaTableView.getColumns().get(0).setVisible(false);
                stanzaTableView.getColumns().get(0).setVisible(true);
            } else {
                stanzaView.setText(null);
            }
        });

        stanzaTableView.setRowFactory(new Callback<TableView<StanzaEntry>, TableRow<StanzaEntry>>() {
            @Override
            public TableRow<StanzaEntry> call(TableView<StanzaEntry> param) {
                return new TableRow<StanzaEntry>() {

                    @Override
                    protected void updateItem(StanzaEntry item, boolean empty) {
                        super.updateItem(item, empty);
                        getStyleClass().removeAll(CSS_INBOUND_STANZA, CSS_OUTBOUND_STANZA, CSS_ERROR_STANZA, CSS_HIGHLIGHT_ROW);
                        setContextMenu(null);
                        if (!empty) {
                            if (item.isError()) {
                                getStyleClass().add(CSS_ERROR_STANZA);
                            }
                            if (item.isInbound()) {
                                getStyleClass().add(CSS_INBOUND_STANZA);
                            } else {
                                getStyleClass().add(CSS_OUTBOUND_STANZA);
                            }
                            if (item.getStanza() instanceof IQ && ((IQ) item.getStanza()).isRequest()) {
                                setContextMenu(iqContextMenu);
                                iqContextMenu.update(item);
                            }
                            if (viewModel.highlightedItems.contains(item)) {
                                getStyleClass().add(CSS_HIGHLIGHT_ROW);
                            }
                        }
                    }

                    private final IQContextMenu iqContextMenu = new IQContextMenu(stanzaTableView);
                };
            }
        });

        // Do not use PropertyValueFactory for columns, because it requires the item class to be public

        columnInbound.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().isInbound()));
        columnInbound.setCellFactory(new Callback<TableColumn<StanzaEntry, Boolean>, TableCell<StanzaEntry, Boolean>>() {
            @Override
            public TableCell<StanzaEntry, Boolean> call(TableColumn<StanzaEntry, Boolean> booleanStanzaEntryTableColumn) {
                TableCell<StanzaEntry, Boolean> cell = new TableCell<StanzaEntry, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        setGraphic(null);

                        if (!empty && item != null) {
                            // Make a region, so that -fx-shape can be applied from CSS.
                            Region rectangle = new Region();
                            rectangle.setMaxWidth(Control.USE_PREF_SIZE);
                            rectangle.setMaxHeight(Control.USE_PREF_SIZE);
                            rectangle.setRotate(item ? 90 : 270);
                            rectangle.getStyleClass().add("arrow");
                            setGraphic(rectangle);
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        columnDate.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDate()));
        columnDate.setCellFactory(new Callback<TableColumn<StanzaEntry, LocalDateTime>, TableCell<StanzaEntry, LocalDateTime>>() {
            @Override
            public TableCell<StanzaEntry, LocalDateTime> call(TableColumn<StanzaEntry, LocalDateTime> dateStanzaEntryTableColumn) {
                TableCell<StanzaEntry, LocalDateTime> cell = new TableCell<StanzaEntry, LocalDateTime>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        setTooltip(null);

                        if (!empty) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
                            String formatted = formatter.format(item);
                            setText(formatted);
                            setTooltip(new Tooltip(formatted));
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        columnStanza.setMaxWidth(Double.MAX_VALUE);
        columnStanza.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getXml()));
        columnStanza.setCellFactory(new Callback<TableColumn<StanzaEntry, String>, TableCell<StanzaEntry, String>>() {
            @Override
            public TableCell<StanzaEntry, String> call(TableColumn<StanzaEntry, String> columnStanza) {
                TableCell<StanzaEntry, String> cell = new TableCell<StanzaEntry, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        setTooltip(null);
                        if (!empty) {
                            if (item.length() > 150) {
                                setText(item.substring(0, 150) + "...");
                            } else {
                                setText(item);
                            }
                        }
                    }
                };
                cell.getStyleClass().add("stanza-cell");
                return cell;
            }
        });

        columnFrom.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getFrom()));
        columnFrom.setCellFactory(new Callback<TableColumn<StanzaEntry, Jid>, TableCell<StanzaEntry, Jid>>() {
            @Override
            public TableCell<StanzaEntry, Jid> call(TableColumn<StanzaEntry, Jid> columnStanza) {
                return new TableCell<StanzaEntry, Jid>() {
                    @Override
                    protected void updateItem(Jid item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        if (!empty && item != null) {
                            setText(item.toString());
                        }
                    }
                };
            }
        });

        columnTo.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getTo()));
        columnTo.setCellFactory(new Callback<TableColumn<StanzaEntry, Jid>, TableCell<StanzaEntry, Jid>>() {
            @Override
            public TableCell<StanzaEntry, Jid> call(TableColumn<StanzaEntry, Jid> columnStanza) {
                return new TableCell<StanzaEntry, Jid>() {
                    @Override
                    protected void updateItem(Jid item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        if (!empty && item != null) {
                            setText(item.toString());
                        }
                    }
                };
            }
        });

        txtInbound.setText("");

        Platform.runLater(() -> stanzaTableView.scrollTo(Integer.MAX_VALUE));
    }

    @FXML
    public void filter() {
        filteredList.setPredicate(this::isVisible);
    }

    @FXML
    public void clear() {
        viewModel.stanzas.clear();
    }

    void addStanza(StanzaEntry stanzaEntry) {
        viewModel.stanzas.add(stanzaEntry);
        // Select the first item. This should actually happen automatically by TableView, but there's some displaying glitch.
        if (viewModel.stanzas.size() == 1) {
            stanzaTableView.getSelectionModel().select(0);
        }
        // Limit the size of stanzas to 200
        viewModel.stanzas.remove(0, viewModel.stanzas.size() - 200);
    }

    private boolean isVisible(StanzaEntry stanzaEntry) {
        return ((cbInbound.isSelected() && stanzaEntry.isInbound())
                || (cbOutbound.isSelected() && !stanzaEntry.isInbound()))
                && (searchField.getText() == null || searchField.getText().equals("")
                || (stanzaEntry.getXml().contains(searchField.getText()) && !cbIgnoreCase.isSelected())
                || (containsIgnoreCase(stanzaEntry.getXml(), searchField.getText()) && cbIgnoreCase.isSelected()));
    }


    void appendTextInbound(final String s) {
        txtInbound.appendText(s);
    }

    void appendTextOutbound(final String s) {
        txtOutbound.appendText(s);
    }

    public void clearOutbound() {
        txtOutbound.clear();
    }

    public void clearInbound() {
        txtInbound.clear();
    }

    public void copyToClipboard() {

        StringBuilder sb = new StringBuilder();

        for (StanzaEntry stanzaEntry : filteredList) {
            sb.append(stanzaEntry).append('\n');
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(sb.toString());
        clipboard.setContents(strSel, null);
    }
}

