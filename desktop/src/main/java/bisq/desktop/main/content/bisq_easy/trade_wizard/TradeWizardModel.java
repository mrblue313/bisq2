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

package bisq.desktop.main.content.bisq_easy.trade_wizard;

import bisq.desktop.common.view.NavigationModel;
import bisq.desktop.common.view.NavigationTarget;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class TradeWizardModel extends NavigationModel {
    @Setter
    private boolean isCreateOfferMode;
    private final IntegerProperty currentIndex = new SimpleIntegerProperty();
    private final StringProperty nextButtonText = new SimpleStringProperty();
    private final StringProperty backButtonText = new SimpleStringProperty();
    private final BooleanProperty closeButtonVisible = new SimpleBooleanProperty();
    private final BooleanProperty nextButtonVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty nextButtonDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty backButtonVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty priceProgressItemVisible = new SimpleBooleanProperty(true);
    private final List<NavigationTarget> childTargets = new ArrayList<>();
    private final ObjectProperty<NavigationTarget> selectedChildTarget = new SimpleObjectProperty<>();
    @Setter
    private boolean animateRightOut = true;
    private final BooleanProperty isBackButtonHighlighted = new SimpleBooleanProperty();

    public TradeWizardModel() {
    }

    @Override
    public NavigationTarget getDefaultNavigationTarget() {
        return NavigationTarget.TRADE_WIZARD_DIRECTION;
    }

    public void reset() {
        currentIndex.set(0);
        nextButtonText.set(null);
        backButtonText.set(null);
        closeButtonVisible.set(false);
        nextButtonVisible.set(true);
        nextButtonDisabled.set(true);
        backButtonVisible.set(true);
        priceProgressItemVisible.set(true);
        childTargets.clear();
        selectedChildTarget.set(null);
        animateRightOut = true;
        isBackButtonHighlighted.set(false);
    }
}
