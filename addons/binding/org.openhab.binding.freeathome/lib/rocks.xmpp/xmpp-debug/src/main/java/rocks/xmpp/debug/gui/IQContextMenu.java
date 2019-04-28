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

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import rocks.xmpp.core.stanza.model.IQ;

/**
 * @author Christian Schudt
 */
final class IQContextMenu extends ContextMenu {
    private StanzaEntry stanzaEntry;

    public IQContextMenu(final TableView<StanzaEntry> stanzaTableView) {
        MenuItem menuItem = new MenuItem("Go To Response");
        menuItem.setOnAction(event -> {
            for (StanzaEntry entry : stanzaTableView.getItems()) {
                if (entry.getStanza() instanceof IQ && ((IQ) entry.getStanza()).getId().equals(((IQ) stanzaEntry.getStanza()).getId()) && (((IQ) entry.getStanza()).getType() == IQ.Type.RESULT || ((IQ) entry.getStanza()).getType() == IQ.Type.ERROR)) {
                    stanzaTableView.getSelectionModel().select(entry);
                    stanzaTableView.scrollTo(entry);
                    break;
                }
            }
        });
        menuItem.setVisible(false);
        getItems().add(menuItem);
    }

    public void update(StanzaEntry stanzaEntry) {
        this.stanzaEntry = stanzaEntry;
    }
}
