package de.uniwue.jpp.errorhandling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface OptionalWithMessage<T> {

    boolean isPresent();
    boolean isEmpty();
    T get();
    T orElse(T def);
    T orElseGet(Supplier<? extends T> supplier);
    String getMessage();
    <S> OptionalWithMessage<S> map(Function<T, S> f);
    <S> OptionalWithMessage<S> flatMap(Function<T, OptionalWithMessage<S>> f);
    Optional<String> consume(Consumer<T> c);
    Optional<String> tryToConsume(Function<T, Optional<String>> c);

    static <T> OptionalWithMessage<T> of(T val) {
        return new de.uniwue.jpp.errorhandling.OptionalWithMessageVal<>(val);
    }

    static <T> OptionalWithMessage<T> ofMsg(String msg) {
        return new de.uniwue.jpp.errorhandling.OptionalWithMessageMsg<>(msg);
    }

    static <T> OptionalWithMessage<T> ofNullable(T val, String msg) {
        return val == null ? ofMsg(msg) : of(val);
    }

    static <T> OptionalWithMessage<T> ofOptional(Optional<T> opt, String msg) {
        return opt.map(t -> OptionalWithMessage.of(t)).orElse(OptionalWithMessage.ofMsg(msg));
    }

    static <T> OptionalWithMessage<List<T>> sequence(List<OptionalWithMessage<T>> list) {
        if (list.isEmpty())
            return OptionalWithMessage.of(new ArrayList<>());

        OptionalWithMessage<T> first = list.get(0);
        List<OptionalWithMessage<T>> rest = new ArrayList<>(list);
        rest.remove(0);
        OptionalWithMessage<List<T>> restSequenced = sequence(rest);

        if (first.isPresent() && restSequenced.isPresent())
            return restSequenced.map(l -> Stream.of(List.of(first.get()), l).flatMap(List::stream).collect(Collectors.toList()));
        if (first.isPresent() && !restSequenced.isPresent())
            return restSequenced;
        if (!first.isPresent() && restSequenced.isPresent())
            return OptionalWithMessage.ofMsg(first.getMessage());
        // !first.isPresent() && !restSequenced.isPresent()
        return OptionalWithMessage.ofMsg(first.getMessage() + System.lineSeparator() + restSequenced.getMessage());
    }
}
