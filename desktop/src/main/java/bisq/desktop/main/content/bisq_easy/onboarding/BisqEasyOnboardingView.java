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

package bisq.desktop.main.content.bisq_easy.onboarding;

import bisq.desktop.common.Icons;
import bisq.desktop.common.utils.ImageUtil;
import bisq.desktop.common.view.View;
import bisq.desktop.components.containers.Spacer;
import bisq.desktop.components.controls.BisqTooltip;
import bisq.i18n.Res;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

@Slf4j
public class BisqEasyOnboardingView extends View<GridPane, BisqEasyOnboardingModel, BisqEasyOnboardingController> {
    private static final int PADDING = 20;

    private Button watchVideoButton, openTradeGuideButton;
    private final Button startTradingButton, openChatButton;
    private ImageView videoImage;
    private Subscription videoSeenPin;

    public BisqEasyOnboardingView(BisqEasyOnboardingModel model, BisqEasyOnboardingController controller) {
        super(new GridPane(), model, controller);

        root.setPadding(new Insets(30, 0, -44, 0));
        root.setHgap(PADDING);
        root.setVgap(10);
        root.setMinWidth(780);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        root.getColumnConstraints().addAll(col1, col2);

        addTopWidgetBox();

        GridPane gridPane = getWidgetBoxGridPane();
        root.add(gridPane, 0, 2, 2, 1);

        startTradingButton = new Button(Res.get("bisqEasy.onboarding.left.button"));
        fillSmallBox(gridPane,
                0,
                startTradingButton,
                Res.get("bisqEasy.onboarding.left.headline"),
                "bisq-easy",
                Res.get("bisqEasy.onboarding.left.info")
        );

        openChatButton = new Button(Res.get("bisqEasy.onboarding.right.button"));
        fillSmallBox(gridPane,
                1,
                openChatButton,
                Res.get("bisqEasy.onboarding.right.headline"),
                "fiat-btc",
                Res.get("bisqEasy.onboarding.right.info")
        );
    }

    @Override
    protected void onViewAttached() {
        videoSeenPin = EasyBind.subscribe(model.getVideoSeen(), videoSeen -> {
            startTradingButton.setDefaultButton(videoSeen);
            watchVideoButton.setDefaultButton(!videoSeen);
        });

        startTradingButton.setOnAction(e -> controller.onOpenTradeWizard());
        openChatButton.setOnAction(e -> controller.onOpenOfferbook());
        openTradeGuideButton.setOnAction(e -> controller.onOpenTradeGuide());
        watchVideoButton.setOnMouseClicked(e -> controller.onPlayVideo());
        videoImage.setOnMouseClicked(e -> controller.onPlayVideo());
    }

    @Override
    protected void onViewDetached() {
        videoSeenPin.unsubscribe();

        startTradingButton.setOnAction(null);
        openChatButton.setOnAction(null);
        openTradeGuideButton.setOnAction(null);
        watchVideoButton.setOnAction(null);
        videoImage.setOnMouseClicked(null);
    }

    private void addTopWidgetBox() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(48);
        gridPane.setVgap(15);
        gridPane.getStyleClass().add("bisq-easy-onboarding-big-box");
        gridPane.setPadding(new Insets(30, 48, 44, 48));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(col1, col2);
        root.add(gridPane, 0, 0, 2, 1);

        Label headlineLabel = new Label(Res.get("bisqEasy.onboarding.top.headline"));
        headlineLabel.getStyleClass().add("bisq-easy-onboarding-big-box-headline");
        headlineLabel.setWrapText(true);
        gridPane.add(headlineLabel, 0, 0, 2, 1);

        HBox line1 = getBulletPoint(Res.get("bisqEasy.onboarding.top.content1"), "thumbs-up");
        HBox line2 = getBulletPoint(Res.get("bisqEasy.onboarding.top.content2"), "onboarding-2-payment");
        HBox line3 = getBulletPoint(Res.get("bisqEasy.onboarding.top.content3"), "onboarding-2-chat");
        VBox vBox = new VBox(15, Spacer.fillVBox(), line1, line2, line3, Spacer.fillVBox());
        gridPane.add(vBox, 0, 1);

        videoImage = ImageUtil.getImageViewById("video");
        videoImage.setCursor(Cursor.HAND);
        Tooltip.install(videoImage, new BisqTooltip(Res.get("bisqEasy.onboarding.watchVideo.tooltip")));
        GridPane.setHalignment(videoImage, HPos.CENTER);
        gridPane.add(videoImage, 1, 1);

        openTradeGuideButton = new Button(Res.get("bisqEasy.onboarding.openTradeGuide"));
        openTradeGuideButton.getStyleClass().add("super-large-button");
        openTradeGuideButton.setMaxWidth(Double.MAX_VALUE);
        GridPane.setMargin(openTradeGuideButton, new Insets(10, 0, 0, 0));
        gridPane.add(openTradeGuideButton, 0, 2);

        Label icon = Icons.getIcon(AwesomeIcon.YOUTUBE_PLAY, "26");
        watchVideoButton = new Button(Res.get("bisqEasy.onboarding.watchVideo"), icon);
        watchVideoButton.setGraphicTextGap(10);
        watchVideoButton.getStyleClass().add("super-large-button");
        watchVideoButton.setMaxWidth(Double.MAX_VALUE);
        watchVideoButton.setTooltip(new BisqTooltip(Res.get("bisqEasy.onboarding.watchVideo.tooltip")));
        GridPane.setMargin(watchVideoButton, new Insets(10, 0, 0, 0));
        gridPane.add(watchVideoButton, 1, 2);
    }

    private GridPane getWidgetBoxGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(116);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(36, 48, 44, 48));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(col1, col2);
        return gridPane;
    }

    private void fillSmallBox(GridPane gridPane,
                              int columnIndex,
                              Button button,
                              String headline,
                              String headlineImageId,
                              String info) {
        Pane group = new Pane();
        group.getStyleClass().add("bisq-easy-onboarding-small-box");
        if (columnIndex == 0) {
            GridPane.setMargin(group, new Insets(-36, -48, -44, -48));
        } else {
            GridPane.setMargin(group, new Insets(-36, -48, -44, -48));
        }
        gridPane.add(group, columnIndex, 0, 1, 3);

        Label headlineLabel = new Label(headline, ImageUtil.getImageViewById(headlineImageId));
        headlineLabel.setGraphicTextGap(16.0);
        headlineLabel.getStyleClass().add("bisq-easy-onboarding-small-box-headline");
        headlineLabel.setWrapText(true);
        GridPane.setMargin(headlineLabel, new Insets(0, 0, 10, 0));
        gridPane.add(headlineLabel, columnIndex, 0);

        Label infoLabel = new Label(info);
        infoLabel.getStyleClass().add("bisq-easy-onboarding-small-box-text");
        infoLabel.setWrapText(true);
        gridPane.add(infoLabel, columnIndex, 1);

        button.getStyleClass().add("large-button");
        button.setMaxWidth(Double.MAX_VALUE);
        GridPane.setMargin(button, new Insets(20, 0, 0, 0));
        gridPane.add(button, columnIndex, 2);
    }

    private HBox getBulletPoint(String text, String imageId) {
        Label label = new Label(text);
        label.getStyleClass().add("bisq-easy-onboarding-big-box-bullet-point");
        label.setWrapText(true);
        ImageView bulletPoint = ImageUtil.getImageViewById(imageId);
        HBox.setMargin(bulletPoint, new Insets(-2, 0, 0, 4));
        HBox hBox = new HBox(15, bulletPoint, label);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }
}
