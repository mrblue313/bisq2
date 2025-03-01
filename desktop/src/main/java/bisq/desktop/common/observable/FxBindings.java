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

package bisq.desktop.common.observable;

import bisq.common.observable.Observable;
import bisq.common.observable.Pin;
import bisq.common.observable.ReadOnlyObservable;
import bisq.common.observable.collection.ObservableArray;
import bisq.common.observable.collection.ObservableSet;
import bisq.desktop.common.threading.UIThread;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class FxBindings {
    public static <S, T> ObservableListBindings<S, T> bind(ObservableList<T> observer) {
        return new ObservableListBindings<>(observer);
    }

    public static <S> ObservablePropertyBindings<S> bind(ObjectProperty<S> observer) {
        return new ObservablePropertyBindings<>(observer);
    }

    public static LongPropertyBindings bind(LongProperty observer) {
        return new LongPropertyBindings(observer);
    }

    public static LongBiDirPropertyBindings bindBiDir(LongProperty observer) {
        return new LongBiDirPropertyBindings(observer);
    }

    public static DoublePropertyBindings bind(DoubleProperty observer) {
        return new DoublePropertyBindings(observer);
    }

    public static IntegerPropertyBindings bind(IntegerProperty observer) {
        return new IntegerPropertyBindings(observer);
    }

    public static BooleanPropertyBindings bind(BooleanProperty observer) {
        return new BooleanPropertyBindings(observer);
    }

    public static BooleanBiDirPropertyBindings bindBiDir(BooleanProperty observer) {
        return new BooleanBiDirPropertyBindings(observer);
    }


    public static <S> ObjectBiDirPropertyBindings<S> bindBiDir(ObjectProperty<S> observer) {
        return new ObjectBiDirPropertyBindings<>(observer);
    }

    public static StringPropertyBindings bind(StringProperty observer) {
        return new StringPropertyBindings(observer);
    }

    public static <S> Pin subscribe(Observable<S> observable, Consumer<S> consumer) {
        return observable.addObserver(e -> UIThread.run(() -> consumer.accept(e)));
    }


    public static class ObservableListBindings<S, T> {
        private final ObservableList<T> observableList;

        // In case there is no map function provided we require that target type is the same as the source type. 
        @SuppressWarnings("unchecked")
        private Function<S, T> mapFunction = e -> (T) e;

        private ObservableListBindings(ObservableList<T> observableList) {
            this.observableList = observableList;
        }

        public ObservableListBindings<S, T> map(Function<S, T> mapFunction) {
            this.mapFunction = mapFunction;
            return this;
        }

        // We support currently only JavaFX ObservableList even if the source is a set.
        public Pin to(ObservableSet<S> observable) {
            return observable.addCollectionChangeMapper(observableList, mapFunction, UIThread::run);
        }

        public Pin to(ObservableArray<S> observable) {
            return observable.addCollectionChangeMapper(observableList, mapFunction, UIThread::run);
        }
    }

    public static final class ObservablePropertyBindings<S> {
        private final ObjectProperty<S> observer;

        public ObservablePropertyBindings(ObjectProperty<S> observer) {
            this.observer = observer;
        }

        public Pin to(Observable<S> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }

        public Pin to(ReadOnlyObservable<S> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }
    }


    public static final class LongPropertyBindings {
        private final LongProperty observer;

        public LongPropertyBindings(LongProperty observer) {
            this.observer = observer;
        }

        public Pin to(Observable<Long> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }

        public Pin to(ReadOnlyObservable<Long> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }
    }


    public static final class DoublePropertyBindings {
        private final DoubleProperty observer;

        public DoublePropertyBindings(DoubleProperty observer) {
            this.observer = observer;
        }

        public Pin to(Observable<Double> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }

        public Pin to(ReadOnlyObservable<Double> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }
    }


    public static final class IntegerPropertyBindings {
        private final IntegerProperty observer;

        public IntegerPropertyBindings(IntegerProperty observer) {
            this.observer = observer;
        }

        public Pin to(Observable<Integer> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }

        public Pin to(ReadOnlyObservable<Integer> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }
    }


    public static final class BooleanPropertyBindings {
        private final BooleanProperty observer;

        public BooleanPropertyBindings(BooleanProperty observer) {
            this.observer = observer;
        }

        public Pin to(Observable<Boolean> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }

        public Pin to(ReadOnlyObservable<Boolean> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }
    }

    public static final class StringPropertyBindings {
        private final StringProperty observer;

        public StringPropertyBindings(StringProperty observer) {
            this.observer = observer;
        }

        public Pin to(Observable<String> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }

        public Pin to(ReadOnlyObservable<String> observable) {
            return observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
        }
    }

    public static final class BooleanBiDirPropertyBindings {
        private final BooleanProperty observer;

        public BooleanBiDirPropertyBindings(BooleanProperty observer) {
            this.observer = observer;
        }

        public Pin to(Observable<Boolean> observable) {
            ChangeListener<Boolean> listener = (o, oldValue, newValue) -> observable.set(newValue);
            observer.addListener(listener);
            Pin pin = observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
            return () -> {
                observer.removeListener(listener);
                pin.unbind();
            };
        }
    }

    public static final class LongBiDirPropertyBindings {
        private final LongProperty observer;

        public LongBiDirPropertyBindings(LongProperty observer) {
            this.observer = observer;
        }

        public Pin to(Observable<Long> observable) {
            ChangeListener<Number> listener = (o, oldValue, newValue) -> observable.set((Long) newValue);
            observer.addListener(listener);
            Pin pin = observable.addObserver(e -> UIThread.run(() -> observer.set(e)));
            return () -> {
                observer.removeListener(listener);
                pin.unbind();
            };
        }
    }


    public static final class ObjectBiDirPropertyBindings<S> {
        private final ObjectProperty<S> observer;

        public ObjectBiDirPropertyBindings(ObjectProperty<S> observer) {
            this.observer = observer;
        }

        public Pin to(Observable<S> observable) {
            ChangeListener<S> listener = (o, oldValue, newValue) -> {
                observable.set(newValue);
            };
            observer.addListener(listener);
            Pin pin = observable.addObserver(e -> UIThread.run(() -> {
                observer.set(e);
            }));
            return () -> {
                observer.removeListener(listener);
                pin.unbind();
            };
        }
    }
}