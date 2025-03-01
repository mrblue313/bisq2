/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.desktop.main.content.bisq_easy.open_trades;

import bisq.chat.ChatChannelDomain;
import bisq.desktop.common.view.NavigationTarget;
import bisq.desktop.main.content.chat.ChatModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class BisqEasyOpenTradesModel extends ChatModel {
    private final BooleanProperty noOpenTrades = new SimpleBooleanProperty();
    private final BooleanProperty tradeWelcomeVisible = new SimpleBooleanProperty();
    private final BooleanProperty tradeRulesAccepted = new SimpleBooleanProperty();
    private final BooleanProperty chatVisible = new SimpleBooleanProperty();
    private final BooleanProperty tradeStateVisible = new SimpleBooleanProperty();
    private final StringProperty chatWindowTitle = new SimpleStringProperty();
    private final ObjectProperty<Stage> chatWindow = new SimpleObjectProperty<>();
    private final ObjectProperty<BisqEasyOpenTradesView.ListItem> selectedItem = new SimpleObjectProperty<>();
    private final ObservableList<BisqEasyOpenTradesView.ListItem> listItems = FXCollections.observableArrayList();
    private final FilteredList<BisqEasyOpenTradesView.ListItem> filteredList = new FilteredList<>(listItems);
    private final SortedList<BisqEasyOpenTradesView.ListItem> sortedList = new SortedList<>(filteredList);

    public BisqEasyOpenTradesModel(ChatChannelDomain chatChannelDomain) {
        super(chatChannelDomain);
    }

    @Override
    public NavigationTarget getDefaultNavigationTarget() {
        return NavigationTarget.NONE;
    }


    void reset() {
        noOpenTrades.set(false);
        tradeWelcomeVisible.set(false);
        tradeRulesAccepted.set(false);
        chatVisible.set(false);
        tradeStateVisible.set(false);
        chatWindowTitle.set(null);
        chatWindow.set(null);
        selectedItem.set(null);
        listItems.clear();
    }
}
