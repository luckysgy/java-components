package com.concise.component.core.event;

import org.springframework.context.ApplicationEvent;

/**
 * 事件类需要继承org.springframework.context.ApplicationEvent，这样发布的事件才能被Spring所识别
 * @author shenguangyang
 * @date 2022-01-01 17:29
 */
public abstract class BaseEvent<T> extends ApplicationEvent {

    private static final long serialVersionUID = 895628808370649881L;

    protected T eventData;

    public BaseEvent(Object source, T eventData){
        super(source);
        this.eventData = eventData;
    }

    public BaseEvent(T eventData){
        super(eventData);
        this.eventData = eventData;
    }
    
    public T getEventData() {
        return eventData;
    }
    public void setEventData(T eventData) {
        this.eventData = eventData;
    }
}

