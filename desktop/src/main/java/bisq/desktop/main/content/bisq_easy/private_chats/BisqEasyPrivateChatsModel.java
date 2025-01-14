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

package bisq.desktop.main.content.bisq_easy.private_chats;

import bisq.chat.ChatChannelDomain;
import bisq.desktop.common.view.NavigationTarget;
import bisq.desktop.main.content.chat.ChatModel;
import bisq.user.profile.UserProfile;
import bisq.user.reputation.ReputationScore;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class BisqEasyPrivateChatsModel extends ChatModel {

    private final BooleanProperty noOpenChats = new SimpleBooleanProperty();
    private final StringProperty chatWindowTitle = new SimpleStringProperty();
    private final ObservableList<BisqEasyPrivateChatsView.ListItem> listItems = FXCollections.observableArrayList();
    private final FilteredList<BisqEasyPrivateChatsView.ListItem> filteredList = new FilteredList<>(listItems);
    private final SortedList<BisqEasyPrivateChatsView.ListItem> sortedList = new SortedList<>(filteredList);
    private final ObjectProperty<BisqEasyPrivateChatsView.ListItem> selectedItem = new SimpleObjectProperty<>();
    private final ObjectProperty<UserProfile> peersUserProfile = new SimpleObjectProperty<>();
    private final ObjectProperty<Stage> chatWindow = new SimpleObjectProperty<>();
    @Setter
    private ReputationScore peersReputationScore;

    public BisqEasyPrivateChatsModel(ChatChannelDomain chatChannelDomain) {
        super(chatChannelDomain);
    }

    @Override
    public NavigationTarget getDefaultNavigationTarget() {
        return NavigationTarget.NONE;
    }
}
