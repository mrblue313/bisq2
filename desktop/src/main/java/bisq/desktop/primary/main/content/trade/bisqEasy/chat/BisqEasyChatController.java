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

package bisq.desktop.primary.main.content.trade.bisqEasy.chat;

import bisq.application.DefaultApplicationService;
import bisq.chat.ChannelKind;
import bisq.chat.channel.Channel;
import bisq.chat.channel.PrivateChannel;
import bisq.chat.message.ChatMessage;
import bisq.chat.trade.TradeChannelSelectionService;
import bisq.chat.trade.priv.PrivateTradeChannel;
import bisq.chat.trade.priv.PrivateTradeChannelService;
import bisq.chat.trade.pub.PublicTradeChannel;
import bisq.chat.trade.pub.PublicTradeChannelService;
import bisq.common.currency.Market;
import bisq.common.observable.Pin;
import bisq.desktop.common.observable.FxBindings;
import bisq.desktop.common.view.Controller;
import bisq.desktop.common.view.Navigation;
import bisq.desktop.common.view.NavigationTarget;
import bisq.desktop.components.overlay.Popup;
import bisq.desktop.primary.main.content.chat.ChatController;
import bisq.desktop.primary.main.content.chat.channels.PublicTradeChannelSelection;
import bisq.desktop.primary.main.content.components.MarketImageComposition;
import bisq.desktop.primary.main.content.trade.bisqEasy.chat.guide.TradeGuideController;
import bisq.desktop.primary.overlay.createOffer.CreateOfferController;
import bisq.i18n.Res;
import bisq.settings.SettingsService;
import bisq.support.MediationService;
import bisq.user.identity.UserIdentity;
import bisq.user.role.RoleRegistrationService;
import bisq.user.role.RoleType;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.easybind.EasyBind;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class BisqEasyChatController extends ChatController<BisqEasyChatView, BisqEasyChatModel> {
    private final PublicTradeChannelService publicTradeChannelService;
    private final TradeChannelSelectionService tradeChannelSelectionService;
    private final SettingsService settingsService;
    private final MediationService mediationService;
    private final RoleRegistrationService roleRegistrationService;
    private final PrivateTradeChannelService privateTradeChannelService;
    private PublicTradeChannelSelection publicTradeChannelSelection;
    private final Set<Pin> newMediationRequestPins = new HashSet<>();
    private Pin offerOnlySettingsPin, myRegistrationsPin;

    public BisqEasyChatController(DefaultApplicationService applicationService) {
        super(applicationService, ChannelKind.TRADE, NavigationTarget.BISQ_EASY_CHAT);

        publicTradeChannelService = chatService.getPublicTradeChannelService();
        tradeChannelSelectionService = chatService.getTradeChannelSelectionService();
        settingsService = applicationService.getSettingsService();
        mediationService = applicationService.getSupportService().getMediationService();
        roleRegistrationService = applicationService.getUserService().getRoleRegistrationService();
        privateTradeChannelService = applicationService.getChatService().getPrivateTradeChannelService();
    }

    @Override
    public void onActivate() {
        super.onActivate();

        notificationSettingSubscription = EasyBind.subscribe(channelSidebar.getSelectedNotificationType(),
                value -> {
                    Channel<? extends ChatMessage> channel = tradeChannelSelectionService.getSelectedChannel().get();
                    if (channel != null) {
                        publicTradeChannelService.setNotificationSetting(channel, value);
                    }
                });
        selectedChannelPin = tradeChannelSelectionService.getSelectedChannel().addObserver(this::handleChannelChange);
        offerOnlySettingsPin = FxBindings.bindBiDir(model.getOfferOnly()).to(settingsService.getOffersOnly());


        myRegistrationsPin = roleRegistrationService.getMyRegistrations().addChangedListener(() -> {
            roleRegistrationService.getMyRegistrations().stream()
                    .filter(data -> data.getRoleType() == RoleType.MEDIATOR)
                    .filter(data -> userIdentityService.findUserIdentity(data.getUserProfile().getId()).isPresent()) // double check that we own that profile
                    .forEach(data -> {
                        Pin newMediationRequestPin = mediationService.getNewMediationRequest().addObserver(mediationRequest -> {
                            if (mediationRequest == null) {
                                return;
                            }

                            UserIdentity myUserIdentity = userIdentityService.findUserIdentity(data.getUserProfile().getId()).orElseThrow();

                            PrivateTradeChannel channel = privateTradeChannelService.mediatorCreatesNewChannel(
                                    mediationRequest.getRequester(),
                                    mediationRequest.getPeer(),
                                    Optional.of(myUserIdentity.getUserProfile()),
                                    myUserIdentity);
                            tradeChannelSelectionService.selectChannel(channel);

                            privateTradeChannelService.sendPrivateChatMessage("msg to peer",
                                    Optional.empty(),
                                    channel,
                                    myUserIdentity,
                                    mediationRequest.getPeer());
                            privateTradeChannelService.sendPrivateChatMessage("msg to requester",
                                    Optional.empty(),
                                    channel,
                                    myUserIdentity,
                                    mediationRequest.getRequester());
                        });
                        newMediationRequestPins.add(newMediationRequestPin);
                    });
        });
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();

        offerOnlySettingsPin.unbind();
        myRegistrationsPin.unbind();
        newMediationRequestPins.forEach(Pin::unbind);
        resetSelectedChildTarget();
    }

    @Override
    public void createComponents() {
        publicTradeChannelSelection = new PublicTradeChannelSelection(applicationService);
    }

    @Override
    public BisqEasyChatModel getChatModel(ChannelKind channelKind) {
        return new BisqEasyChatModel(channelKind);
    }

    @Override
    public BisqEasyChatView getChatView() {
        return new BisqEasyChatView(model,
                this,
                publicTradeChannelSelection.getRoot(),
                privateChannelSelection.getRoot(),
                chatMessagesComponent.getRoot(),
                channelSidebar.getRoot());
    }

    @Override
    protected void handleChannelChange(Channel<? extends ChatMessage> channel) {
        super.handleChannelChange(channel);

        if (channel == null) {
            return;
        }
        if (channel instanceof PrivateTradeChannel) {
            applyPeersIcon((PrivateChannel<?>) channel);

            publicTradeChannelSelection.deSelectChannel();
            model.getActionButtonText().set(Res.get("bisqEasy.openDispute"));

            Navigation.navigateTo(NavigationTarget.TRADE_GUIDE);
        } else {
            resetSelectedChildTarget();
            model.getActionButtonText().set(Res.get("createOffer"));
            privateChannelSelection.deSelectChannel();

            Market market = ((PublicTradeChannel) channel).getMarket();
            StackPane marketsImage = MarketImageComposition.imageBoxForMarket(
                    market.getBaseCurrencyCode().toLowerCase(),
                    market.getQuoteCurrencyCode().toLowerCase()).getFirst();
            model.getChannelIcon().set(marketsImage);
        }
    }

    @Override
    protected Optional<? extends Controller> createController(NavigationTarget navigationTarget) {
        switch (navigationTarget) {
            case TRADE_GUIDE: {
                return Optional.of(new TradeGuideController(applicationService));
            }

            default: {
                return Optional.empty();
            }
        }
    }

    void onActionButtonClicked() {
        Channel<? extends ChatMessage> channel = model.getSelectedChannel().get();
        if (channel instanceof PrivateTradeChannel) {
            PrivateTradeChannel privateTradeChannel = (PrivateTradeChannel) channel;
            mediationService.requestMediation(privateTradeChannel.getMyProfile(), privateTradeChannel.getPeer());
            new Popup().headLine("bisqEasy.requestMediation.popup.headline")
                    .feedback(Res.get("bisqEasy.requestMediation.popup.msg")).show();
        } else {
            Navigation.navigateTo(NavigationTarget.CREATE_OFFER, new CreateOfferController.InitData(false));
        }
    }
}
