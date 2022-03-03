package org.bju;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Pair<T, K> {
    @NonNull public T key;
    @NonNull public K value;
}
