package io.cronx.web.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import io.cronx.web.annotation.UnifiedPostConstruct;
import io.cronx.web.annotation.UnifiedPostConstructOrder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnifiedPostConstructUtils {

    public static void doPostConstruct(ApplicationContext context) throws Exception {
        Map<String, UnifiedPostConstruct> postConstructs = context.getBeansOfType(UnifiedPostConstruct.class);

        List<UnifiedPostConstructWrap> postConstructList = new ArrayList<>();
        postConstructs.forEach((beanName, bean) -> {
            Class<?> pcClass = context.getType(beanName);
            pcClass = ClassUtils.getUserClass(pcClass);
            UnifiedPostConstructOrder order = pcClass.getAnnotation(UnifiedPostConstructOrder.class);
            if (order == null) {
                postConstructList.add(new UnifiedPostConstructWrap(0, bean, pcClass));
            } else {
                postConstructList.add(new UnifiedPostConstructWrap(order.value(), bean, pcClass));
            }
        });
        postConstructList.sort(Comparator.comparingInt(UnifiedPostConstructWrap::getOrder));

        for (UnifiedPostConstruct postConstruct : postConstructList) {
            postConstruct.init();
        }
    }

    @Slf4j
    static class UnifiedPostConstructWrap implements UnifiedPostConstruct {

        private final int                  order;
        private final Class<?>             targetType;
        private final UnifiedPostConstruct target;

        public UnifiedPostConstructWrap(int order, UnifiedPostConstruct target, Class<?> targetType){
            this.order = order;
            this.target = target;
            this.targetType = targetType;
        }

        public int getOrder() { return order; }

        @Override
        public void init() throws Exception {
            this.target.init();
            log.info(this.targetType.getSimpleName() + " inited.");
        }

        @Override
        public String toString() {
            return targetType.getSimpleName() + ", order " + this.order;
        }
    }
}
