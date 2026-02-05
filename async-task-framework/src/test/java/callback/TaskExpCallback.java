package callback;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskExpCallback implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) {
        log.warn("Receive task exception", throwable);
    }
}
