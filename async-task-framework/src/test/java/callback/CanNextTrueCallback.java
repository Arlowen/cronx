package callback;

import java.util.function.BooleanSupplier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CanNextTrueCallback implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        log.warn("Judge can next result is true");
        return true;
    }
}
