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

package bisq.desktop.components.controls.validator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * An abstract class that defines the basic validation functionality for a certain control.
 */
public abstract class ValidatorBase {

    /**
     * This {@link PseudoClass} will be activated when a validation error occurs.
     */
    public static final PseudoClass PSEUDO_CLASS_ERROR = PseudoClass.getPseudoClass("error");

    /**
     * When using {@code Tooltip.install(node, tooltip)}, the given tooltip is stored in the Node's properties
     * under this key.
     *
     * @see Tooltip#install(Node, Tooltip)
     */
    private static final String TOOLTIP_PROP_KEY = "javafx.scene.control.Tooltip";

    /**
     * Default error tooltip style class
     */
    public static final String ERROR_TOOLTIP_STYLE_CLASS = "error-tooltip";

    /**
     * Key used to stash control tooltip upon validation
     */
    private static final String TEMP_TOOLTIP_KEY = "stashed-tootlip";

    /**
     * supported tooltips keys
     */
    private static final Set<String> supportedTooltipKeys = new HashSet<>(
            List.of(
                    "javafx.scene.control.Tooltip"
            )
    );

    /**
     * @param message will be set as the validator's {@link #message}.
     * @see #ValidatorBase()
     */
    public ValidatorBase(String message) {
        this();
        this.setMessage(message);
    }

    /**
     * When creating a new validator you need to define the validation condition by implementing {@link #eval()}.
     * <p>
     */
    public ValidatorBase() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Will validate the source control.
     * <p>
     * Calls {@link #eval()} and then {@link #onEval()}.
     */
    public void validate() {
        eval();
        onEval();
    }

    /**
     * Should evaluate the validation condition and set {@link #hasErrors} to true or false. It should
     * be true when the value is invalid (it has errors) and false when the value is valid (no errors).
     * <p>
     * This method is fired once {@link #validate()} is called.
     */
    protected abstract void eval();

    /**
     * This method will update the source control after evaluating the validation condition (see {@link #eval()}).
     * <p>
     * If the validator isn't "passing" the {@link #PSEUDO_CLASS_ERROR :error} pseudoclass is applied to the
     * {@link #srcControl}.
     * <p>
     * Applies the {@link #PSEUDO_CLASS_ERROR :error} pseudo class and the errorTooltip to
     * the {@link #srcControl}.
     */
    protected void onEval() {
        Node control = getSrcControl();
        boolean invalid = hasErrors.get();
        control.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, invalid);
        Tooltip activeTooltip = getActiveTooltip(control);
        if (invalid) {
            Tooltip errorTooltip = errorTooltipSupplier.get();
            errorTooltip.getStyleClass().add(ERROR_TOOLTIP_STYLE_CLASS);
            errorTooltip.setText(getMessage());
            install(control, activeTooltip, errorTooltip);
        } else {
            Tooltip orgTooltip = (Tooltip) control.getProperties().remove(TEMP_TOOLTIP_KEY);
            install(control, activeTooltip, orgTooltip);
        }
    }

    private final Tooltip getActiveTooltip(Node node) {
        Tooltip tooltip = null;
        for (String key : supportedTooltipKeys) {
            tooltip = (Tooltip) node.getProperties().get(key);
            if (tooltip != null) {
                break;
            }
        }
        return tooltip;
    }

    private void install(Node node, Tooltip oldVal, Tooltip newVal) {
        // stash old tooltip if it's not error tooltip
        if (oldVal != null && !oldVal.getStyleClass().contains(ERROR_TOOLTIP_STYLE_CLASS)) {
            node.getProperties().put(TEMP_TOOLTIP_KEY, oldVal);
        }
        if (node instanceof Control) {
            // uninstall
            if (oldVal != null) {
                if (newVal == null) {
                    ((Control) node).setTooltip(newVal);
                    return;
                }
            }
            // install
            ((Control) node).setTooltip(newVal);
        } else {
            uninstall(node, oldVal);
            install(node, newVal);
        }
    }

    private void uninstall(Node node, Tooltip tooltip) {
        Tooltip.uninstall(node, tooltip);
    }

    private void install(Node node, Tooltip tooltip) {
        if (tooltip == null) {
            return;
        }
        Tooltip.install(node, tooltip);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The {@link Control}/{@link Node} that the validator is checking the value of.
     * <p>
     * Supports {@link Node}s because not all things that need validating are {@link Control}s.
     */
    protected SimpleObjectProperty<Node> srcControl = new SimpleObjectProperty<>();

    /**
     * @see #srcControl
     */
    public void setSrcControl(Node srcControl) {
        this.srcControl.set(srcControl);
    }

    /**
     * @see #srcControl
     */
    public Node getSrcControl() {
        return this.srcControl.get();
    }

    /**
     * @see #srcControl
     */
    public ObjectProperty<Node> srcControlProperty() {
        return this.srcControl;
    }

    /**
     * Tells whether the validator is "passing" or not.
     * <p>
     * In a validator's implementation of {@link #eval()}, if the value the validator is checking is invalid, it should
     * set this to <em>true</em>. If the value is <em>valid</em>, it should set this to <em>false</em>.
     * <p>
     * When <em>hasErrors</em> is true, the validator will automatically apply the {@link #PSEUDO_CLASS_ERROR :error}
     * pseudoclass to the {@link #srcControl}; the {@link #srcControl} will also have a {@link Tooltip} containing the
     * {@link #message} applied to it.
     */
    protected ReadOnlyBooleanWrapper hasErrors = new ReadOnlyBooleanWrapper(false);

    /**
     * @see #hasErrors
     */
    public boolean getHasErrors() {
        return hasErrors.get();
    }

    /**
     * @see #hasErrors
     */
    public ReadOnlyBooleanProperty hasErrorsProperty() {
        return hasErrors.getReadOnlyProperty();
    }

    private Supplier<Tooltip> errorTooltipSupplier = Tooltip::new;

    public Supplier<Tooltip> getErrorTooltipSupplier() {
        return errorTooltipSupplier;
    }

    public void setErrorTooltipSupplier(Supplier<Tooltip> errorTooltipSupplier) {
        this.errorTooltipSupplier = errorTooltipSupplier;
    }

    /**
     * The error message to display when the validator is <em>not</em> "passing."
     * <p>
     * When {@link #hasErrors} is true, this message is displayed near the {@link #srcControl} (usually below);
     * it's also displayed in a {@link Tooltip} applied to the {@link #srcControl}.
     */
    protected SimpleStringProperty message = new SimpleStringProperty();

    /**
     * @see #message
     */
    public void setMessage(String msg) {
        this.message.set(msg);
    }

    /**
     * @see #message
     */
    public String getMessage() {
        return this.message.get();
    }

    /**
     * @see #message
     */
    public StringProperty messageProperty() {
        return this.message;
    }


    /***** Icon *****/
    protected SimpleObjectProperty<Supplier<Node>> iconSupplier = new SimpleObjectProperty<Supplier<Node>>();

    public void setIconSupplier(Supplier<Node> icon) {
        this.iconSupplier.set(icon);
    }

    public SimpleObjectProperty<Supplier<Node>> iconSupplierProperty() {
        return this.iconSupplier;
    }

    public Supplier<Node> getIconSupplier() {
        return iconSupplier.get();
    }

    /**
     * @param icon
     */
    public void setIcon(Node icon) {
        iconSupplier.set(() -> icon);
    }

    public Node getIcon() {
        if (iconSupplier.get() == null) {
            return null;
        }
        Node icon = iconSupplier.get().get();
        if (icon != null) {
            icon.getStyleClass().add("error-icon");
        }
        return icon;
    }
}
